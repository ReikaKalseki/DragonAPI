/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

public final class WorldLocation {

	private static final Random rand = new Random();

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	public final int dimensionID;

	public WorldLocation(World world, int x, int y, int z) {
		this(world.provider.dimensionId, x, y, z);
	}

	private WorldLocation(int dim, int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
		dimensionID = dim;
	}

	public WorldLocation(TileEntity te) {
		this(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public Block getBlock() {
		World world = this.getWorld();
		return world != null ? world.getBlock(xCoord, yCoord, zCoord) : null;
	}

	public boolean isEmpty() {
		return this.getBlock() == Blocks.air;
	}

	public int getBlockMetadata() {
		World world = this.getWorld();
		return world != null ? world.getBlockMetadata(xCoord, yCoord, zCoord) : -1;
	}

	public TileEntity getTileEntity() {
		World world = this.getWorld();
		return world != null ? world.getTileEntity(xCoord, yCoord, zCoord) : null;
	}

	public int getRedstone() {
		World world = this.getWorld();
		return world != null ? world.getBlockPowerInput(xCoord, yCoord, zCoord) : 0;
	}

	public int getRedstoneOnSide(ForgeDirection dir) {
		ForgeDirection opp = dir.getOpposite();
		int s = dir.ordinal();
		World world = this.getWorld();
		return world != null ? world.getIndirectPowerLevelTo(xCoord+opp.offsetX, yCoord+opp.offsetY, zCoord+opp.offsetZ, s) : 0;
	}

	public boolean isRedstonePowered() {
		World world = this.getWorld();
		return world != null ? world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) : false;
	}

	public void triggerBlockUpdate(boolean adjacent) {
		World world = this.getWorld();
		if (world != null) {
			world.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (adjacent) {
				ReikaWorldHelper.causeAdjacentUpdates(world, xCoord, yCoord, zCoord);
			}
		}
	}

	public void dropItem(ItemStack is) {
		this.dropItem(is, 1);
	}

	public void dropItem(ItemStack is, double vscale) {
		World world = this.getWorld();
		if (world != null && !world.isRemote) {
			ReikaItemHelper.dropItem(this.getWorld(), xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), is, vscale);
		}
	}

	public void setBlock(Block b) {
		this.setBlock(b, 0);
	}

	public void setBlock(ItemStack is) {
		this.setBlock(Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public void setBlock(Block id, int meta) {
		World world = this.getWorld();
		if (world != null) {
			world.setBlock(xCoord, yCoord, zCoord, id, meta, 3);
		}
	}

	public WorldLocation move(int dx, int dy, int dz) {
		return new WorldLocation(dimensionID, xCoord+dx, yCoord+dy, zCoord+dz);
	}

	public WorldLocation move(ForgeDirection dir, int dist) {
		return this.move(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public WorldLocation changeWorld(World world) {
		return new WorldLocation(world, xCoord, yCoord, zCoord);
	}

	public World getWorld() {
		return DimensionManager.getWorld(dimensionID);
	}

	public void writeToNBT(String tag, NBTTagCompound NBT) {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("dim", dimensionID);
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
		NBT.setTag(tag, data);
	}

	public static WorldLocation readFromNBT(String tag, NBTTagCompound NBT) {
		if (!NBT.hasKey(tag))
			return null;
		NBTTagCompound data = NBT.getCompoundTag(tag);
		if (data != null) {
			int x = data.getInteger("x");
			int y = data.getInteger("y");
			int z = data.getInteger("z");
			int dim = data.getInteger("dim");
			return new WorldLocation(dim, x, y, z);
		}
		return null;
	}

	public WorldLocation copy() {
		return new WorldLocation(dimensionID, xCoord, yCoord, zCoord);
	}

	@Override
	public String toString() {
		return xCoord+", "+yCoord+", "+zCoord+" in DIM"+dimensionID;
	}

	@Override
	public int hashCode() {
		return xCoord + zCoord << 8 + yCoord << 16 + dimensionID << 24;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WorldLocation) {
			WorldLocation w = (WorldLocation)o;
			return this.equals(w.dimensionID, w.xCoord, w.yCoord, w.zCoord);
		}
		return false;
	}

	private boolean equals(int dim, int x, int y, int z) {
		return dim == dimensionID && x == xCoord && y == yCoord && z == zCoord;
	}

	public boolean equals(World world, int x, int y, int z) {
		return this.equals(world.provider.dimensionId, x, y, z);
	}

	public double getDistanceTo(WorldLocation src) {
		return this.getDistanceTo(src.xCoord, src.yCoord, src.zCoord);
	}

	public double getDistanceTo(double x, double y, double z) {
		return ReikaMathLibrary.py3d(x-xCoord, y-yCoord, z-zCoord);
	}

}
