/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.IO.Shaders.ShaderProgram;
import Reika.DragonAPI.Instantiable.CubePoints;
import Reika.DragonAPI.Instantiable.Effects.ReikaModelledBreakFX;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileModel;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaRenderHelper extends DragonAPICore {

	private static final RenderBlocks rb = new RenderBlocks();

	private static boolean entityLighting;
	private static boolean generalLighting;

	private static ScratchFramebuffer tempBuffer;

	public static enum RenderDistance {
		FAR(),
		NORMAL(),
		SHORT(),
		TINY();
		private static final RenderDistance[] list = values();
	}

	public static RenderDistance getRenderDistance() {
		float r = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
		if (r > 8) {
			return RenderDistance.FAR;
		}
		else if (r > 4) {
			return RenderDistance.NORMAL;
		}
		else if (r > 2) {
			return RenderDistance.SHORT;
		}
		else {
			return RenderDistance.TINY;
		}
	}

	/** Converts a biome to a color multiplier (for use in things like leaf textures).
	 * Args: World, x, z, material (grass, water, etc), bit */
	public static float biomeToColorMultiplier(World world, int x, int y, int z, Material mat, int bit) {
		int[] color = ReikaBiomeHelper.biomeToRGB(world, x, y, z, mat);
		float mult = ReikaColorAPI.RGBtoColorMultiplier(color, bit);
		return mult;
	}

	/** Renders a flat circle in the world. Args: radius, center x,y,z, RGBA, angle step */
	public static void renderCircle(double r, double x, double y, double z, int rgba, int step) {
		//GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = Tessellator.instance;
		if (var5.isDrawing)
			var5.draw();
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA_I(rgba, rgba >> 24 & 255);
		for (int i = 0; i < 360; i += step) {
			double a = Math.toRadians(i);
			var5.addVertex(x+r*Math.cos(a), y, z+r*Math.sin(a));
		}
		var5.draw();
		//GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** Renders a vertical-plane circle in the world. Args: radius, center x,y,z, RGBA, phi, angle step */
	public static void renderVCircle(double r, double x, double y, double z, int rgba, double phi, int step) {
		//GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = Tessellator.instance;
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA_I(rgba, rgba >> 24 & 255);
		for (int i = 0; i < 360; i += step) {
			int sign = 1;
			double h = r*Math.cos(ReikaPhysicsHelper.degToRad(i));
			if (i >= 180)
				sign = -1;
			var5.addVertex(x-Math.sin(Math.toRadians(phi))*(sign)*(Math.sqrt(r*r-h*h)), y+r*Math.cos(Math.toRadians(i)), z+r*Math.sin(Math.toRadians(i))*Math.cos(Math.toRadians(phi)));
		}
		var5.draw();
		//GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** Renders a line between two points in the world. Args: Start xyz, End xyz, rgb */
	public static void renderLine(double x1, double y1, double z1, double x2, double y2, double z2, int rgba) {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = Tessellator.instance;
		if (var5.isDrawing)
			var5.draw();
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA_I(rgba & 0xffffff, rgba >> 24 & 255);
		var5.addVertex(x1, y1, z1);
		var5.addVertex(x2, y2, z2);
		var5.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void renderTube(double x1, double y1, double z1, double x2, double y2, double z2, int c1, int c2, double r1, double r2, int sides) {

		Tessellator v5 = Tessellator.instance;

		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;

		GL11.glPushMatrix();

		GL11.glTranslated(x1, y1, z1);

		//ReikaJavaLibrary.pConsole(x1+","+y1+","+z1+"  >  "+x2+","+y2+","+z2);

		double f7 = Math.sqrt(dx*dx+dz*dz);
		double f8 = Math.sqrt(dx*dx+dy*dy+dz*dz);
		double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI-90;
		double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI-90;
		GL11.glRotated(ang1, 0, 1, 0);
		GL11.glRotated(ang2, 1, 0, 0);

		v5.startDrawing(GL11.GL_TRIANGLE_STRIP);
		v5.setBrightness(240);
		for (int i = 0; i <= sides; i++) {
			double f11a = r1*Math.sin(i % sides * Math.PI * 2 / sides) * 0.75;
			double f12a = r1*Math.cos(i % sides * Math.PI * 2 / sides) * 0.75;
			double f11b = r2*Math.sin(i % sides * Math.PI * 2 / sides) * 0.75;
			double f12b = r2*Math.cos(i % sides * Math.PI * 2 / sides) * 0.75;
			double f13 = i % sides * 1 / sides;
			v5.setColorRGBA_I(c1 & 0xffffff, c1 >> 24 & 255);
			v5.addVertex(f11a, f12a, 0);
			v5.setColorRGBA_I(c2 & 0xffffff, c2 >> 24 & 255);
			v5.addVertex(f11b, f12b, f8);
		}

		v5.draw();

		GL11.glPopMatrix();
	}

	public static void disableLighting() {
		Minecraft.getMinecraft().entityRenderer.disableLightmap(1);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	public static void enableLighting() {
		enableEntityLighting();
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public static void disableEntityLighting() {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit); //block/sky light grid image
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void enableEntityLighting() {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void pushTESRLightingState() {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		entityLighting = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		generalLighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LIGHTING);
		if (entityLighting)
			disableEntityLighting();
	}

	public static void popTESRLightingState() {
		if (entityLighting)
			enableEntityLighting();
		if (generalLighting)
			GL11.glEnable(GL11.GL_LIGHTING);
	}

	/** Prepare for drawing primitive geometry by disabling all lighting and textures. Args: Is alpha going to be used */
	public static void prepareGeoDraw(boolean alpha) {
		disableLighting();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if (alpha)
			GL11.glEnable(GL11.GL_BLEND);
	}

	public static void exitGeoDraw() {
		enableLighting();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	/** Renders a rectangle in-world. Args: r,g,b,a, Start x,y,z, End x,y,z */
	public static void renderRectangle(int r, int g, int b, int a, double x1, double y1, double z1, double x2, double y2, double z2) {
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setColorRGBA(r, g, b, a);
		v5.addVertex(x1, y1, z1);
		v5.addVertex(x2, y1, z2);
		v5.addVertex(x2, y2, z2);
		v5.addVertex(x1, y2, z1);
		v5.draw();
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockDestroyEffects!
	 * Args: Base path (contains TE textures, world, x, y, z, Block, EffectRenderer, Allowed Texture Regions<br><br>
	 *
	 * Explanation of Allowed Regions-Expects a list of size-4 double arrays, whose elements are as follows:<br>
	 * allowed[0]: Lower X-coordinate of allowed region in texture file (left)<br>
	 * allowed[1]: Lower Y-coordinate of allowed region in texture file (top)<br>
	 * allowed[2]: Upper X-coordinate of allowed region in texture file (right)<br>
	 * allowed[3]: Upper Y-coordinate of allowed region in texture file (bottom)<br>
	 *
	 * Note that these are referenced to a whole image, so [0,0,1,1] would be the entire image file.
	 * @Author Reika
	 * */
	public static boolean addModelledBlockParticles(String basedir, World world, int x, int y, int z, Block b, EffectRenderer eff, List<double[]> allowedRegions, Class mod) {
		String name = null;
		if (world.getBlock(x, y, z) == b) {
			TileEntity t = world.getTileEntity(x, y, z);
			if (t instanceof RenderFetcher) {
				RenderFetcher te = (RenderFetcher)t;
				TextureFetcher r = te.getRenderer();
				if (r != null)
					name = r.getImageFileName(te);
			}
		}
		if (name == null)
			return false;
		String file = basedir+name;
		for (int i = 0; i < 48; i++) {
			int k = rand.nextInt(allowedRegions.size());
			double[] p = allowedRegions.get(k);
			double px = p[0]+rand.nextDouble()*(p[2]-p[0]);
			double py = p[1]+rand.nextDouble()*(p[3]-p[1]);
			double overx = px+ReikaModelledBreakFX.pw-p[2];
			if (overx > 0)
				px -= overx;
			double overy = py+ReikaModelledBreakFX.pw-p[2];
			if (overy > 0)
				py -= overy;
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), file, px, py, mod));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockHitEffects!
	 * Args: Base path (contains TE textures, world, MovingObjectPosition, Block, EffectRenderer, Allowed Texture Regions <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticles(String basedir, World world, MovingObjectPosition mov, Block b, EffectRenderer eff, List<double[]> allowedRegions, Class mod) {
		if (mov == null)
			return false;
		int x = mov.blockX;
		int y = mov.blockY;
		int z = mov.blockZ;
		String name = null;
		if (world.getBlock(x, y, z) == b) {
			TileEntity t = world.getTileEntity(x, y, z);
			if (t instanceof RenderFetcher) {
				RenderFetcher te = (RenderFetcher)t;
				TextureFetcher r = te.getRenderer();
				if (r != null)
					name = r.getImageFileName(te);
			}
		}
		int j = 1+rand.nextInt(2);
		String file = basedir+name;
		for (int i = 0; i < j; i++) {
			int k = rand.nextInt(allowedRegions.size());
			double[] p = allowedRegions.get(k);
			double px = p[0]+rand.nextDouble()*(p[2]-p[0]);
			double py = p[1]+rand.nextDouble()*(p[3]-p[1]);
			double overx = px+ReikaModelledBreakFX.pw-p[2];
			if (overx > 0)
				px -= overx;
			double overy = py+ReikaModelledBreakFX.pw-p[2];
			if (overy > 0)
				py -= overy;
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), file, px, py, mod));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockDestroyEffects!
	 * Args: Texture Path, world, x, y, z, Block, EffectRenderer, Allowed Texture Regions <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticlesDirect(String texture, World world, int x, int y, int z, Block b, EffectRenderer eff, List<double[]> allowedRegions, Class mod) {
		for (int i = 0; i < 48; i++) {
			int k = rand.nextInt(allowedRegions.size());
			double[] p = allowedRegions.get(k);
			double px = p[0]+rand.nextDouble()*(p[2]-p[0]);
			double py = p[1]+rand.nextDouble()*(p[3]-p[1]);
			double overx = px+ReikaModelledBreakFX.pw-p[2];
			if (overx > 0)
				px -= overx;
			double overy = py+ReikaModelledBreakFX.pw-p[2];
			if (overy > 0)
				py -= overy;
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), texture, px, py, mod));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockHitEffects!
	 * Args: Texture Path, world, MovingObjectPosition, Block, EffectRenderer, Allowed Texture Regions. <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticlesDirect(String texture, World world, MovingObjectPosition mov, Block b, EffectRenderer eff, List<double[]> allowedRegions, Class mod) {
		if (mov == null)
			return false;
		int x = mov.blockX;
		int y = mov.blockY;
		int z = mov.blockZ;
		int j = 1+rand.nextInt(2);
		for (int i = 0; i < j; i++) {
			int k = rand.nextInt(allowedRegions.size());
			double[] p = allowedRegions.get(k);
			double px = p[0]+rand.nextDouble()*(p[2]-p[0]);
			double py = p[1]+rand.nextDouble()*(p[3]-p[1]);
			double overx = px+ReikaModelledBreakFX.pw-p[2];
			if (overx > 0)
				px -= overx;
			double overy = py+ReikaModelledBreakFX.pw-p[2];
			if (overy > 0)
				py -= overy;
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), texture, px, py, mod));
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void spawnDropParticles(World world, int x, int y, int z, Block b, int meta) {
		int n = 12+rand.nextInt(12);
		spawnDropParticles(world, x, y, z, b, meta, n);
	}

	@SideOnly(Side.CLIENT)
	public static void spawnDropParticles(World world, int x, int y, int z, Block b, int meta, int n) {
		for (int i = 0; i < n; i++) {
			double vx = ReikaRandomHelper.getRandomPlusMinus(0D, 0.25);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0D, 0.25);
			double vy = ReikaRandomHelper.getRandomBetween(0.125, 1);
			Minecraft.getMinecraft().effectRenderer.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), vx, vy, vz, b, meta, 0));
		}
	}

	@SideOnly(Side.CLIENT)
	public static TesselatorVertexState getTessellatorState() {
		Entity e = Minecraft.getMinecraft().renderViewEntity;
		TesselatorVertexState st = Tessellator.instance.getVertexState((float)e.posX, (float)e.posY, (float)e.posZ);
		return st;
	}

	@SideOnly(Side.CLIENT)
	public static void rerenderAllChunks() {
		Minecraft.getMinecraft().renderGlobal.loadRenderers();
	}

	@SideOnly(Side.CLIENT)
	public static void rerenderAllChunksLazily() {
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		int r = 192;
		int x1 = MathHelper.floor_double(ep.posX-r);
		int x2 = MathHelper.floor_double(ep.posX+r);
		int z1 = MathHelper.floor_double(ep.posZ-r);
		int z2 = MathHelper.floor_double(ep.posZ+r);
		world.markBlockRangeForRenderUpdate(x1, 0, z1, x2, world.provider.getHeight()-1, z2);
	}

	public static int getFPS() {
		return Minecraft.debugFPS;
	}

	public static int getRealFOV() {
		float base = Minecraft.getMinecraft().gameSettings.fovSetting;//-14;
		float diff = ((-40+70-base)/40F)*15F;
		float ang = base+diff;
		//ReikaJavaLibrary.pConsole(ang);
		return (int)ang;
	}

	/*
	public static void updateAllWorldRenderers() {
		try {
			Field f = RenderGlobal.class.getDeclaredField("worldRenderers");
			f.setAccessible(true);
			WorldRenderer[] w = (WorldRenderer[])f.get(Minecraft.getMinecraft().renderGlobal);
			for (int i = 0; i < w.length; i++) {
				w[i].markDirty();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public static void renderEnchantedModel(TileEntity tile, TileModel model, ArrayList li, float rotation) {
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;
		float f9 = (System.nanoTime()/100000000)%64/64F;
		ReikaTextureHelper.bindEnchantmentTexture();
		GL11.glEnable(GL11.GL_BLEND);
		BlendMode.OVERLAYDARK.apply();
		float f10 = 0.5F;
		GL11.glColor4f(f10, f10, f10, 1.0F);

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glTranslated(f9, f9, f9);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(0, 2, 2);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		float f11 = 0.76F;
		GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		GL11.glDepthMask(false);

		GL11.glDisable(GL11.GL_LIGHTING);

		double d = 1.0125;
		int p = 2;
		GL11.glTranslated(0, p, 0);
		GL11.glScaled(d, d, d);
		GL11.glTranslated(0, -p, 0);

		model.renderAll(tile, li);

		GL11.glTranslated(0, p, 0);
		GL11.glScaled(1D/d, 1D/d, 1D/d);
		GL11.glTranslated(0, -p, 0);

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDepthMask(true);

		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		BlendMode.DEFAULT.apply();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public static int getGUIScale() {
		Minecraft mc = Minecraft.getMinecraft();
		return new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
	}

	public static boolean prepareAmbientOcclusion(IBlockAccess iba, int x, int y, int z, Block b, RenderBlocks rb, ForgeDirection dir, float red, float green, float blue) {
		rb.enableAO = true;
		boolean flag = false;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		boolean flag1 = true;
		int l = b.getMixedBrightnessForBlock(iba, x, y, z);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(983055);

		if (rb.getBlockIcon(b).getIconName().equals("grass_top")) {
			flag1 = false;
		}
		else if (rb.hasOverrideBlockTexture()) {
			flag1 = false;
		}

		boolean flag2;
		boolean flag3;
		boolean flag4;
		boolean flag5;
		int i1;
		float f7;

		switch(dir) {

			case DOWN: {
				if (rb.renderMinY <= 0.0D) {
					--y;
				}

				rb.aoBrightnessXYNN = b.getMixedBrightnessForBlock(iba, x-1, y, z);
				rb.aoBrightnessYZNN = b.getMixedBrightnessForBlock(iba, x, y, z-1);
				rb.aoBrightnessYZNP = b.getMixedBrightnessForBlock(iba, x, y, z+1);
				rb.aoBrightnessXYPN = b.getMixedBrightnessForBlock(iba, x+1, y, z);
				rb.aoLightValueScratchXYNN = iba.getBlock(x-1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNN = iba.getBlock(x, y, z-1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNP = iba.getBlock(x, y, z+1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPN = iba.getBlock(x+1, y, z).getAmbientOcclusionLightValue();
				flag2 = iba.getBlock(x+1, y-1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x-1, y-1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x, y-1, z+1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y-1, z-1).getCanBlockGrass();

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXYNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXYNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x-1, y, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x-1, y, z-1);
				}

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXYNN;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXYNN;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x-1, y, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x-1, y, z+1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXYPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXYPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x+1, y, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x+1, y, z-1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXYPN;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXYPN;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x+1, y, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x+1, y, z+1);
				}

				if (rb.renderMinY <= 0.0D) {
					++y;
				}

				i1 = l;

				if (rb.renderMinY <= 0.0D || !iba.getBlock(x, y-1, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y-1, z);
				}

				f7 = iba.getBlock(x, y-1, z).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXYZNNP+rb.aoLightValueScratchXYNN+rb.aoLightValueScratchYZNP+f7) / 4.0F;
				f6 = (rb.aoLightValueScratchYZNP+f7+rb.aoLightValueScratchXYZPNP+rb.aoLightValueScratchXYPN) / 4.0F;
				f5 = (f7+rb.aoLightValueScratchYZNN+rb.aoLightValueScratchXYPN+rb.aoLightValueScratchXYZPNN) / 4.0F;
				f4 = (rb.aoLightValueScratchXYNN+rb.aoLightValueScratchXYZNNN+f7+rb.aoLightValueScratchYZNN) / 4.0F;
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessXYZNNP, rb.aoBrightnessXYNN, rb.aoBrightnessYZNP, i1);
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessYZNP, rb.aoBrightnessXYZPNP, rb.aoBrightnessXYPN, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessYZNN, rb.aoBrightnessXYPN, rb.aoBrightnessXYZPNN, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessXYNN, rb.aoBrightnessXYZNNN, rb.aoBrightnessYZNN, i1);

				if (flag1) {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red * 0.5F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green * 0.5F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue * 0.5F;
				}
				else {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = 0.5F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = 0.5F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = 0.5F;
				}

				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				//rb.renderFaceYNeg(b, x, y, z, rb.getBlockIcon(b, iba, x, y, z, 0));
				flag = true;
			}

			case UP: {
				if (rb.renderMaxY >= 1.0D) {
					++y;
				}

				rb.aoBrightnessXYNP = b.getMixedBrightnessForBlock(iba, x-1, y, z);
				rb.aoBrightnessXYPP = b.getMixedBrightnessForBlock(iba, x+1, y, z);
				rb.aoBrightnessYZPN = b.getMixedBrightnessForBlock(iba, x, y, z-1);
				rb.aoBrightnessYZPP = b.getMixedBrightnessForBlock(iba, x, y, z+1);
				rb.aoLightValueScratchXYNP = iba.getBlock(x-1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPP = iba.getBlock(x+1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPN = iba.getBlock(x, y, z-1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPP = iba.getBlock(x, y, z+1).getAmbientOcclusionLightValue();
				flag2 = iba.getBlock(x+1, y+1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x-1, y+1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x, y+1, z+1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y+1, z-1).getCanBlockGrass();

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXYNP;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXYNP;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x-1, y, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x-1, y, z-1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXYPP;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXYPP;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x+1, y, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x+1, y, z-1);
				}

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXYNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXYNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x-1, y, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x-1, y, z+1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXYPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXYPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x+1, y, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x+1, y, z+1);
				}

				if (rb.renderMaxY >= 1.0D) {
					--y;
				}

				i1 = l;

				if (rb.renderMaxY >= 1.0D || !iba.getBlock(x, y+1, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y+1, z);
				}

				f7 = iba.getBlock(x, y+1, z).getAmbientOcclusionLightValue();
				f6 = (rb.aoLightValueScratchXYZNPP+rb.aoLightValueScratchXYNP+rb.aoLightValueScratchYZPP+f7) / 4.0F;
				f3 = (rb.aoLightValueScratchYZPP+f7+rb.aoLightValueScratchXYZPPP+rb.aoLightValueScratchXYPP) / 4.0F;
				f4 = (f7+rb.aoLightValueScratchYZPN+rb.aoLightValueScratchXYPP+rb.aoLightValueScratchXYZPPN) / 4.0F;
				f5 = (rb.aoLightValueScratchXYNP+rb.aoLightValueScratchXYZNPN+f7+rb.aoLightValueScratchYZPN) / 4.0F;
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessXYZNPP, rb.aoBrightnessXYNP, rb.aoBrightnessYZPP, i1);
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessYZPP, rb.aoBrightnessXYZPPP, rb.aoBrightnessXYPP, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessYZPN, rb.aoBrightnessXYPP, rb.aoBrightnessXYZPPN, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessXYNP, rb.aoBrightnessXYZNPN, rb.aoBrightnessYZPN, i1);
				rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red;
				rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green;
				rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue;
				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				//rb.renderFaceYPos(b, x, y, z, rb.getBlockIcon(b, iba, x, y, z, 1));
				flag = true;
			}

			IIcon iicon;

			case NORTH: {
				if (rb.renderMinZ <= 0.0D) {
					--z;
				}

				rb.aoLightValueScratchXZNN = iba.getBlock(x-1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNN = iba.getBlock(x, y-1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPN = iba.getBlock(x, y+1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPN = iba.getBlock(x+1, y, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXZNN = b.getMixedBrightnessForBlock(iba, x-1, y, z);
				rb.aoBrightnessYZNN = b.getMixedBrightnessForBlock(iba, x, y-1, z);
				rb.aoBrightnessYZPN = b.getMixedBrightnessForBlock(iba, x, y+1, z);
				rb.aoBrightnessXZPN = b.getMixedBrightnessForBlock(iba, x+1, y, z);
				flag2 = iba.getBlock(x+1, y, z-1).getCanBlockGrass();
				flag3 = iba.getBlock(x-1, y, z-1).getCanBlockGrass();
				flag4 = iba.getBlock(x, y+1, z-1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y-1, z-1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x-1, y-1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x-1, y-1, z);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x-1, y+1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x-1, y+1, z);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x+1, y-1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x+1, y-1, z);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x+1, y+1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x+1, y+1, z);
				}

				if (rb.renderMinZ <= 0.0D) {
					++z;
				}

				i1 = l;

				if (rb.renderMinZ <= 0.0D || !iba.getBlock(x, y, z-1).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y, z-1);
				}

				f7 = iba.getBlock(x, y, z-1).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXZNN+rb.aoLightValueScratchXYZNPN+f7+rb.aoLightValueScratchYZPN) / 4.0F;
				f4 = (f7+rb.aoLightValueScratchYZPN+rb.aoLightValueScratchXZPN+rb.aoLightValueScratchXYZPPN) / 4.0F;
				f5 = (rb.aoLightValueScratchYZNN+f7+rb.aoLightValueScratchXYZPNN+rb.aoLightValueScratchXZPN) / 4.0F;
				f6 = (rb.aoLightValueScratchXYZNNN+rb.aoLightValueScratchXZNN+rb.aoLightValueScratchYZNN+f7) / 4.0F;
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessXZNN, rb.aoBrightnessXYZNPN, rb.aoBrightnessYZPN, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessYZPN, rb.aoBrightnessXZPN, rb.aoBrightnessXYZPPN, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessYZNN, rb.aoBrightnessXYZPNN, rb.aoBrightnessXZPN, i1);
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessXYZNNN, rb.aoBrightnessXZNN, rb.aoBrightnessYZNN, i1);

				if (flag1) {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red * 0.8F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green * 0.8F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue * 0.8F;
				}
				else {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = 0.8F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = 0.8F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = 0.8F;
				}

				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				iicon = rb.getBlockIcon(b, iba, x, y, z, 2);
				//rb.renderFaceZNeg(b, x, y, z, iicon);

				if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !rb.hasOverrideBlockTexture()) {
					rb.colorRedTopLeft *= red;
					rb.colorRedBottomLeft *= red;
					rb.colorRedBottomRight *= red;
					rb.colorRedTopRight *= red;
					rb.colorGreenTopLeft *= green;
					rb.colorGreenBottomLeft *= green;
					rb.colorGreenBottomRight *= green;
					rb.colorGreenTopRight *= green;
					rb.colorBlueTopLeft *= blue;
					rb.colorBlueBottomLeft *= blue;
					rb.colorBlueBottomRight *= blue;
					rb.colorBlueTopRight *= blue;
					//rb.renderFaceZNeg(b, x, y, z, BlockGrass.getIconSideOverlay());
				}

				flag = true;
			}

			case SOUTH: {
				if (rb.renderMaxZ >= 1.0D) {
					++z;
				}

				rb.aoLightValueScratchXZNP = iba.getBlock(x-1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPP = iba.getBlock(x+1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNP = iba.getBlock(x, y-1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPP = iba.getBlock(x, y+1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXZNP = b.getMixedBrightnessForBlock(iba, x-1, y, z);
				rb.aoBrightnessXZPP = b.getMixedBrightnessForBlock(iba, x+1, y, z);
				rb.aoBrightnessYZNP = b.getMixedBrightnessForBlock(iba, x, y-1, z);
				rb.aoBrightnessYZPP = b.getMixedBrightnessForBlock(iba, x, y+1, z);
				flag2 = iba.getBlock(x+1, y, z+1).getCanBlockGrass();
				flag3 = iba.getBlock(x-1, y, z+1).getCanBlockGrass();
				flag4 = iba.getBlock(x, y+1, z+1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y-1, z+1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x-1, y-1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x-1, y-1, z);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x-1, y+1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x-1, y+1, z);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x+1, y-1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x+1, y-1, z);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x+1, y+1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x+1, y+1, z);
				}

				if (rb.renderMaxZ >= 1.0D) {
					--z;
				}

				i1 = l;

				if (rb.renderMaxZ >= 1.0D || !iba.getBlock(x, y, z+1).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y, z+1);
				}

				f7 = iba.getBlock(x, y, z+1).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXZNP+rb.aoLightValueScratchXYZNPP+f7+rb.aoLightValueScratchYZPP) / 4.0F;
				f6 = (f7+rb.aoLightValueScratchYZPP+rb.aoLightValueScratchXZPP+rb.aoLightValueScratchXYZPPP) / 4.0F;
				f5 = (rb.aoLightValueScratchYZNP+f7+rb.aoLightValueScratchXYZPNP+rb.aoLightValueScratchXZPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXYZNNP+rb.aoLightValueScratchXZNP+rb.aoLightValueScratchYZNP+f7) / 4.0F;
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessXZNP, rb.aoBrightnessXYZNPP, rb.aoBrightnessYZPP, i1);
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessYZPP, rb.aoBrightnessXZPP, rb.aoBrightnessXYZPPP, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessYZNP, rb.aoBrightnessXYZPNP, rb.aoBrightnessXZPP, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessXYZNNP, rb.aoBrightnessXZNP, rb.aoBrightnessYZNP, i1);

				if (flag1) {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red * 0.8F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green * 0.8F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue * 0.8F;
				}
				else {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = 0.8F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = 0.8F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = 0.8F;
				}

				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				iicon = rb.getBlockIcon(b, iba, x, y, z, 3);
				//rb.renderFaceZPos(b, x, y, z, rb.getBlockIcon(b, iba, x, y, z, 3));

				if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !rb.hasOverrideBlockTexture()) {
					rb.colorRedTopLeft *= red;
					rb.colorRedBottomLeft *= red;
					rb.colorRedBottomRight *= red;
					rb.colorRedTopRight *= red;
					rb.colorGreenTopLeft *= green;
					rb.colorGreenBottomLeft *= green;
					rb.colorGreenBottomRight *= green;
					rb.colorGreenTopRight *= green;
					rb.colorBlueTopLeft *= blue;
					rb.colorBlueBottomLeft *= blue;
					rb.colorBlueBottomRight *= blue;
					rb.colorBlueTopRight *= blue;
					//rb.renderFaceZPos(b, x, y, z, BlockGrass.getIconSideOverlay());
				}

				flag = true;
			}

			case WEST: {
				if (rb.renderMinX <= 0.0D) {
					--x;
				}

				rb.aoLightValueScratchXYNN = iba.getBlock(x, y-1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZNN = iba.getBlock(x, y, z-1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZNP = iba.getBlock(x, y, z+1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYNP = iba.getBlock(x, y+1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXYNN = b.getMixedBrightnessForBlock(iba, x, y-1, z);
				rb.aoBrightnessXZNN = b.getMixedBrightnessForBlock(iba, x, y, z-1);
				rb.aoBrightnessXZNP = b.getMixedBrightnessForBlock(iba, x, y, z+1);
				rb.aoBrightnessXYNP = b.getMixedBrightnessForBlock(iba, x, y+1, z);
				flag2 = iba.getBlock(x-1, y+1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x-1, y-1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x-1, y, z-1).getCanBlockGrass();
				flag5 = iba.getBlock(x-1, y, z+1).getCanBlockGrass();

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x, y-1, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x, y-1, z-1);
				}

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x, y-1, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x, y-1, z+1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x, y+1, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x, y+1, z-1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x, y+1, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x, y+1, z+1);
				}

				if (rb.renderMinX <= 0.0D) {
					++x;
				}

				i1 = l;

				if (rb.renderMinX <= 0.0D || !iba.getBlock(x-1, y, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x-1, y, z);
				}

				f7 = iba.getBlock(x-1, y, z).getAmbientOcclusionLightValue();
				f6 = (rb.aoLightValueScratchXYNN+rb.aoLightValueScratchXYZNNP+f7+rb.aoLightValueScratchXZNP) / 4.0F;
				f3 = (f7+rb.aoLightValueScratchXZNP+rb.aoLightValueScratchXYNP+rb.aoLightValueScratchXYZNPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXZNN+f7+rb.aoLightValueScratchXYZNPN+rb.aoLightValueScratchXYNP) / 4.0F;
				f5 = (rb.aoLightValueScratchXYZNNN+rb.aoLightValueScratchXYNN+rb.aoLightValueScratchXZNN+f7) / 4.0F;
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessXYNN, rb.aoBrightnessXYZNNP, rb.aoBrightnessXZNP, i1);
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessXZNP, rb.aoBrightnessXYNP, rb.aoBrightnessXYZNPP, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessXZNN, rb.aoBrightnessXYZNPN, rb.aoBrightnessXYNP, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessXYZNNN, rb.aoBrightnessXYNN, rb.aoBrightnessXZNN, i1);

				if (flag1) {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red * 0.6F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green * 0.6F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue * 0.6F;
				}
				else {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = 0.6F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = 0.6F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = 0.6F;
				}

				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				iicon = rb.getBlockIcon(b, iba, x, y, z, 4);
				//rb.renderFaceXNeg(b, x, y, z, iicon);

				if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !rb.hasOverrideBlockTexture()) {
					rb.colorRedTopLeft *= red;
					rb.colorRedBottomLeft *= red;
					rb.colorRedBottomRight *= red;
					rb.colorRedTopRight *= red;
					rb.colorGreenTopLeft *= green;
					rb.colorGreenBottomLeft *= green;
					rb.colorGreenBottomRight *= green;
					rb.colorGreenTopRight *= green;
					rb.colorBlueTopLeft *= blue;
					rb.colorBlueBottomLeft *= blue;
					rb.colorBlueBottomRight *= blue;
					rb.colorBlueTopRight *= blue;
					//rb.renderFaceXNeg(b, x, y, z, BlockGrass.getIconSideOverlay());
				}

				flag = true;
			}

			case EAST: {
				if (rb.renderMaxX >= 1.0D) {
					++x;
				}

				rb.aoLightValueScratchXYPN = iba.getBlock(x, y-1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPN = iba.getBlock(x, y, z-1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPP = iba.getBlock(x, y, z+1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPP = iba.getBlock(x, y+1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXYPN = b.getMixedBrightnessForBlock(iba, x, y-1, z);
				rb.aoBrightnessXZPN = b.getMixedBrightnessForBlock(iba, x, y, z-1);
				rb.aoBrightnessXZPP = b.getMixedBrightnessForBlock(iba, x, y, z+1);
				rb.aoBrightnessXYPP = b.getMixedBrightnessForBlock(iba, x, y+1, z);
				flag2 = iba.getBlock(x+1, y+1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x+1, y-1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x+1, y, z+1).getCanBlockGrass();
				flag5 = iba.getBlock(x+1, y, z-1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x, y-1, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x, y-1, z-1);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x, y-1, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x, y-1, z+1);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x, y+1, z-1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x, y+1, z-1);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x, y+1, z+1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x, y+1, z+1);
				}

				if (rb.renderMaxX >= 1.0D) {
					--x;
				}

				i1 = l;

				if (rb.renderMaxX >= 1.0D || !iba.getBlock(x+1, y, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x+1, y, z);
				}

				f7 = iba.getBlock(x+1, y, z).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXYPN+rb.aoLightValueScratchXYZPNP+f7+rb.aoLightValueScratchXZPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXYZPNN+rb.aoLightValueScratchXYPN+rb.aoLightValueScratchXZPN+f7) / 4.0F;
				f5 = (rb.aoLightValueScratchXZPN+f7+rb.aoLightValueScratchXYZPPN+rb.aoLightValueScratchXYPP) / 4.0F;
				f6 = (f7+rb.aoLightValueScratchXZPP+rb.aoLightValueScratchXYPP+rb.aoLightValueScratchXYZPPP) / 4.0F;
				rb.brightnessTopLeft = rb.getAoBrightness(rb.aoBrightnessXYPN, rb.aoBrightnessXYZPNP, rb.aoBrightnessXZPP, i1);
				rb.brightnessTopRight = rb.getAoBrightness(rb.aoBrightnessXZPP, rb.aoBrightnessXYPP, rb.aoBrightnessXYZPPP, i1);
				rb.brightnessBottomRight = rb.getAoBrightness(rb.aoBrightnessXZPN, rb.aoBrightnessXYZPPN, rb.aoBrightnessXYPP, i1);
				rb.brightnessBottomLeft = rb.getAoBrightness(rb.aoBrightnessXYZPNN, rb.aoBrightnessXYPN, rb.aoBrightnessXZPN, i1);

				if (flag1) {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = red * 0.6F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = green * 0.6F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = blue * 0.6F;
				}
				else {
					rb.colorRedTopLeft = rb.colorRedBottomLeft = rb.colorRedBottomRight = rb.colorRedTopRight = 0.6F;
					rb.colorGreenTopLeft = rb.colorGreenBottomLeft = rb.colorGreenBottomRight = rb.colorGreenTopRight = 0.6F;
					rb.colorBlueTopLeft = rb.colorBlueBottomLeft = rb.colorBlueBottomRight = rb.colorBlueTopRight = 0.6F;
				}

				rb.colorRedTopLeft *= f3;
				rb.colorGreenTopLeft *= f3;
				rb.colorBlueTopLeft *= f3;
				rb.colorRedBottomLeft *= f4;
				rb.colorGreenBottomLeft *= f4;
				rb.colorBlueBottomLeft *= f4;
				rb.colorRedBottomRight *= f5;
				rb.colorGreenBottomRight *= f5;
				rb.colorBlueBottomRight *= f5;
				rb.colorRedTopRight *= f6;
				rb.colorGreenTopRight *= f6;
				rb.colorBlueTopRight *= f6;
				iicon = rb.getBlockIcon(b, iba, x, y, z, 5);
				//rb.renderFaceXPos(b, x, y, z, iicon);

				if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !rb.hasOverrideBlockTexture()) {
					rb.colorRedTopLeft *= red;
					rb.colorRedBottomLeft *= red;
					rb.colorRedBottomRight *= red;
					rb.colorRedTopRight *= red;
					rb.colorGreenTopLeft *= green;
					rb.colorGreenBottomLeft *= green;
					rb.colorGreenBottomRight *= green;
					rb.colorGreenTopRight *= green;
					rb.colorBlueTopLeft *= blue;
					rb.colorBlueBottomLeft *= blue;
					rb.colorBlueBottomRight *= blue;
					rb.colorBlueTopRight *= blue;
					//rb.renderFaceXPos(b, x, y, z, BlockGrass.getIconSideOverlay());
				}

				flag = true;
			}
			default:
				break;

		}

		rb.enableAO = false;
		return flag;
	}

	public static long getRenderFrame() {
		return frame;
	}

	public static float getPartialTickTime() {
		return ptick;
	}

	private static int frame = -1;
	private static float ptick = -1;
	public static ICamera renderFrustrum = new Frustrum();
	public static double thirdPersonDistance;

	public static class RenderTick implements TickHandler {

		@Override
		public void tick(TickType type, Object... tickData) {
			frame++;
			ptick = (Float)tickData[0];
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.RENDER);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return null;
		}

	}

	public static void renderTorch(IBlockAccess world, double x, double y, double z, IIcon ico, Tessellator v5, RenderBlocks rb, double h, double w) {
		ico = rb.getIconSafe(ico);

		double u = ico.getMinU();
		double v = ico.getMinV();
		double du = ico.getMaxU();
		double dv = ico.getMaxV();
		double d9 = ico.getInterpolatedU(7.0D);
		double d10 = ico.getInterpolatedV(6.0D);
		double d11 = ico.getInterpolatedU(9.0D);
		double d12 = ico.getInterpolatedV(8.0D);
		double d13 = ico.getInterpolatedU(7.0D);
		double d14 = ico.getInterpolatedV(13.0D);
		double d15 = ico.getInterpolatedU(9.0D);
		double d16 = ico.getInterpolatedV(15.0D);
		x += 0.5D;
		z += 0.5D;
		double xmin = x - 0.5D;
		double xmax = x + 0.5D;
		double zmin = z - 0.5D;
		double zmax = z + 0.5D;

		v5.addVertexWithUV(x - w, y + h, z - w, d9, d10);
		v5.addVertexWithUV(x - w, y + h, z + w, d9, d12);
		v5.addVertexWithUV(x + w, y + h, z + w, d11, d12);
		v5.addVertexWithUV(x + w, y + h, z - w, d11, d10);

		v5.addVertexWithUV(x + w, y, z - w, d15, d14);
		v5.addVertexWithUV(x + w, y, z + w, d15, d16);
		v5.addVertexWithUV(x - w, y, z + w, d13, d16);
		v5.addVertexWithUV(x - w, y, z - w, d13, d14);

		v5.addVertexWithUV(x - w, y + 1.0D, zmin, u, v);
		v5.addVertexWithUV(x - w, y + 0.0D, zmin, u, dv);
		v5.addVertexWithUV(x - w, y + 0.0D, zmax, du, dv);
		v5.addVertexWithUV(x - w, y + 1.0D, zmax, du, v);

		v5.addVertexWithUV(x + w, y + 1.0D, zmax, u, v);
		v5.addVertexWithUV(x + w, y + 0.0D, zmax, u, dv);
		v5.addVertexWithUV(x + w, y + 0.0D, zmin, du, dv);
		v5.addVertexWithUV(x + w, y + 1.0D, zmin, du, v);

		v5.addVertexWithUV(xmin, y + 1.0D, z + w, u, v);
		v5.addVertexWithUV(xmin, y + 0.0D, z + w, u, dv);
		v5.addVertexWithUV(xmax, y + 0.0D, z + w, du, dv);
		v5.addVertexWithUV(xmax, y + 1.0D, z + w, du, v);

		v5.addVertexWithUV(xmax, y + 1.0D, z - w, u, v);
		v5.addVertexWithUV(xmax, y + 0.0D, z - w, u, dv);
		v5.addVertexWithUV(xmin, y + 0.0D, z - w, du, dv);
		v5.addVertexWithUV(xmin, y + 1.0D, z - w, du, v);
	}

	public static void renderCrossTex(IBlockAccess world, int x, int y, int z, IIcon ico, Tessellator v5, RenderBlocks rb, double h) {
		ico = rb.getIconSafe(ico);

		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		v5.addVertexWithUV(x, y+h, z, u, v);
		v5.addVertexWithUV(x+1, y+h, z+1, du, v);
		v5.addVertexWithUV(x+1, y, z+1, du, dv);
		v5.addVertexWithUV(x, y, z, u, dv);

		v5.addVertexWithUV(x, y, z, u, dv);
		v5.addVertexWithUV(x+1, y, z+1, du, dv);
		v5.addVertexWithUV(x+1, y+h, z+1, du, v);
		v5.addVertexWithUV(x, y+h, z, u, v);

		v5.addVertexWithUV(x, y+h, z+1, u, v);
		v5.addVertexWithUV(x+1, y+h, z, du, v);
		v5.addVertexWithUV(x+1, y, z, du, dv);
		v5.addVertexWithUV(x, y, z+1, u, dv);

		v5.addVertexWithUV(x, y, z+1, u, dv);
		v5.addVertexWithUV(x+1, y, z, du, dv);
		v5.addVertexWithUV(x+1, y+h, z, du, v);
		v5.addVertexWithUV(x, y+h, z+1, u, v);
	}

	public static void renderFlatInnerTextureOnSide(IBlockAccess world, int x, int y, int z, IIcon ico, Tessellator v5, RenderBlocks rb, ForgeDirection dir, double inset, boolean needAdj) {
		ico = rb.getIconSafe(ico);

		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		if (needAdj) {
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == Blocks.air)
				return;
			if (b.isAir(world, dx, dy, dz))
				return;
			if (!b.getMaterial().isSolid() || !b.isSideSolid(world, dx, dy, dz, dir.getOpposite()))
				return;
		}

		switch(dir) {
			case DOWN:
				v5.addVertexWithUV(x, y+inset, z+1, u, dv);
				v5.addVertexWithUV(x+1, y+inset, z+1, u, v);
				v5.addVertexWithUV(x+1, y+inset, z, du, v);
				v5.addVertexWithUV(x, y+inset, z, du, dv);
				break;
			case UP:
				v5.addVertexWithUV(x, y+1-inset, z, du, dv);
				v5.addVertexWithUV(x+1, y+1-inset, z, u, dv);
				v5.addVertexWithUV(x+1, y+1-inset, z+1, u, v);
				v5.addVertexWithUV(x, y+1-inset, z+1, du, v);
				break;
			case EAST:
				v5.addVertexWithUV(x+1-inset, y, z+1, u, dv);
				v5.addVertexWithUV(x+1-inset, y+1, z+1, u, v);
				v5.addVertexWithUV(x+1-inset, y+1, z, du, v);
				v5.addVertexWithUV(x+1-inset, y, z, du, dv);
				break;
			case WEST:
				v5.addVertexWithUV(x+inset, y, z, u, dv);
				v5.addVertexWithUV(x+inset, y+1, z, u, v);
				v5.addVertexWithUV(x+inset, y+1, z+1, du, v);
				v5.addVertexWithUV(x+inset, y, z+1, du, dv);
				break;
			case NORTH:
				v5.addVertexWithUV(x+1, y, z+inset, u, dv);
				v5.addVertexWithUV(x+1, y+1, z+inset, u, v);
				v5.addVertexWithUV(x, y+1, z+inset, du, v);
				v5.addVertexWithUV(x, y, z+inset, du, dv);
				break;
			case SOUTH:
				v5.addVertexWithUV(x, y, z+1-inset, du, dv);
				v5.addVertexWithUV(x, y+1, z+1-inset, du, v);
				v5.addVertexWithUV(x+1, y+1, z+1-inset, u, v);
				v5.addVertexWithUV(x+1, y, z+1-inset, u, dv);
				break;
			case UNKNOWN:
				break;
		}
	}

	public static void renderCropTypeTex(IBlockAccess world, int x, int y, int z, IIcon ico, Tessellator v5, RenderBlocks rb, double space, double h) {
		ico = rb.getIconSafe(ico);

		float u = ico.getMinU();
		float du = ico.getMaxU();
		float v = ico.getMinV();
		float dv = ico.getMaxV();

		double d7 = x+0.5D-space;
		double d8 = x+0.5D+space;
		double d9 = z+0.5D-0.5D;
		double d10 = z+0.5D+0.5D;

		double dy = y-0.0625;

		v5.addVertexWithUV(d7, dy+h, d9, u, v);
		v5.addVertexWithUV(d7, dy+0, d9, u, dv);
		v5.addVertexWithUV(d7, dy+0, d10, du, dv);
		v5.addVertexWithUV(d7, dy+h, d10, du, v);
		v5.addVertexWithUV(d7, dy+h, d10, u, v);
		v5.addVertexWithUV(d7, dy+0, d10, u, dv);
		v5.addVertexWithUV(d7, dy+0, d9, du, dv);
		v5.addVertexWithUV(d7, dy+h, d9, du, v);
		v5.addVertexWithUV(d8, dy+h, d10, u, v);
		v5.addVertexWithUV(d8, dy+0, d10, u, dv);
		v5.addVertexWithUV(d8, dy+0, d9, du, dv);
		v5.addVertexWithUV(d8, dy+h, d9, du, v);
		v5.addVertexWithUV(d8, dy+h, d9, u, v);
		v5.addVertexWithUV(d8, dy+0, d9, u, dv);
		v5.addVertexWithUV(d8, dy+0, d10, du, dv);
		v5.addVertexWithUV(d8, dy+h, d10, du, v);
		d7 = x+0.5D-0.5D;
		d8 = x+0.5D+0.5D;
		d9 = z+0.5D-space;
		d10 = z+0.5D+space;
		v5.addVertexWithUV(d7, dy+h, d9, u, v);
		v5.addVertexWithUV(d7, dy+0, d9, u, dv);
		v5.addVertexWithUV(d8, dy+0, d9, du, dv);
		v5.addVertexWithUV(d8, dy+h, d9, du, v);
		v5.addVertexWithUV(d8, dy+h, d9, u, v);
		v5.addVertexWithUV(d8, dy+0, d9, u, dv);
		v5.addVertexWithUV(d7, dy+0, d9, du, dv);
		v5.addVertexWithUV(d7, dy+h, d9, du, v);
		v5.addVertexWithUV(d8, dy+h, d10, u, v);
		v5.addVertexWithUV(d8, dy+0, d10, u, dv);
		v5.addVertexWithUV(d7, dy+0, d10, du, dv);
		v5.addVertexWithUV(d7, dy+h, d10, du, v);
		v5.addVertexWithUV(d7, dy+h, d10, u, v);
		v5.addVertexWithUV(d7, dy+0, d10, u, dv);
		v5.addVertexWithUV(d8, dy+0, d10, du, dv);
		v5.addVertexWithUV(d8, dy+h, d10, du, v);
	}

	//A big block of hack
	@SideOnly(Side.CLIENT)
	public static void setCameraPosition(EntityPlayer ep, double cx, double cy, double cz, double cxPrev, double cyPrev, double czPrev, double yaw, double yawPrev, double pitch, double pitchPrev, boolean setPos, boolean setAngs) {
		DragonAPICore.debug("Moving "+ep.getCommandSenderName()+" camera to "+cx+","+cy+","+cz+" @ "+yaw+" / "+pitch);
		RenderManager rm = RenderManager.instance;
		if (setPos) {
			RenderManager.renderPosX = cx;
			RenderManager.renderPosY = cy;
			RenderManager.renderPosZ = cz;
			rm.viewerPosX = cx;
			rm.viewerPosY = cy;
			rm.viewerPosZ = cz;
		}
		if (rm.field_147941_i != null) {
			if (setPos) {
				rm.field_147941_i.posX = cx;
				rm.field_147941_i.posY = cy;
				rm.field_147941_i.posZ = cz;
				rm.field_147941_i.lastTickPosX = cxPrev;
				rm.field_147941_i.lastTickPosY = cyPrev;
				rm.field_147941_i.lastTickPosZ = czPrev;
				rm.field_147941_i.prevPosX = cxPrev;
				rm.field_147941_i.prevPosY = cyPrev;
				rm.field_147941_i.prevPosZ = czPrev;
			}
			rm.cacheActiveRenderInfo(rm.worldObj, rm.renderEngine, Minecraft.getMinecraft().fontRenderer, ep, rm.field_147941_i, rm.options, 0);
		}
		if (setPos) {
			TileEntityRendererDispatcher.staticPlayerX = cx;
			TileEntityRendererDispatcher.staticPlayerY = cy;
			TileEntityRendererDispatcher.staticPlayerZ = cz;
		}
		EntityPlayer mcp = Minecraft.getMinecraft().thePlayer;
		EntityLivingBase mcp2 = Minecraft.getMinecraft().renderViewEntity;
		if (setPos) {
			mcp.posX = cx;
			mcp.posY = cy;
			mcp.posZ = cz;
			mcp.lastTickPosX = cxPrev;
			mcp.lastTickPosY = cyPrev;
			mcp.lastTickPosZ = czPrev;
			mcp.prevPosX = cxPrev;
			mcp.prevPosY = cyPrev;
			mcp.prevPosZ = czPrev;

			mcp2.posX = cx;
			mcp2.posY = cy;
			mcp2.posZ = cz;
			mcp2.lastTickPosX = cxPrev;
			mcp2.lastTickPosY = cyPrev;
			mcp2.lastTickPosZ = czPrev;
			mcp2.prevPosX = cxPrev;
			mcp2.prevPosY = cyPrev;
			mcp2.prevPosZ = czPrev;
		}
		if (setAngs) {
			mcp.rotationYawHead = (float)yaw;
			mcp.rotationYaw = (float)yaw;
			mcp.prevRotationYaw = (float)yawPrev;
			mcp.prevRotationYawHead = (float)yawPrev;
			mcp.cameraYaw = (float)yaw;
			mcp.prevCameraYaw = (float)yawPrev;
			mcp.rotationPitch = (float)pitch;
			mcp.prevRotationPitch = (float)pitchPrev;

			mcp2.rotationYawHead = (float)yaw;
			mcp2.rotationYaw = (float)yaw;
			mcp2.prevRotationYaw = (float)yawPrev;
			mcp2.prevRotationYawHead = (float)yawPrev;
			mcp2.rotationPitch = (float)pitch;
			mcp2.prevRotationPitch = (float)pitchPrev;
		}
		Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
	}

	public static void renderBlockSubCube(int x, int y, int z, double dx, double dy, double dz, double s, Tessellator v5, RenderBlocks rb, Block b, int meta) {
		renderBlockSubCube(x, y, z, dx, dy, dz, s, s, s, v5, rb, b, meta);
	}

	public static void renderBlockSubCube(int x, int y, int z, double dx, double dy, double dz, double sx, double sy, double sz, Tessellator v5, RenderBlocks rb, Block b, int meta) {
		boolean flag = rb.renderAllFaces;
		rb.renderAllFaces = true;
		rb.renderMinX = dx/16;
		rb.renderMinY = dy/16;
		rb.renderMinZ = dz/16;
		rb.renderMaxX = rb.renderMinX+sx/16;
		rb.renderMaxY = rb.renderMinY+sy/16;
		rb.renderMaxZ = rb.renderMinZ+sz/16;
		rb.partialRenderBounds = true;
		rb.renderStandardBlockWithAmbientOcclusion(b, x, y, z, 1, 1, 1);
		rb.setRenderBounds(0, 0, 0, 1, 1, 1);
		rb.renderAllFaces = flag;
	}

	public static void renderBlockPieceNonCuboid(IBlockAccess iba, int x, int y, int z, Block b, Tessellator v5, CubePoints points) {
		v5.addTranslation(x, y, z);

		ForgeDirection dir = ForgeDirection.DOWN;
		IIcon ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x1y1z1.draw(v5, ico, dir);
		points.x2y1z1.draw(v5, ico, dir);
		points.x2y1z2.draw(v5, ico, dir);
		points.x1y1z2.draw(v5, ico, dir);

		dir = ForgeDirection.UP;
		ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x1y2z2.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);

		dir = ForgeDirection.WEST;
		ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x1y1z2.draw(v5, ico, dir);
		points.x1y2z2.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);
		points.x1y1z1.draw(v5, ico, dir);

		dir = ForgeDirection.EAST;
		ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x2y1z1.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x2y1z2.draw(v5, ico, dir);

		dir = ForgeDirection.NORTH;
		ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x1y1z1.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x2y1z1.draw(v5, ico, dir);

		dir = ForgeDirection.SOUTH;
		ico = b.getIcon(iba, x, y, z, dir.ordinal());
		points.x2y1z2.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x1y2z2.draw(v5, ico, dir);
		points.x1y1z2.draw(v5, ico, dir);

		v5.addTranslation(-x, -y, -z);
	}

	public static void renderBlockPieceNonCuboid(Block b, int meta, Tessellator v5, CubePoints points) {
		ForgeDirection dir = ForgeDirection.DOWN;
		IIcon ico = b.getIcon(dir.ordinal(), meta);
		points.x1y1z1.draw(v5, ico, dir);
		points.x2y1z1.draw(v5, ico, dir);
		points.x2y1z2.draw(v5, ico, dir);
		points.x1y1z2.draw(v5, ico, dir);

		dir = ForgeDirection.UP;
		ico = b.getIcon(dir.ordinal(), meta);
		points.x1y2z2.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);

		dir = ForgeDirection.WEST;
		ico = b.getIcon(dir.ordinal(), meta);
		points.x1y1z2.draw(v5, ico, dir);
		points.x1y2z2.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);
		points.x1y1z1.draw(v5, ico, dir);

		dir = ForgeDirection.EAST;
		ico = b.getIcon(dir.ordinal(), meta);
		points.x2y1z1.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x2y1z2.draw(v5, ico, dir);

		dir = ForgeDirection.NORTH;
		ico = b.getIcon(dir.ordinal(), meta);
		points.x1y1z1.draw(v5, ico, dir);
		points.x1y2z1.draw(v5, ico, dir);
		points.x2y2z1.draw(v5, ico, dir);
		points.x2y1z1.draw(v5, ico, dir);

		dir = ForgeDirection.SOUTH;
		ico = b.getIcon(dir.ordinal(), meta);
		points.x2y1z2.draw(v5, ico, dir);
		points.x2y2z2.draw(v5, ico, dir);
		points.x1y2z2.draw(v5, ico, dir);
		points.x1y1z2.draw(v5, ico, dir);
	}

	public static void renderIconIn3D(TessellatorVertexList v5, IIcon ico, int x, int y, int z) {
		float t = 0.0625F;
		float w = ico.getIconWidth();
		float h = ico.getIconHeight();
		float maxu = ico.getMaxU();
		float maxv = ico.getMaxV();
		float minu = ico.getMinU();
		float minv = ico.getMinV();

		//v5.setNormal(0.0F, 0.0F, 1.0F);
		v5.addVertexWithUVColor(0.0D, 0.0D, 0.0D, maxu, maxv, 0xffffffff);
		v5.addVertexWithUVColor(1.0D, 0.0D, 0.0D, minu, maxv, 0xffffffff);
		v5.addVertexWithUVColor(1.0D, 1.0D, 0.0D, minu, minv, 0xffffffff);
		v5.addVertexWithUVColor(0.0D, 1.0D, 0.0D, maxu, minv, 0xffffffff);

		//v5.setNormal(0.0F, 0.0F, -1.0F);
		v5.addVertexWithUVColor(0.0D, 1.0D, 0.0F - t, maxu, minv, 0xffa0a0a0);
		v5.addVertexWithUVColor(1.0D, 1.0D, 0.0F - t, minu, minv, 0xffa0a0a0);
		v5.addVertexWithUVColor(1.0D, 0.0D, 0.0F - t, minu, maxv, 0xffa0a0a0);
		v5.addVertexWithUVColor(0.0D, 0.0D, 0.0F - t, maxu, maxv, 0xffa0a0a0);

		float f5 = 0.5F * (maxu - minu) / w;
		float f6 = 0.5F * (maxv - minv) / h;
		//v5.setNormal(-1.0F, 0.0F, 0.0F);
		int k;
		float f7;
		float f8;
		for (k = 0; k < w; ++k) {
			f7 = k / w;
			f8 = maxu + (minu - maxu) * f7 - f5;
			v5.addVertexWithUVColor(f7, 0.0D, 0.0F - t, f8, maxv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f7, 0.0D, 0.0D, f8, maxv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f7, 1.0D, 0.0D, f8, minv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f7, 1.0D, 0.0F - t, f8, minv, 0xffb5b5b5);
		}

		//v5.setNormal(1.0F, 0.0F, 0.0F);
		float f9;
		for (k = 0; k < w; ++k) {
			f7 = k / w;
			f8 = maxu + (minu - maxu) * f7 - f5;
			f9 = f7 + 1.0F / w;
			v5.addVertexWithUVColor(f9, 1.0D, 0.0F - t, f8, minv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f9, 1.0D, 0.0D, f8, minv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f9, 0.0D, 0.0D, f8, maxv, 0xffb5b5b5);
			v5.addVertexWithUVColor(f9, 0.0D, 0.0F - t, f8, maxv, 0xffb5b5b5);
		}

		//v5.setNormal(0.0F, 1.0F, 0.0F);
		for (k = 0; k < h; ++k) {
			f7 = k / h;
			f8 = maxv + (minv - maxv) * f7 - f6;
			f9 = f7 + 1.0F / h;
			v5.addVertexWithUVColor(0.0D, f9, 0.0D, maxu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(1.0D, f9, 0.0D, minu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(1.0D, f9, 0.0F - t, minu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(0.0D, f9, 0.0F - t, maxu, f8, 0xffb5b5b5);
		}

		//v5.setNormal(0.0F, -1.0F, 0.0F);
		for (k = 0; k < h; ++k) {
			f7 = k / h;
			f8 = maxv + (minv - maxv) * f7 - f6;
			v5.addVertexWithUVColor(1.0D, f7, 0.0D, minu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(0.0D, f7, 0.0D, maxu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(0.0D, f7, 0.0F - t, maxu, f8, 0xffb5b5b5);
			v5.addVertexWithUVColor(1.0D, f7, 0.0F - t, minu, f8, 0xffb5b5b5);
		}
	}

	public static void renderFrameBufferToItself(Framebuffer fb, int w, int h, ShaderProgram p/*, Matrix4f model, Matrix4f proj*/) {
		p.updateEnabled();
		if (!p.isEnabled())
			return;
		if (tempBuffer == null) {
			tempBuffer = new ScratchFramebuffer(w, h, true);
			tempBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		}
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);
		boolean flag = true;
		int pass = 0;
		while (flag) {
			tempBuffer.createBindFramebuffer(w, h);
			setRenderTarget(tempBuffer);
			//p.setMatrices(model, proj);
			//exportFramebuffer(fb, pass, p);
			flag = false;//ShaderRegistry.runShader(p);
			fb.framebufferRender(w, h);
			//ShaderRegistry.completeShader();
			setRenderTarget(fb);
			tempBuffer.framebufferRender(w, h);
			pass++;
		}
		//exportFramebuffer(fb, -1, p);
		GL11.glPopAttrib();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	}

	private static void exportFramebuffer(Framebuffer fb, int pass, ShaderProgram p) {
		if (!p.identifier.contains("reika"))
			return;
		try {
			File root = new File(Minecraft.getMinecraft().mcDataDir, "FramebufferExport");
			String name = pass == -1 ? p.identifier+"_end" : pass == 0 ? p.identifier+"_begin" : p.identifier+"_c_pass_"+pass;
			name = name+".png";
			File f = new File(new File(root, "screenshots"), name);
			if (f.exists())
				return;
			ScreenShotHelper.saveScreenshot(root, name, fb.framebufferWidth, fb.framebufferHeight, fb);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Supply null to make the screen the render target. */
	public static void setRenderTarget(Framebuffer fb) {
		OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, fb != null ? fb.framebufferObject : 0);
	}

	public static void renderTextureToFramebuffer(int texture, Framebuffer fb) {
		IntBuffer pixelBuffer = BufferUtils.createIntBuffer(fb.framebufferHeight*fb.framebufferWidth);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.framebufferTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, fb.framebufferWidth, fb.framebufferHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
	}

	public static class ScratchFramebuffer extends Framebuffer {

		public ScratchFramebuffer(int w, int h, boolean depth) {
			super(w, h, depth);
		}

		@Override
		public void createBindFramebuffer(int w, int h) {
			if (w != framebufferWidth || h != framebufferHeight)
				super.createBindFramebuffer(w, h);
		}

		public void clear() {
			/*
			IntBuffer pixelBuffer = BufferUtils.createIntBuffer(framebufferHeight*framebufferWidth);
			for (int i = 0; i < pixelBuffer.capacity(); i++) {
				pixelBuffer.put(i, 0xffffffff); //argb
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, framebufferWidth, framebufferHeight, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
			 */
			this.framebufferClear();
		}

		public void replaceWith(int texture) {
			ReikaRenderHelper.renderTextureToFramebuffer(texture, this);
		}

	}

	public static Matrix4f getModelviewMatrix() {
		return getMatrix(GL11.GL_MODELVIEW_MATRIX);
	}

	public static Matrix4f getProjectionMatrix() {
		return getMatrix(GL11.GL_PROJECTION_MATRIX);
	}

	public static Matrix4f getTextureMatrix() {
		return getMatrix(GL11.GL_TEXTURE_MATRIX);
	}

	private static Matrix4f getMatrix(int id) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(id, buf);
		buf.rewind();
		Matrix4f mat = new Matrix4f();
		mat.load(buf);
		return mat;
	}

}
