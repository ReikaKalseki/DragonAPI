/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import buildcraft.api.core.IAreaProvider;

public final class BlockBox {

	public final int minX;
	public final int minY;
	public final int minZ;
	public final int maxX;
	public final int maxY;
	public final int maxZ;

	private boolean isEmpty = false;

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
		BlockBox ret = new BlockBox(0, 0);
		ret.isEmpty = true;
		return ret;
	}

	public static BlockBox origin() {
		return new BlockBox(-1, 1); //not 0,1
	}

	public static BlockBox block(int x, int y, int z) {
		return new BlockBox(x, y, z, x+1, y+1, z+1);
	}

	public static BlockBox block(TileEntity te) {
		return block(te.xCoord, te.yCoord, te.zCoord);
	}

	public static BlockBox block(ChunkCoordinates cc) {
		return block(cc.posX, cc.posY, cc.posZ);
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

	public int getSurfaceArea() {
		return 2*this.getSizeX()+2*this.getSizeY()+2*this.getSizeZ();
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
				maxx += amt;
				break;
			case WEST:
				minx -= amt;
				break;
			case NORTH:
				minz -= amt;
				break;
			case SOUTH:
				maxz += amt;
				break;
			case UP:
				maxy += amt;
				break;
			case DOWN:
				miny -= amt;
				break;
			default:
				break;
		}
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public BlockBox expandScale(double sx, double sy, double sz) {
		int midX = (minX+maxX)/2;
		int midY = (minY+maxY)/2;
		int midZ = (minZ+maxZ)/2;
		int nx = MathHelper.floor_double(midX-sx*(midX-minX));
		int px = MathHelper.ceiling_double_int(midX+sx*(maxX-midX));
		int ny = MathHelper.floor_double(midY-sy*(midY-minY));
		int py = MathHelper.ceiling_double_int(midY+sy*(maxY-midY));
		int nz = MathHelper.floor_double(midZ-sz*(midZ-minZ));
		int pz = MathHelper.ceiling_double_int(midZ+sz*(maxZ-midZ));
		return new BlockBox(nx, ny, nz, px, py, pz);
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
				maxx -= amt;
				break;
			case WEST:
				minx += amt;
				break;
			case NORTH:
				minz += amt;
				break;
			case SOUTH:
				maxz -= amt;
				break;
			case UP:
				maxy -= amt;
				break;
			case DOWN:
				miny += amt;
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

	public BlockBox clamp(ForgeDirection side, int x0, int y0, int z0, int dist) {
		int minx = minX;
		int miny = minY;
		int minz = minZ;
		int maxx = maxX;
		int maxy = maxY;
		int maxz = maxX;
		switch(side) {
			case DOWN:
				miny = Math.max(y0-dist, miny);
				break;
			case UP:
				maxy = Math.min(y0+1+dist, maxy);
				break;
			case EAST:
				maxx = Math.min(x0+1+dist, maxx);
				break;
			case WEST:
				minx = Math.max(x0-dist, minx);
				break;
			case NORTH:
				minz = Math.max(z0-dist, minz);
				break;
			case SOUTH:
				maxz = Math.min(z0+1+dist, maxz);
				break;
			default:
				break;
		}
		return new BlockBox(minx, miny, minz, maxx, maxy, maxz);
	}

	public BlockBox clampTo(BlockBox box) {
		int minX = Math.max(this.minX, box.minX);
		int minY = Math.max(this.minY, box.minY);
		int minZ = Math.max(this.minZ, box.minZ);
		int maxX = Math.min(this.maxX, box.maxX);
		int maxY = Math.min(this.maxY, box.maxY);
		int maxZ = Math.min(this.maxZ, box.maxZ);
		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
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

	public BlockBox addCoordinate(int x, int y, int z) {
		if (isEmpty) {
			return block(x, y, z);
		}
		int minX = Math.min(this.minX, x);
		int minY = Math.min(this.minY, y);
		int minZ = Math.min(this.minZ, z);
		int maxX = Math.max(this.maxX, x+1);
		int maxY = Math.max(this.maxY, y+1);
		int maxZ = Math.max(this.maxZ, z+1);
		return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public boolean isBlockInside(Coordinate c) {
		return this.isBlockInside(c.xCoord, c.yCoord, c.zCoord);
	}

	public boolean isBlockInside(int x, int y, int z) {
		boolean ix = ReikaMathLibrary.isValueInsideBoundsIncl(minX, maxX, x);
		boolean iy = ReikaMathLibrary.isValueInsideBoundsIncl(minY, maxY, y);
		boolean iz = ReikaMathLibrary.isValueInsideBoundsIncl(minZ, maxZ, z);
		return ix && iy && iz;
	}

	public boolean isBlockInsideExclusive(Coordinate c) {
		return this.isBlockInsideExclusive(c.xCoord, c.yCoord, c.zCoord);
	}

	public boolean isBlockInsideExclusive(int x, int y, int z) {
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

	public Coordinate findBlock(World world, BlockCheck bc) {
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = minY; y <= maxY; y++) {
					//ReikaJavaLibrary.pConsole(new Coordinate(x, y, z));
					if (bc.matchInWorld(world, x, y, z))
						return new Coordinate(x, y, z);
				}
			}
		}
		return null;
	}

	public int getLongestEdge() {
		return ReikaMathLibrary.multiMax(this.getSizeX(), this.getSizeY(), this.getSizeZ());
	}

	public int getCenterX() {
		return (maxX-minX)/2+minX;
	}

	public int getCenterY() {
		return (maxY-minY)/2+minY;
	}

	public int getCenterZ() {
		return (maxZ-minZ)/2+minZ;
	}

	public Coordinate getFarthestPointFrom(int x, int y, int z) {
		if (this.isBlockInside(x, y, z)) {
			int dxn = x-minX;
			int dxp = maxX-x;
			int dyn = y-minY;
			int dyp = maxY-y;
			int dzn = z-minZ;
			int dzp = maxZ-z;
			boolean negX = Math.abs(dxn) > Math.abs(dxp);
			boolean negY = Math.abs(dyn) > Math.abs(dyp);
			boolean negZ = Math.abs(dzn) > Math.abs(dzp);
			int rx = negX ? x-dxn : x+dxp;
			int ry = negY ? y-dyn : y+dyp;
			int rz = negZ ? z-dzn : z+dzp;
			return new Coordinate(rx, ry, rz);
		}
		else {
			int rx = x < minX ? maxX : minX;
			int ry = y < minY ? maxY : minY;
			int rz = z < minZ ? maxZ : minZ;
			return new Coordinate(rx, ry, rz);
		}
	}

	public static BlockBox between(Entity e1, Entity e2) {
		return new BlockBox(MathHelper.floor_double(e1.posX), MathHelper.floor_double(e1.posY), MathHelper.floor_double(e1.posZ), MathHelper.floor_double(e2.posX), MathHelper.floor_double(e2.posY), MathHelper.floor_double(e2.posZ));
	}

	public static BlockBox between(DecimalPosition e1, DecimalPosition e2) {
		return new BlockBox(MathHelper.floor_double(e1.xCoord), MathHelper.floor_double(e1.yCoord), MathHelper.floor_double(e1.zCoord), MathHelper.floor_double(e2.xCoord), MathHelper.floor_double(e2.yCoord), MathHelper.floor_double(e2.zCoord));
	}

	public static BlockBox between(Coordinate c1, Coordinate c2) {
		return new BlockBox(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
	}

	public static BlockBox between(WorldLocation c1, WorldLocation c2) {
		return new BlockBox(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
	}

	@ClassDependent("buildcraft.api.core.IAreaProvider")
	public static BlockBox getFromIAP(IAreaProvider iap) {
		return new BlockBox(iap.xMin(), iap.yMin(), iap.zMin(), iap.xMax()+1, iap.yMax()+1, iap.zMax()+1);
	}

}
