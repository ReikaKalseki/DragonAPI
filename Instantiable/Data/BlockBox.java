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

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import net.minecraftforge.common.util.ForgeDirection;

public final class BlockBox {

	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;

	public BlockBox() {
		this(0, 0);
	}

	public BlockBox(int min, int max) {
		this(min, min, min, max, max, max);
	}

	public BlockBox(int x0, int y0, int z0, int x1, int y1, int z1) {
		minX = x0;
		minY = y0;
		minZ = z0;

		maxX = x1;
		maxY = y1;
		maxZ = z1;
	}

	public static BlockBox infinity() {
		return new BlockBox(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public int getSizeX() {
		return maxX-minX;
	}

	public int getSizeY() {
		return maxY-minY;
	}

	public int getSizeZ() {
		return maxZ-minZ;
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

}
