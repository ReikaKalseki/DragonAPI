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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public final class Coordinate {

	private static final Random rand = new Random();

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public Coordinate(int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public Coordinate(TileEntity te) {
		this(te.xCoord, te.yCoord, te.zCoord);
	}

	public Coordinate(Entity e) {
		this(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ));
	}

	public Coordinate(WorldLocation loc) {
		this(loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public Coordinate offset(int dx, int dy, int dz) {
		return new Coordinate(xCoord+dx, yCoord+dy, zCoord+dz);
	}

	public Coordinate offset(ForgeDirection dir, int dist) {
		return this.offset(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public void writeToNBT(String tag, NBTTagCompound NBT) {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
		NBT.setTag(tag, data);
	}

	public static final Coordinate readFromNBT(String tag, NBTTagCompound NBT) {
		if (!NBT.hasKey(tag))
			return null;
		NBTTagCompound data = NBT.getCompoundTag(tag);
		if (data != null) {
			int x = data.getInteger("x");
			int y = data.getInteger("y");
			int z = data.getInteger("z");
			int dim = data.getInteger("dim");
		}
		return null;
	}

	public NBTTagCompound writeToTag() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
		return data;
	}

	public static final Coordinate readTag(NBTTagCompound data) {
		int x = data.getInteger("x");
		int y = data.getInteger("y");
		int z = data.getInteger("z");
		return new Coordinate(x, y, z);
	}

	public Coordinate copy() {
		return new Coordinate(xCoord, yCoord, zCoord);
	}

	@Override
	public String toString() {
		return xCoord+", "+yCoord+", "+zCoord;
	}

	@Override
	public int hashCode() {
		return xCoord + zCoord << 8 + yCoord << 16;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Coordinate) {
			Coordinate w = (Coordinate)o;
			return this.equals(w.xCoord, w.yCoord, w.zCoord);
		}
		return false;
	}

	private boolean equals(int x, int y, int z) {
		return x == xCoord && y == yCoord && z == zCoord;
	}

	public double getDistanceTo(Coordinate src) {
		return this.getDistanceTo(src.xCoord, src.yCoord, src.zCoord);
	}

	public double getDistanceTo(double x, double y, double z) {
		return ReikaMathLibrary.py3d(x-xCoord, y-yCoord, z-zCoord);
	}

	public int[] toArray() {
		int[] a = new int[3];
		a[0] = xCoord;
		a[1] = yCoord;
		a[2] = zCoord;
		return a;
	}

	public Block getBlock(World world) {
		return world != null ? world.getBlock(xCoord, yCoord, zCoord) : null;
	}

	public boolean isEmpty(World world) {
		return this.getBlock(world) == Blocks.air;
	}

	public int getBlockMetadata(World world) {
		return world != null ? world.getBlockMetadata(xCoord, yCoord, zCoord) : -1;
	}

	public TileEntity getTileEntity(World world) {
		return world != null ? world.getTileEntity(xCoord, yCoord, zCoord) : null;
	}

	public int getRedstone(World world) {
		return world != null ? world.getBlockPowerInput(xCoord, yCoord, zCoord) : 0;
	}

	public void triggerBlockUpdate(World world, boolean adjacent) {
		if (world != null) {
			world.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (adjacent) {
				ReikaWorldHelper.causeAdjacentUpdates(world, xCoord, yCoord, zCoord);
			}
		}
	}

	public void dropItem(World world, ItemStack is) {
		this.dropItem(world, is, 1);
	}

	public void dropItem(World world, ItemStack is, double vscale) {
		if (world != null && !world.isRemote) {
			ReikaItemHelper.dropItem(world, xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), is, vscale);
		}
	}

	public void setBlock(World world, Block b) {
		this.setBlock(world, b, 0);
	}

	public void setBlock(World world, ItemStack is) {
		this.setBlock(world, Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public void setBlock(World world, Block id, int meta) {
		if (world != null) {
			world.setBlock(xCoord, yCoord, zCoord, id, meta, 3);
		}
	}

}
