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

import java.lang.ref.WeakReference;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class WorldLocation {

	private static final Random rand = new Random();

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	public final int dimensionID;

	private boolean isRemote = false;

	private WeakReference<World> clientWorld;

	public WorldLocation(World world, int x, int y, int z) {
		this(world.provider.dimensionId, x, y, z);

		if (world.isRemote) {
			isRemote = true;
			clientWorld = new WeakReference(world);
		}
	}

	public WorldLocation(int dim, int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
		dimensionID = dim;
	}

	private WorldLocation(WorldLocation loc) {
		this(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public WorldLocation(TileEntity te) {
		this(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public WorldLocation(Entity e) {
		this(e.worldObj, MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ));
	}

	public WorldLocation(World world, Coordinate loc) {
		this(world, loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public WorldLocation(World world, MovingObjectPosition hit) {
		this(world, hit.blockX, hit.blockY, hit.blockZ);
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
		return isRemote && clientWorld.get() != null ? clientWorld.get() : DimensionManager.getWorld(dimensionID);
	}

	public void writeToNBT(String tag, NBTTagCompound NBT) {
		NBTTagCompound data = new NBTTagCompound();
		this.writeToNBT(data);
		NBT.setTag(tag, data);
	}

	public void writeToNBT(NBTTagCompound data) {
		data.setInteger("dim", dimensionID);
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
	}

	public static final WorldLocation readFromNBT(NBTTagCompound data) {
		int x = data.getInteger("x");
		int y = data.getInteger("y");
		int z = data.getInteger("z");
		int dim = data.getInteger("dim");
		return new WorldLocation(dim, x, y, z);
	}

	public static final WorldLocation readFromNBT(String tag, NBTTagCompound NBT) {
		if (!NBT.hasKey(tag))
			return null;
		NBTTagCompound data = NBT.getCompoundTag(tag);
		if (data != null) {
			return readFromNBT(data);
		}
		return null;
	}

	public NBTTagCompound writeToTag() {
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("dim", dimensionID);
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
		return data;
	}

	public static final WorldLocation readTag(NBTTagCompound data) {
		int x = data.getInteger("x");
		int y = data.getInteger("y");
		int z = data.getInteger("z");
		int dim = data.getInteger("dim");
		return new WorldLocation(dim, x, y, z);
	}

	public WorldLocation copy() {
		return new WorldLocation(this);
	}

	@Override
	public String toString() {
		return xCoord+", "+yCoord+", "+zCoord+" in DIM"+dimensionID;
	}

	@Override
	public int hashCode() {
		return xCoord + (zCoord << 8) + (yCoord << 16) + (dimensionID << 24);
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

	public double getDistanceTo(Entity e) {
		return this.getDistanceTo(e.posX, e.posY, e.posZ);
	}

	public double getSquareDistanceTo(double x, double y, double z) {
		return Math.abs(x-xCoord)+Math.abs(y-yCoord)+Math.abs(z-zCoord);
	}

	public boolean isWithinSquare(WorldLocation c, int d) {
		return this.isWithinSquare(c, d, d, d);
	}

	public boolean isWithinSquare(WorldLocation c, int dx, int dy, int dz) {
		return c.dimensionID == dimensionID && Math.abs(c.xCoord-xCoord) <= dx && Math.abs(c.yCoord-yCoord) <= dy && Math.abs(c.zCoord-zCoord) <= dz;
	}

	public DoubleWorldLocation decimalOffset(double dx, double dy, double dz) {
		return new DoubleWorldLocation(this, dx, dy, dz);
	}

	public static final class DoubleWorldLocation extends WorldLocation {

		public final double offsetX;
		public final double offsetY;
		public final double offsetZ;

		private DoubleWorldLocation(WorldLocation loc, double dx, double dy, double dz) {
			super(loc);
			offsetX = dx;
			offsetY = dy;
			offsetZ = dz;
		}
	}

	public String toSerialString() {
		return String.format("%d:%d:%d:%d", dimensionID, xCoord, yCoord, zCoord);
	}

	public static WorldLocation fromSerialString(String s) {
		String[] parts = s.split(":");
		return new WorldLocation(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
	}

	public ChunkCoordIntPair getChunk() {
		return new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
	}

	public BlockKey getBlockKey() {
		return new BlockKey(this.getBlock(), this.getBlockMetadata());
	}

	public boolean isWithinDistOnAllCoords(WorldLocation loc, int radius) {
		return Math.abs(loc.xCoord-xCoord) <= radius && Math.abs(loc.yCoord-yCoord) <= radius && Math.abs(loc.zCoord-zCoord) <= radius;
	}

}
