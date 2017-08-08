/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaAABBHelper extends DragonAPICore {

	/** Renders an AABB bounding box in the world. Very useful for debug purposes, or as a user-friendliness feature.
	 * Args: World, AABB, Render par2,4,6, x,y,z of machine, root alpha value (-ve for solid color), RGB, solid outline yes/no */
	@SideOnly(Side.CLIENT)
	public static void renderAABB(AxisAlignedBB box, double par2, double par4, double par6, int x, int y, int z, int a, int r, int g, int b, boolean line) {
		int[] color = {r, g, b, a};
		ReikaRenderHelper.prepareGeoDraw(true);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6 + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		if (color[3] > 255 && color[3] > 0)
			color[3] = 255;
		if (color[3] < 0)
			color[3] *= -1;
		boolean filled = true;
		Tessellator var5 = Tessellator.instance;
		double xdiff = box.minX-x;
		double ydiff = box.minY-y;
		double zdiff = box.minZ-z;
		double xdiff2 = box.maxX-x;
		double ydiff2 = box.maxY-y;
		double zdiff2 = box.maxZ-z;

		double px = par2+xdiff;
		double py = par4+ydiff;
		double pz = par6+zdiff;
		double px2 = par2+xdiff2;
		double py2 = par4+ydiff2;
		double pz2 = par6+zdiff2;
		if (var5.isDrawing)
			var5.draw();
		if (line) {
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px2, py2, pz);
			var5.addVertex(px, py2, pz);
			var5.addVertex(px, py2, pz2);
			var5.addVertex(px2, py2, pz2);
			var5.draw();
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px2, py, pz);
			var5.addVertex(px, py, pz);
			var5.addVertex(px, py, pz2);
			var5.addVertex(px2, py, pz2);
			var5.draw();
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px, py, pz);
			var5.addVertex(px, py2, pz);
			var5.draw();
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px2, py, pz);
			var5.addVertex(px2, py2, pz);
			var5.draw();
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px2, py, pz2);
			var5.addVertex(px2, py2, pz2);
			var5.draw();
			var5.startDrawing(GL11.GL_LINE_LOOP);
			var5.setColorRGBA(color[0], color[1], color[2], color[3]);
			var5.addVertex(px, py, pz2);
			var5.addVertex(px, py2, pz2);
			var5.draw();
		}
		if (filled)
		{
			var5.startDrawing(GL11.GL_QUADS);
			//var5.setBrightness(255);
			var5.setColorRGBA(color[0], color[1], color[2], (int)(color[3]*0.375F));

			var5.addVertex(px, py, pz);
			var5.addVertex(px2, py, pz);
			var5.addVertex(px2, py, pz2);
			var5.addVertex(px, py, pz2);

			var5.addVertex(px2, py, pz);
			var5.addVertex(px2, py2, pz);
			var5.addVertex(px2, py2, pz2);
			var5.addVertex(px2, py, pz2);

			var5.addVertex(px, py2, pz);
			var5.addVertex(px, py, pz);
			var5.addVertex(px, py, pz2);
			var5.addVertex(px, py2, pz2);

			var5.addVertex(px, py2, pz2);
			var5.addVertex(px, py, pz2);
			var5.addVertex(px2, py, pz2);
			var5.addVertex(px2, py2, pz2);

			var5.addVertex(px, py, pz);
			var5.addVertex(px, py2, pz);
			var5.addVertex(px2, py2, pz);
			var5.addVertex(px2, py, pz);

			var5.addVertex(px2, py2, pz);
			var5.addVertex(px, py2, pz);
			var5.addVertex(px, py2, pz2);
			var5.addVertex(px2, py2, pz2);
			var5.draw();
		}

		ReikaRenderHelper.exitGeoDraw();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static AxisAlignedBB getBlockAABB(TileEntity te) {
		return getBlockAABB(te.xCoord, te.yCoord, te.zCoord);
	}

	/** Returns a 1-block bounding box. Args: x, y, z */
	public static AxisAlignedBB getBlockAABB(int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
	}

	public static AxisAlignedBB getZeroAABB() {
		return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	/** Returns a sized bounding box centered on a Blocks. Args: x, y, z */
	public static AxisAlignedBB getBlockCenteredAABB(int x, int y, int z, double range) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(range, range, range);
	}

	public static AxisAlignedBB getEntityCenteredAABB(Entity e, double range) {
		return AxisAlignedBB.getBoundingBox(e.posX, e.posY, e.posZ, e.posX, e.posY, e.posZ).expand(range, range, range);
	}

	public static AxisAlignedBB getSizedBlockAABB(int x, int y, int z, float size) {
		size = size/2F;
		return AxisAlignedBB.getBoundingBox(x+0.5-size, y+0.5-size, z+0.5-size, x+0.5+size, y+0.5+size, z+0.5+size);
	}

	public static HashSet<Coordinate> getBlocksIntersectingAABB(AxisAlignedBB box, World world, boolean checkCollideable) {
		HashSet<Coordinate> c = new HashSet();
		int minX = MathHelper.floor_double(box.minX);
		int minY = MathHelper.floor_double(box.minY);
		int minZ = MathHelper.floor_double(box.minZ);
		int maxX = MathHelper.floor_double(box.maxX);
		int maxY = MathHelper.floor_double(box.maxY);
		int maxZ = MathHelper.floor_double(box.maxZ);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block b = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					if (checkCollideable) {
						if (!b.isCollidable())
							continue;
						if (!b.canCollideCheck(meta, false))
							continue;
					}
					if (x == minX && box.minX >= x+b.getBlockBoundsMaxX()) {
						continue;
					}
					else if (x == maxX && box.maxX <= x+b.getBlockBoundsMinX()) {
						continue;
					}
					else if (y == minY && box.minY >= y+b.getBlockBoundsMaxY()) {
						continue;
					}
					else if (y == maxY && box.maxY <= y+b.getBlockBoundsMinY()) {
						continue;
					}
					else if (z == minZ && box.minZ >= z+b.getBlockBoundsMaxZ()) {
						continue;
					}
					else if (z == maxZ && box.maxZ <= z+b.getBlockBoundsMinZ()) {
						continue;
					}
					c.add(new Coordinate(x, y, z));
				}
			}
		}
		return c;
	}

	public static double getVolume(AxisAlignedBB box) {
		return (box.maxX-box.minX)*(box.maxY-box.minY)*(box.maxZ-box.minZ);
	}

	public static AxisAlignedBB structureToAABB(StructureBoundingBox box) {
		return AxisAlignedBB.getBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public static AxisAlignedBB getBeamBox(int x, int y, int z, ForgeDirection dir, int dist) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		int dx = dir.offsetX*dist;
		int dy = dir.offsetY*dist;
		int dz = dir.offsetZ*dist;
		box = box.addCoord(dx, dy, dz);
		return box;
	}

	public static AxisAlignedBB getBeamBox(int x, int y, int z, ForgeDirection dir, int d1, int d2) {
		int x1 = x+dir.offsetX*d1;
		int y1 = y+dir.offsetY*d1;
		int z1 = z+dir.offsetZ*d1;
		int x2 = dir.offsetX*d2;
		int y2 = dir.offsetY*d2;
		int z2 = dir.offsetZ*d2;
		return getBeamBox(x1, y1, z1, x2, y2, z2);
	}

	public static AxisAlignedBB getBeamBox(int x1, int y1, int z1, int x2, int y2, int z2) {
		return getBlockAABB(x1, y1, z1).addCoord(x2, y2, z2);
	}

	public static AxisAlignedBB copyAABB(AxisAlignedBB box) {
		return AxisAlignedBB.getBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public static AxisAlignedBB fromPoints(DecimalPosition... points) {
		double[] limits = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		for (int i = 0; i < points.length; i++) {
			limits[0] = Math.min(limits[0], points[i].xCoord);
			limits[1] = Math.min(limits[1], points[i].yCoord);
			limits[2] = Math.min(limits[2], points[i].zCoord);
			limits[3] = Math.max(limits[3], points[i].xCoord);
			limits[4] = Math.max(limits[4], points[i].yCoord);
			limits[5] = Math.max(limits[5], points[i].zCoord);
		}
		return AxisAlignedBB.getBoundingBox(limits[0], limits[1], limits[2], limits[3], limits[4], limits[5]);
	}

	public static AxisAlignedBB scaleAABB(AxisAlignedBB box, double sx, double sy, double sz) {
		double dx = sx*(box.maxX-box.minX);
		double dy = sy*(box.maxY-box.minY);
		double dz = sz*(box.maxZ-box.minZ);
		return box.expand(dx, dy, dz);
	}

}
