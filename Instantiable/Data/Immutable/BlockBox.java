/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

	public static BlockBox nothing() {
		return new BlockBox(Integer.MAX_VALUE, Integer.MIN_VALUE);
	}

	public static BlockBox block(int x, int y, int z) {
		return new BlockBox(x, y, z, x+1, y+1, z+1);
	}

	public static BlockBox block(TileEntity te) {
		return block(te.xCoord, te.yCoord, te.zCoord);
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

	public BlockBox expand(ForgeDirection dir, int amt) {
		int minx = minX;
		int miny = minY;
		int minz = minZ;
		int maxx = maxX;
		int maxy = maxY;
		int maxz = maxZ;
		switch(dir) {
			case EAST:
				maxx++;
				break;
			case WEST:
				minx--;
				break;
			case NORTH:
				minz--;
				break;
			case SOUTH:
				maxz++;
				break;
			case UP:
				maxy++;
				break;
			case DOWN:
				miny--;
				break;
			default:
				break;
		}
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
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

	public BlockBox contract(ForgeDirection dir, int amt) {
		int minx = minX;
		int miny = minY;
		int minz = minZ;
		int maxx = maxX;
		int maxy = maxY;
		int maxz = maxZ;
		switch(dir) {
			case EAST:
				maxx--;
				break;
			case WEST:
				minx++;
				break;
			case NORTH:
				minz++;
				break;
			case SOUTH:
				maxz--;
				break;
			case UP:
				maxy--;
				break;
			case DOWN:
				miny++;
				break;
			default:
				break;
		}
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public BlockBox contract(int dx, int dy, int dz) {
		return new BlockBox(minX+dx, minY+dy, minZ+dz, maxX-dx, maxY-dy, maxZ-dz);
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

	public BlockBox combineWith(BlockBox box) {
		int minX = Math.min(this.minX, box.minX);
		int minY = Math.min(this.minY, box.minY);
		int minZ = Math.min(this.minZ, box.minZ);
		int maxX = Math.max(this.maxX, box.maxX);
		int maxY = Math.max(this.maxY, box.maxY);
		int maxZ = Math.max(this.maxZ, box.maxZ);
		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public boolean isBlockInside(Coordinate c) {
		return this.isBlockInside(c.xCoord, c.yCoord, c.zCoord);
	}

	public boolean isBlockInside(int x, int y, int z) {
		boolean ix = ReikaMathLibrary.isValueInsideBoundsIncl(minX, maxX-1, x);
		boolean iy = ReikaMathLibrary.isValueInsideBoundsIncl(minY, maxY-1, y);
		boolean iz = ReikaMathLibrary.isValueInsideBoundsIncl(minZ, maxZ-1, z);
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

	public static BlockBox readFromNBT(NBTTagCompound tag) {
		int minx = tag.getInteger("minx");
		int miny = tag.getInteger("miny");
		int minz = tag.getInteger("minz");
		int maxx = tag.getInteger("maxx");
		int maxy = tag.getInteger("maxy");
		int maxz = tag.getInteger("maxz");
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("minx", minX);
		tag.setInteger("miny", minY);
		tag.setInteger("minz", minZ);
		tag.setInteger("maxx", maxX);
		tag.setInteger("maxy", maxY);
		tag.setInteger("maxz", maxZ);
	}

	public BlockBox offset(Coordinate offset) {
		return this.offset(offset.xCoord, offset.yCoord, offset.zCoord);
	}

	public BlockBox offset(int x, int y, int z) {
		return new BlockBox(minX+x, minY+y, minZ+z, maxX+x, maxY+y, maxZ+z);
	}

	public Coordinate getRandomContainedCoordinate(Random rand) {
		return new Coordinate(minX+rand.nextInt(maxX-minX+1), minY+rand.nextInt(maxY-minY+1), minZ+rand.nextInt(maxZ-minZ+1));
	}

}
