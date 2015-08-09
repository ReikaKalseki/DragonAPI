/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class DecimalPosition {

	private static final Random rand = new Random();

	public final double xCoord;
	public final double yCoord;
	public final double zCoord;

	public DecimalPosition(double x, double y, double z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public DecimalPosition(TileEntity te) {
		this(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
	}

	public DecimalPosition(Entity e) {
		this(e.posX, e.posY, e.posZ);
	}

	public DecimalPosition(DecimalPosition loc) {
		this(loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public DecimalPosition(MovingObjectPosition hit) {
		this(hit.blockX+0.5, hit.blockY+0.5, hit.blockZ+0.5);
	}

	public DecimalPosition offset(int dx, int dy, int dz) {
		return new DecimalPosition(xCoord+dx, yCoord+dy, zCoord+dz);
	}

	public DecimalPosition offset(ForgeDirection dir, int dist) {
		return this.offset(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public boolean sharesBlock(DecimalPosition dec) {
		return this.sharesBlock(dec.xCoord, dec.yCoord, dec.zCoord);
	}

	public boolean sharesBlock(double x, double y, double z) {
		return this.matchX(x) && this.matchY(y) && this.matchZ(z);
	}

	private boolean matchX(double x) {
		return MathHelper.floor_double(x) == MathHelper.floor_double(xCoord);
	}

	private boolean matchY(double y) {
		return MathHelper.floor_double(y) == MathHelper.floor_double(yCoord);
	}

	private boolean matchZ(double z) {
		return MathHelper.floor_double(z) == MathHelper.floor_double(zCoord);
	}

	public void writeToNBT(String tag, NBTTagCompound NBT) {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x", xCoord);
		data.setDouble("y", yCoord);
		data.setDouble("z", zCoord);
		NBT.setTag(tag, data);
	}

	public static final DecimalPosition readFromNBT(String tag, NBTTagCompound NBT) {
		if (!NBT.hasKey(tag))
			return null;
		NBTTagCompound data = NBT.getCompoundTag(tag);
		if (data != null) {
			double x = data.getDouble("x");
			double y = data.getDouble("y");
			double z = data.getDouble("z");
			return new DecimalPosition(x, y, z);
		}
		return null;
	}

	public NBTTagCompound writeToTag() {
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x", xCoord);
		data.setDouble("y", yCoord);
		data.setDouble("z", zCoord);
		return data;
	}

	public static final DecimalPosition readTag(NBTTagCompound data) {
		double x = data.getDouble("x");
		double y = data.getDouble("y");
		double z = data.getDouble("z");
		return new DecimalPosition(x, y, z);
	}

	public DecimalPosition copy() {
		return new DecimalPosition(xCoord, yCoord, zCoord);
	}

	@Override
	public String toString() {
		return "["+xCoord+", "+yCoord+", "+zCoord+"]";
	}

	@Override
	public int hashCode() {
		return (int)(xCoord + (zCoord * 256) + (yCoord * 65536));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DecimalPosition) {
			DecimalPosition w = (DecimalPosition)o;
			return this.equals(w.xCoord, w.yCoord, w.zCoord);
		}
		return false;
	}

	private boolean equals(double x, double y, double z) {
		return x == xCoord && y == yCoord && z == zCoord;
	}

	public double getDistanceTo(DecimalPosition src) {
		return this.getDistanceTo(src.xCoord, src.yCoord, src.zCoord);
	}

	public double getDistanceTo(double x, double y, double z) {
		return ReikaMathLibrary.py3d(x-xCoord, y-yCoord, z-zCoord);
	}

	public double[] toArray() {
		double[] a = new double[3];
		a[0] = xCoord;
		a[1] = yCoord;
		a[2] = zCoord;
		return a;
	}

	public Coordinate getCoordinate() {
		return new Coordinate(MathHelper.floor_double(xCoord), MathHelper.floor_double(yCoord), MathHelper.floor_double(zCoord));
	}

	public Block getBlock(World world) {
		return world != null ? this.getCoordinate().getBlock(world) : null;
	}

	public boolean isEmpty(World world) {
		return this.getBlock(world) == Blocks.air;
	}

	public int getBlockMetadata(World world) {
		return world != null ? this.getCoordinate().getBlockMetadata(world) : -1;
	}

	public TileEntity getTileEntity(World world) {
		return world != null ? this.getCoordinate().getTileEntity(world) : null;
	}

	public int getRedstone(World world) {
		return world != null ? this.getCoordinate().getRedstone(world) : 0;
	}

	public void triggerBlockUpdate(World world, boolean adjacent) {
		this.getCoordinate().triggerBlockUpdate(world, adjacent);
	}

	public void dropItem(World world, ItemStack is) {
		this.dropItem(world, is, 1);
	}

	public void dropItem(World world, ItemStack is, double vscale) {
		if (world != null && !world.isRemote) {
			ReikaItemHelper.dropItem(world, xCoord+rand.nextDouble(), yCoord+rand.nextDouble(), zCoord+rand.nextDouble(), is, vscale);
		}
	}

	public boolean setBlock(World world, Block b) {
		return this.setBlock(world, b, 0);
	}

	public boolean setBlock(World world, ItemStack is) {
		return this.setBlock(world, Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public boolean setBlock(World world, Block id, int meta) {
		return world != null && this.getCoordinate().setBlock(world, id, meta);
	}

}
