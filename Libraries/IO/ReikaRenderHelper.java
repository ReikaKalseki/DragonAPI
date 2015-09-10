/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Effects.ReikaModelledBreakFX;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileModel;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaRenderHelper extends DragonAPICore {

	private static final RenderBlocks rb = new RenderBlocks();

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
	public static float biomeToColorMultiplier(World world, int x, int y, int z, String mat, int bit) {
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
			var5.addVertex(x+r*Math.cos(Math.toRadians(i)), y, z+r*Math.sin(Math.toRadians(i)));
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

	public static void renderTube(double x1, double y1, double z1, double x2, double y2, double z2, int c1, int c2, double r1, double r2) {
		Tessellator v5 = Tessellator.instance;

		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;

		GL11.glPushMatrix();

		GL11.glTranslated(x1, y1, z1);

		double f7 = Math.sqrt(dx*dx+dz*dz);
		double f8 = Math.sqrt(dx*dx+dy*dy+dz*dz);
		double ang1 = -Math.atan2(dz, dx) * 180 / Math.PI - 90;
		double ang2 = -Math.atan2(f7, dy) * 180 / Math.PI - 90;
		GL11.glRotated(ang1, 0, 1, 0);
		GL11.glRotated(ang2, 1, 0, 0);

		int sides = 16;

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
	 * Explanation of Allowed Regions - Expects a list of size-4 double arrays, whose elements are as follows:<br>
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
		for (int i = 0; i < 16; i++) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, meta, 0));
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

				rb.aoBrightnessXYNN = b.getMixedBrightnessForBlock(iba, x - 1, y, z);
				rb.aoBrightnessYZNN = b.getMixedBrightnessForBlock(iba, x, y, z - 1);
				rb.aoBrightnessYZNP = b.getMixedBrightnessForBlock(iba, x, y, z + 1);
				rb.aoBrightnessXYPN = b.getMixedBrightnessForBlock(iba, x + 1, y, z);
				rb.aoLightValueScratchXYNN = iba.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNN = iba.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNP = iba.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPN = iba.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
				flag2 = iba.getBlock(x + 1, y - 1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x - 1, y - 1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x, y - 1, z + 1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y - 1, z - 1).getCanBlockGrass();

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXYNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXYNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x - 1, y, z - 1);
				}

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXYNN;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXYNN;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x - 1, y, z + 1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXYPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXYPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x + 1, y, z - 1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXYPN;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXYPN;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x + 1, y, z + 1);
				}

				if (rb.renderMinY <= 0.0D) {
					++y;
				}

				i1 = l;

				if (rb.renderMinY <= 0.0D || !iba.getBlock(x, y - 1, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y - 1, z);
				}

				f7 = iba.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXYZNNP + rb.aoLightValueScratchXYNN + rb.aoLightValueScratchYZNP + f7) / 4.0F;
				f6 = (rb.aoLightValueScratchYZNP + f7 + rb.aoLightValueScratchXYZPNP + rb.aoLightValueScratchXYPN) / 4.0F;
				f5 = (f7 + rb.aoLightValueScratchYZNN + rb.aoLightValueScratchXYPN + rb.aoLightValueScratchXYZPNN) / 4.0F;
				f4 = (rb.aoLightValueScratchXYNN + rb.aoLightValueScratchXYZNNN + f7 + rb.aoLightValueScratchYZNN) / 4.0F;
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

				rb.aoBrightnessXYNP = b.getMixedBrightnessForBlock(iba, x - 1, y, z);
				rb.aoBrightnessXYPP = b.getMixedBrightnessForBlock(iba, x + 1, y, z);
				rb.aoBrightnessYZPN = b.getMixedBrightnessForBlock(iba, x, y, z - 1);
				rb.aoBrightnessYZPP = b.getMixedBrightnessForBlock(iba, x, y, z + 1);
				rb.aoLightValueScratchXYNP = iba.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPP = iba.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPN = iba.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPP = iba.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
				flag2 = iba.getBlock(x + 1, y + 1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x - 1, y + 1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x, y + 1, z + 1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y + 1, z - 1).getCanBlockGrass();

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXYNP;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXYNP;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x - 1, y, z - 1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXYPP;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXYPP;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x + 1, y, z - 1);
				}

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXYNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXYNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x - 1, y, z + 1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXYPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXYPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x + 1, y, z + 1);
				}

				if (rb.renderMaxY >= 1.0D) {
					--y;
				}

				i1 = l;

				if (rb.renderMaxY >= 1.0D || !iba.getBlock(x, y + 1, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y + 1, z);
				}

				f7 = iba.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
				f6 = (rb.aoLightValueScratchXYZNPP + rb.aoLightValueScratchXYNP + rb.aoLightValueScratchYZPP + f7) / 4.0F;
				f3 = (rb.aoLightValueScratchYZPP + f7 + rb.aoLightValueScratchXYZPPP + rb.aoLightValueScratchXYPP) / 4.0F;
				f4 = (f7 + rb.aoLightValueScratchYZPN + rb.aoLightValueScratchXYPP + rb.aoLightValueScratchXYZPPN) / 4.0F;
				f5 = (rb.aoLightValueScratchXYNP + rb.aoLightValueScratchXYZNPN + f7 + rb.aoLightValueScratchYZPN) / 4.0F;
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

				rb.aoLightValueScratchXZNN = iba.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNN = iba.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPN = iba.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPN = iba.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXZNN = b.getMixedBrightnessForBlock(iba, x - 1, y, z);
				rb.aoBrightnessYZNN = b.getMixedBrightnessForBlock(iba, x, y - 1, z);
				rb.aoBrightnessYZPN = b.getMixedBrightnessForBlock(iba, x, y + 1, z);
				rb.aoBrightnessXZPN = b.getMixedBrightnessForBlock(iba, x + 1, y, z);
				flag2 = iba.getBlock(x + 1, y, z - 1).getCanBlockGrass();
				flag3 = iba.getBlock(x - 1, y, z - 1).getCanBlockGrass();
				flag4 = iba.getBlock(x, y + 1, z - 1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y - 1, z - 1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x - 1, y - 1, z);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x - 1, y + 1, z);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x + 1, y - 1, z);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x + 1, y + 1, z);
				}

				if (rb.renderMinZ <= 0.0D) {
					++z;
				}

				i1 = l;

				if (rb.renderMinZ <= 0.0D || !iba.getBlock(x, y, z - 1).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y, z - 1);
				}

				f7 = iba.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXZNN + rb.aoLightValueScratchXYZNPN + f7 + rb.aoLightValueScratchYZPN) / 4.0F;
				f4 = (f7 + rb.aoLightValueScratchYZPN + rb.aoLightValueScratchXZPN + rb.aoLightValueScratchXYZPPN) / 4.0F;
				f5 = (rb.aoLightValueScratchYZNN + f7 + rb.aoLightValueScratchXYZPNN + rb.aoLightValueScratchXZPN) / 4.0F;
				f6 = (rb.aoLightValueScratchXYZNNN + rb.aoLightValueScratchXZNN + rb.aoLightValueScratchYZNN + f7) / 4.0F;
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

				rb.aoLightValueScratchXZNP = iba.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPP = iba.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZNP = iba.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchYZPP = iba.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXZNP = b.getMixedBrightnessForBlock(iba, x - 1, y, z);
				rb.aoBrightnessXZPP = b.getMixedBrightnessForBlock(iba, x + 1, y, z);
				rb.aoBrightnessYZNP = b.getMixedBrightnessForBlock(iba, x, y - 1, z);
				rb.aoBrightnessYZPP = b.getMixedBrightnessForBlock(iba, x, y + 1, z);
				flag2 = iba.getBlock(x + 1, y, z + 1).getCanBlockGrass();
				flag3 = iba.getBlock(x - 1, y, z + 1).getCanBlockGrass();
				flag4 = iba.getBlock(x, y + 1, z + 1).getCanBlockGrass();
				flag5 = iba.getBlock(x, y - 1, z + 1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x - 1, y - 1, z);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x - 1, y + 1, z);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x + 1, y - 1, z);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x + 1, y + 1, z);
				}

				if (rb.renderMaxZ >= 1.0D) {
					--z;
				}

				i1 = l;

				if (rb.renderMaxZ >= 1.0D || !iba.getBlock(x, y, z + 1).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x, y, z + 1);
				}

				f7 = iba.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXZNP + rb.aoLightValueScratchXYZNPP + f7 + rb.aoLightValueScratchYZPP) / 4.0F;
				f6 = (f7 + rb.aoLightValueScratchYZPP + rb.aoLightValueScratchXZPP + rb.aoLightValueScratchXYZPPP) / 4.0F;
				f5 = (rb.aoLightValueScratchYZNP + f7 + rb.aoLightValueScratchXYZPNP + rb.aoLightValueScratchXZPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXYZNNP + rb.aoLightValueScratchXZNP + rb.aoLightValueScratchYZNP + f7) / 4.0F;
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

				rb.aoLightValueScratchXYNN = iba.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZNN = iba.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZNP = iba.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYNP = iba.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXYNN = b.getMixedBrightnessForBlock(iba, x, y - 1, z);
				rb.aoBrightnessXZNN = b.getMixedBrightnessForBlock(iba, x, y, z - 1);
				rb.aoBrightnessXZNP = b.getMixedBrightnessForBlock(iba, x, y, z + 1);
				rb.aoBrightnessXYNP = b.getMixedBrightnessForBlock(iba, x, y + 1, z);
				flag2 = iba.getBlock(x - 1, y + 1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x - 1, y - 1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x - 1, y, z - 1).getCanBlockGrass();
				flag5 = iba.getBlock(x - 1, y, z + 1).getCanBlockGrass();

				if (!flag4 && !flag3) {
					rb.aoLightValueScratchXYZNNN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNNN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNNN = iba.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNN = b.getMixedBrightnessForBlock(iba, x, y - 1, z - 1);
				}

				if (!flag5 && !flag3) {
					rb.aoLightValueScratchXYZNNP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNNP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNNP = iba.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNNP = b.getMixedBrightnessForBlock(iba, x, y - 1, z + 1);
				}

				if (!flag4 && !flag2) {
					rb.aoLightValueScratchXYZNPN = rb.aoLightValueScratchXZNN;
					rb.aoBrightnessXYZNPN = rb.aoBrightnessXZNN;
				}
				else {
					rb.aoLightValueScratchXYZNPN = iba.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPN = b.getMixedBrightnessForBlock(iba, x, y + 1, z - 1);
				}

				if (!flag5 && !flag2) {
					rb.aoLightValueScratchXYZNPP = rb.aoLightValueScratchXZNP;
					rb.aoBrightnessXYZNPP = rb.aoBrightnessXZNP;
				}
				else {
					rb.aoLightValueScratchXYZNPP = iba.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZNPP = b.getMixedBrightnessForBlock(iba, x, y + 1, z + 1);
				}

				if (rb.renderMinX <= 0.0D) {
					++x;
				}

				i1 = l;

				if (rb.renderMinX <= 0.0D || !iba.getBlock(x - 1, y, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x - 1, y, z);
				}

				f7 = iba.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
				f6 = (rb.aoLightValueScratchXYNN + rb.aoLightValueScratchXYZNNP + f7 + rb.aoLightValueScratchXZNP) / 4.0F;
				f3 = (f7 + rb.aoLightValueScratchXZNP + rb.aoLightValueScratchXYNP + rb.aoLightValueScratchXYZNPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXZNN + f7 + rb.aoLightValueScratchXYZNPN + rb.aoLightValueScratchXYNP) / 4.0F;
				f5 = (rb.aoLightValueScratchXYZNNN + rb.aoLightValueScratchXYNN + rb.aoLightValueScratchXZNN + f7) / 4.0F;
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

				rb.aoLightValueScratchXYPN = iba.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPN = iba.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXZPP = iba.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
				rb.aoLightValueScratchXYPP = iba.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
				rb.aoBrightnessXYPN = b.getMixedBrightnessForBlock(iba, x, y - 1, z);
				rb.aoBrightnessXZPN = b.getMixedBrightnessForBlock(iba, x, y, z - 1);
				rb.aoBrightnessXZPP = b.getMixedBrightnessForBlock(iba, x, y, z + 1);
				rb.aoBrightnessXYPP = b.getMixedBrightnessForBlock(iba, x, y + 1, z);
				flag2 = iba.getBlock(x + 1, y + 1, z).getCanBlockGrass();
				flag3 = iba.getBlock(x + 1, y - 1, z).getCanBlockGrass();
				flag4 = iba.getBlock(x + 1, y, z + 1).getCanBlockGrass();
				flag5 = iba.getBlock(x + 1, y, z - 1).getCanBlockGrass();

				if (!flag3 && !flag5) {
					rb.aoLightValueScratchXYZPNN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPNN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPNN = iba.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNN = b.getMixedBrightnessForBlock(iba, x, y - 1, z - 1);
				}

				if (!flag3 && !flag4) {
					rb.aoLightValueScratchXYZPNP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPNP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPNP = iba.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPNP = b.getMixedBrightnessForBlock(iba, x, y - 1, z + 1);
				}

				if (!flag2 && !flag5) {
					rb.aoLightValueScratchXYZPPN = rb.aoLightValueScratchXZPN;
					rb.aoBrightnessXYZPPN = rb.aoBrightnessXZPN;
				}
				else {
					rb.aoLightValueScratchXYZPPN = iba.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPN = b.getMixedBrightnessForBlock(iba, x, y + 1, z - 1);
				}

				if (!flag2 && !flag4) {
					rb.aoLightValueScratchXYZPPP = rb.aoLightValueScratchXZPP;
					rb.aoBrightnessXYZPPP = rb.aoBrightnessXZPP;
				}
				else {
					rb.aoLightValueScratchXYZPPP = iba.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
					rb.aoBrightnessXYZPPP = b.getMixedBrightnessForBlock(iba, x, y + 1, z + 1);
				}

				if (rb.renderMaxX >= 1.0D) {
					--x;
				}

				i1 = l;

				if (rb.renderMaxX >= 1.0D || !iba.getBlock(x + 1, y, z).isOpaqueCube()) {
					i1 = b.getMixedBrightnessForBlock(iba, x + 1, y, z);
				}

				f7 = iba.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
				f3 = (rb.aoLightValueScratchXYPN + rb.aoLightValueScratchXYZPNP + f7 + rb.aoLightValueScratchXZPP) / 4.0F;
				f4 = (rb.aoLightValueScratchXYZPNN + rb.aoLightValueScratchXYPN + rb.aoLightValueScratchXZPN + f7) / 4.0F;
				f5 = (rb.aoLightValueScratchXZPN + f7 + rb.aoLightValueScratchXYZPPN + rb.aoLightValueScratchXYPP) / 4.0F;
				f6 = (f7 + rb.aoLightValueScratchXZPP + rb.aoLightValueScratchXYPP + rb.aoLightValueScratchXYZPPP) / 4.0F;
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

}
