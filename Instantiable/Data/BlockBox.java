/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class BlockBox {

	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;

	public BlockBox(int min, int max) {
		this(min, min, min, max, max, max);
	}

	public BlockBox(int x0, int y0, int z0, int x1, int y1, int z1) {
		minX = Math.min(x0, x1);
		minY = Math.min(y0, y1);
		minZ = Math.min(z0, z1);

		maxX = Math.max(x0, x1);
		maxY = Math.max(y0, y1);
		maxZ = Math.max(z0, z1);
	}

	public BlockBox(WorldLocation loc, WorldLocation loc2) {
		this(loc.xCoord, loc.yCoord, loc.zCoord, loc2.xCoord, loc2.yCoord, loc2.zCoord);
	}

	public static BlockBox infinity() {
		return new BlockBox(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public int getSizeX() {
		return maxX-minX+1;
	}

	public int getSizeY() {
		return maxY-minY+1;
	}

	public int getSizeZ() {
		return maxZ-minZ+1;
	}

	public int getVolume() {
		return this.getSizeX()*this.getSizeY()*this.getSizeZ();
	}

	public BlockBox expand(int amt) {
		return this.expand(amt, amt, amt);
	}

	public BlockBox expand(int dx, int dy, int dz) {
		return new BlockBox(minX-dx, minY-dy, minZ-dz, maxX+dx, maxY+dy, maxZ+dz);
	}

	public BlockBox shift(ForgeDirection dir, int dist) {
		return this.shift(dist*dir.offsetX, dist*dir.offsetY, dist*dir.offsetZ);
	}

	public BlockBox shift(int dx, int dy, int dz) {
		return new BlockBox(minX+dx, minY+dy, minZ+dz, maxX+dx, maxY+dy, maxZ+dz);
	}

	public BlockBox clamp(ForgeDirection side, int value) {
		int minx = minX;
		int miny = minY;
		int minz = minZ;
		int maxx = maxX;
		int maxy = maxY;
		int maxz = maxX;
		switch(side) {
		case DOWN:
			miny = Math.max(value, miny);
			break;
		case UP:
			maxy = Math.min(value, maxy);
			break;
		case EAST:
			maxx = Math.min(value, maxx);
			break;
		case WEST:
			minx = Math.max(value, minx);
			break;
		case NORTH:
			minz = Math.max(value, minz);
			break;
		case SOUTH:
			maxz = Math.min(value, maxz);
			break;
		default:
			break;
		}
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public boolean isBlockInside(int x, int y, int z) {
		boolean ix = ReikaMathLibrary.isValueInsideBoundsIncl(minX, maxX, x);
		boolean iy = ReikaMathLibrary.isValueInsideBoundsIncl(minY, maxY, y);
		boolean iz = ReikaMathLibrary.isValueInsideBoundsIncl(minZ, maxZ, z);
		return ix && iy && iz;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BlockBox) {
			BlockBox b = (BlockBox)o;
			return b.maxX == maxX && b.maxY == maxY && b.maxZ == maxZ && b.minX == minX && b.minY == minY && b.minZ == minZ;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return minX+maxX+minY+maxY+minZ+maxZ;
	}

	@Override
	public String toString() {
		return String.format("%d, %d, %d >> %d, %d, %d", minX, minY, minZ, maxX, maxY, maxZ);
	}

	public AxisAlignedBB asAABB() {
		return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX+1, maxY+1, maxZ+1);
	}

}
