/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.ReikaModelledBreakFX;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;

public final class ReikaRenderHelper extends DragonAPICore {

	/** Converts an RGB array into a color multiplier. Args: RGB[], bit */
	public static float RGBtoColorMultiplier(int[] RGB, int bit) {
		float color = 1F;
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;
		return color;
	}

	/** Converts a hex color code to a color multiplier. Args: Hex, bit */
	public static float HextoColorMultiplier(int hex, int bit) {
		float color = 1F;
		int[] RGB = ReikaColorAPI.HexToRGB(hex);
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;
		return color;
	}

	/** Converts a biome to a color multiplier (for use in things like leaf textures).
	 * Args: World, x, z, material (grass, water, etc), bit */
	public static float biomeToColorMultiplier(World world, int x, int z, String mat, int bit) {
		int[] color = ReikaWorldHelper.biomeToRGB(world, x, z, mat);
		float mult = RGBtoColorMultiplier(color, bit);
		return mult;
	}

	/** Renders a flat circle in the world. Args: radius, center x,y,z, RGB*/
	public static void renderCircle(double r, double x, double y, double z, int[] color) {
		prepareGeoDraw(false);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = new Tessellator();
		if (var5.isDrawing)
			var5.draw();
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA(color[0], color[1], color[2], 255);
		for (int i = 0; i < 360; i++) {
			var5.addVertex(x+r*Math.cos(ReikaPhysicsHelper.degToRad(i)), y, z+r*Math.sin(ReikaPhysicsHelper.degToRad(i)));
		}
		var5.draw();
		exitGeoDraw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** Renders a vertical-plane circle in the world. Args: radius, center x,y,z, RGB, phi */
	public static void renderVCircle(double r, double x, double y, double z, int[] color, double phi) {
		prepareGeoDraw(false);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = new Tessellator();
		//var5.setColorRGBA(color[0], color[1], color[2], 255);
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA(color[0], color[1], color[2], 255);
		for (int i = 0; i < 360; i++) {
			int sign = 1;
			double h = r*Math.cos(ReikaPhysicsHelper.degToRad(i));
			if (i >= 180)
				sign = -1;
			var5.addVertex(x-Math.sin(phi)*(sign)*(Math.sqrt(r*r-h*h)), y+r*Math.cos(ReikaPhysicsHelper.degToRad(i)), z+r*Math.sin(ReikaPhysicsHelper.degToRad(i))*Math.cos(phi));
		}
		var5.draw();
		exitGeoDraw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** Renders a line between two points in the world. Args: Start xyz, End xyz, rgb */
	public static void renderLine(double x1, double y1, double z1, double x2, double y2, double z2, int[] color) {
		prepareGeoDraw(false);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Tessellator var5 = new Tessellator();
		if (var5.isDrawing)
			var5.draw();
		var5.startDrawing(GL11.GL_LINE_LOOP);
		var5.setColorRGBA(color[0], color[1], color[2], 255);
		var5.addVertex(x1, y1, z1);
		var5.addVertex(x2, y2, z2);
		var5.draw();
		exitGeoDraw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableLighting() {
		Minecraft.getMinecraft().entityRenderer.disableLightmap(1);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	public static void enableLighting() {
		Minecraft.getMinecraft().entityRenderer.enableLightmap(1);
		RenderHelper.enableStandardItemLighting();
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
		prepareGeoDraw(a < 255);
		Tessellator v5 = new Tessellator();
		v5.startDrawingQuads();
		v5.setColorRGBA(r, g, b, a);
		v5.addVertex(x1, y1, z1);
		v5.addVertex(x2, y1, z2);
		v5.addVertex(x2, y2, z2);
		v5.addVertex(x1, y2, z1);
		v5.draw();
		exitGeoDraw();
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
	public static boolean addModelledBlockParticles(String basedir, World world, int x, int y, int z, Block b, EffectRenderer eff, List<double[]> allowedRegions) {
		String name = null;
		if (world.getBlockId(x, y, z) == b.blockID) {
			TileEntity t = world.getBlockTileEntity(x, y, z);
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
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), Minecraft.getMinecraft().renderEngine, file, px, py));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockHitEffects!
	 * Args: Base path (contains TE textures, world, MovingObjectPosition, Block, EffectRenderer, Allowed Texture Regions <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticles(String basedir, World world, MovingObjectPosition mov, Block b, EffectRenderer eff, List<double[]> allowedRegions) {
		if (mov == null)
			return false;
		int x = mov.blockX;
		int y = mov.blockY;
		int z = mov.blockZ;
		String name = null;
		if (world.getBlockId(x, y, z) == b.blockID) {
			TileEntity t = world.getBlockTileEntity(x, y, z);
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
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), Minecraft.getMinecraft().renderEngine, file, px, py));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockDestroyEffects!
	 * Args: Texture Path, world, x, y, z, Block, EffectRenderer, Allowed Texture Regions <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticlesDirect(String texture, World world, int x, int y, int z, Block b, EffectRenderer eff, List<double[]> allowedRegions) {
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
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), Minecraft.getMinecraft().renderEngine, texture, px, py));
		}
		return true;
	}

	/** Renders break particles for custom-rendered TileEntities. Call this one from BlockHitEffects!
	 * Args: Texture Path, world, MovingObjectPosition, Block, EffectRenderer, Allowed Texture Regions. <br>
	 * See addModelledBlockParticles(basedir, world, x, y, z, b, eff, allowedRegions) for explanation of the regions. */
	public static boolean addModelledBlockParticlesDirect(String texture, World world, MovingObjectPosition mov, Block b, EffectRenderer eff, List<double[]> allowedRegions) {
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
			eff.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, 0, world.getBlockMetadata(x, y, z), Minecraft.getMinecraft().renderEngine, texture, px, py));
		}
		return true;
	}

	public static void spawnInfusedDropParticles(World world, int x, int y, int z, int thaumOreID) {
		Block ore = Block.blocksList[thaumOreID];
		Icon ico = new RenderBlocks().getBlockIcon(ore);
		for (int i = 0; i < 16; i++) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, ore, world.getBlockMetadata(x, y, z), 0, Minecraft.getMinecraft().renderEngine, "/terrain.png", ico.getInterpolatedU(0), ico.getInterpolatedV(0)));
		}
	}

	public static void spawnDropParticles(World world, int x, int y, int z, Block b, int meta) {
		Icon ico = new RenderBlocks().getBlockIcon(b);
		for (int i = 0; i < 16; i++) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new ReikaModelledBreakFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), -1+rand.nextDouble()*2, 2, -1+rand.nextDouble()*2, b, meta, 0, Minecraft.getMinecraft().renderEngine, "/terrain.png", ico.getInterpolatedU(0), ico.getInterpolatedV(0)));
		}
	}

}
