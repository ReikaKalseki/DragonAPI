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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent.UpdateFlags;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Interfaces.Location;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public final class Coordinate implements Location, Comparable<Coordinate> {

	public static class DistanceComparator implements Comparator<Coordinate> {

		public final Coordinate target;
		public final boolean taxicab;

		public DistanceComparator(Coordinate tg, boolean taxi) {
			target = tg;
			taxicab = taxi;
		}

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
			return taxicab ? Integer.compare(o1.getTaxicabDistanceTo(target), o2.getTaxicabDistanceTo(target)) : Double.compare(o1.getDistanceTo(target), o2.getDistanceTo(target));
		}

	}

	public static final NBTIO<Coordinate> nbtHandler = new NBTIO<Coordinate>() {

		@Override
		public Coordinate createFromNBT(NBTBase nbt) {
			return Coordinate.readTag((NBTTagCompound)nbt);
		}

		@Override
		public NBTBase convertToNBT(Coordinate obj) {
			return obj.writeToTag();
		}

	};

	private static final Random rand = new Random();

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public Coordinate(int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public Coordinate(Vec3 vec) {
		this(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public Coordinate(DecimalPosition vec) {
		this(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public Coordinate(double x, double y, double z) {
		this(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
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

	public Coordinate(MovingObjectPosition hit) {
		this(hit.blockX, hit.blockY, hit.blockZ);
	}

	public Coordinate(ChunkCoordinates cc) {
		this(cc.posX, cc.posY, cc.posZ);
	}

	public Coordinate(ChunkPosition pos) {
		this(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
	}

	@SideOnly(Side.CLIENT)
	public Coordinate(WorldRenderer cc) {
		this(cc.posX, cc.posY, cc.posZ);
	}

	public Coordinate offset(int dx, int dy, int dz) {
		return new Coordinate(xCoord+dx, yCoord+dy, zCoord+dz);
	}

	public Coordinate offset(ForgeDirection dir, int dist) {
		return this.offset(dir.offsetX*dist, dir.offsetY*dist, dir.offsetZ*dist);
	}

	public Coordinate offset(Coordinate c) {
		return this.offset(c.xCoord, c.yCoord, c.zCoord);
	}

	public Coordinate setX(int x) {
		return new Coordinate(x, yCoord, zCoord);
	}

	public Coordinate setY(int y) {
		return new Coordinate(xCoord, y, zCoord);
	}

	public Coordinate setZ(int z) {
		return new Coordinate(xCoord, yCoord, z);
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
			return new Coordinate(x, y, z);
		}
		return null;
	}

	public void writeToTag(NBTTagCompound data) {
		data.setInteger("x", xCoord);
		data.setInteger("y", yCoord);
		data.setInteger("z", zCoord);
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

	@Override
	public String toString() {
		return "["+xCoord+", "+yCoord+", "+zCoord+"]";
	}

	@Override
	public int hashCode() {
		return coordHash(xCoord, yCoord, zCoord);
	}

	public static int coordHash(int x, int y, int z) {
		//return xCoord + (zCoord << 8) + (yCoord << 16);
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Coordinate) {
			Coordinate w = (Coordinate)o;
			return this.equals(w.xCoord, w.yCoord, w.zCoord);
		}
		return false;
	}

	public boolean equals(int x, int y, int z) {
		return x == xCoord && y == yCoord && z == zCoord;
	}

	public double getDistanceTo(Coordinate src) {
		return this.getDistanceTo(src.xCoord, src.yCoord, src.zCoord);
	}

	public double getDistanceTo(double x, double y, double z) {
		return ReikaMathLibrary.py3d(x-xCoord, y-yCoord, z-zCoord);
	}

	public double getDistanceTo(Entity e) {
		return this.getDistanceTo(e.posX, e.posY, e.posZ);
	}

	public boolean isWithinSquare(Coordinate c, int d) {
		return this.isWithinSquare(c, d, d, d);
	}

	public boolean isWithinSquare(Coordinate c, int dx, int dy, int dz) {
		return Math.abs(c.xCoord-xCoord) <= dx && Math.abs(c.yCoord-yCoord) <= dy && Math.abs(c.zCoord-zCoord) <= dz;
	}

	public int getTaxicabDistanceTo(Coordinate c) {
		return Math.abs(c.xCoord-xCoord)+Math.abs(c.yCoord-yCoord)+Math.abs(c.zCoord-zCoord);
	}

	public int getTaxicabDistanceTo(double x, double y, double z) {
		return Math.abs(MathHelper.floor_double(x)-xCoord)+Math.abs(MathHelper.floor_double(y)-yCoord)+Math.abs(MathHelper.floor_double(z)-zCoord);
	}

	public int[] toArray() {
		int[] a = new int[3];
		a[0] = xCoord;
		a[1] = yCoord;
		a[2] = zCoord;
		return a;
	}

	public Block getBlock(IBlockAccess world) {
		return world != null ? world.getBlock(xCoord, yCoord, zCoord) : null;
	}

	public boolean isEmpty(IBlockAccess world) {
		return this.getBlock(world).isAir(world, xCoord, yCoord, zCoord);
	}

	public boolean softBlock(IBlockAccess world) {
		return ReikaWorldHelper.softBlocks(world, xCoord, yCoord, zCoord);
	}

	public int getBlockMetadata(IBlockAccess world) {
		return world != null ? world.getBlockMetadata(xCoord, yCoord, zCoord) : -1;
	}

	public BlockKey getBlockKey(IBlockAccess world) {
		return world != null ? new BlockKey(world.getBlock(xCoord, yCoord, zCoord), world.getBlockMetadata(xCoord, yCoord, zCoord)) : null;
	}

	public TileEntity getTileEntity(IBlockAccess world) {
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

	public void triggerRenderUpdate(World world) {
		if (world != null) {
			world.markBlockRangeForRenderUpdate(xCoord-1, yCoord-1, zCoord-1, xCoord+1, yCoord+1, zCoord+1);
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

	public boolean setBlock(World world, Block b) {
		return this.setBlock(world, b, 0);
	}

	public boolean setBlock(World world, ItemStack is) {
		return this.setBlock(world, Block.getBlockFromItem(is.getItem()), is.getItemDamage());
	}

	public boolean setBlock(World world, Block id, int meta) {
		return this.setBlock(world, id, meta, 3);
	}

	public boolean setBlockMetadata(World world, int meta) {
		return world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 3);
	}

	public boolean setBlock(World world, Block id, int meta, int flags) {
		return world != null && world.setBlock(xCoord, yCoord, zCoord, id, meta, flags);
	}

	public BiomeGenBase getBiome(World world) {
		return world.getBiomeGenForCoords(xCoord, zCoord);
	}

	public void updateTick(World world) {
		this.updateTick(world, world.rand);
	}

	public void updateTick(World world, Random r) {
		Block b = this.getBlock(world);
		b.updateTick(world, xCoord, yCoord, zCoord, r);
		BlockTickEvent.fire(world, xCoord, yCoord, zCoord, b, UpdateFlags.FORCED.flag);
	}

	public void scheduleUpdateTick(World world, int delay) {
		world.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlock(world), delay);
	}

	public ChunkCoordIntPair asChunkPair() {
		return new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
	}

	public List<Integer> asIntList() {
		return Arrays.asList(xCoord, yCoord, zCoord);
	}

	public Coordinate negate() {
		return new Coordinate(-xCoord, -yCoord, -zCoord);
	}

	public Coordinate rotate90About(int ox, int oz, boolean left) {
		/*
		int dx = xCoord-ox;
		int dz = zCoord-oz;
		int x2 = xCoord;
		int z2 = zCoord;
		if (left) { //pdx > ndz, pdz > pdx, ndz > ndx, ndx > pdz
			x2 += dz;
			z2 += -dx;
		}
		else { //pdx > pdz, pdz > ndx, ndz > pdx, ndx > ndz
			x2 += -dz;
			z2 += dx;
		}
		 */
		Vec3 vec = Vec3.createVectorHelper(xCoord-ox, yCoord, zCoord-oz);
		vec = ReikaVectorHelper.rotateVector(vec, 0, left ? -90 : 90, 0);
		vec.xCoord += ox;
		vec.zCoord += oz;
		return new Coordinate(vec.xCoord, yCoord, vec.zCoord);//new Coordinate(x2, yCoord, z2);
	}

	public Coordinate rotate180About(int ox, int oz) {
		int dx = xCoord-ox;
		int dz = zCoord-oz;
		int x2 = ox-dx;
		int z2 = oz-dz;
		return new Coordinate(x2, yCoord, z2);
	}

	public void writeToBuf(ByteBuf data) {
		data.writeInt(xCoord);
		data.writeInt(yCoord);
		data.writeInt(zCoord);
	}

	public static Coordinate readFromBuf(ByteBuf data) {
		int x = data.readInt();
		int y = data.readInt();
		int z = data.readInt();
		return new Coordinate(x, y, z);
	}

	public Coordinate getDifference(Coordinate c) {
		return this.offset(c.negate());
	}

	public void destroyBlockPartially(World world, double i) {
		world.destroyBlockInWorldPartially(Block.getIdFromBlock(this.getBlock(world)), xCoord, yCoord, zCoord, (int)i);
	}

	public ChunkCoordinates asChunkCoordinates() {
		return new ChunkCoordinates(xCoord, yCoord, zCoord);
	}

	public Collection<Coordinate> getAdjacentCoordinates() {
		ArrayList<Coordinate> li = new ArrayList();
		for (int i = 0; i < 6; i++) {
			li.add(this.offset(ForgeDirection.VALID_DIRECTIONS[i], 1));
		}
		return li;
	}

	public Coordinate to2D() {
		return new Coordinate(xCoord, 0, zCoord);
	}

	@Override
	public int compareTo(Coordinate o) {
		int val = Integer.compare(this.hashCode(), o.hashCode());
		if (val != 0)
			return val;
		val = Integer.compare(xCoord, o.xCoord);
		if (val != 0)
			return val;
		val = Integer.compare(yCoord, o.yCoord);
		if (val != 0)
			return val;
		val = Integer.compare(zCoord, o.zCoord);
		return val;
	}

	public void setBlock(ChunkSplicedGenerationCache world, Block b) {
		world.setBlock(xCoord, yCoord, zCoord, b);
	}

	public void setBlock(ChunkSplicedGenerationCache world, Block b, int meta) {
		world.setBlock(xCoord, yCoord, zCoord, b, meta);
	}

	public boolean isChunkLoaded(World world) {
		return world.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}

	public boolean isWithinDistOnAllCoords(Coordinate loc, int radius) {
		return Math.abs(loc.xCoord-xCoord) <= radius && Math.abs(loc.yCoord-yCoord) <= radius && Math.abs(loc.zCoord-zCoord) <= radius;
	}

	public boolean isWithinDistOnAllCoords(int x, int y, int z, int radius) {
		return Math.abs(x-xCoord) <= radius && Math.abs(y-yCoord) <= radius && Math.abs(z-zCoord) <= radius;
	}

	public String toSerialString() {
		return String.format("%d:%d:%d", xCoord, yCoord, zCoord);
	}

	public static Coordinate fromSerialString(String s) {
		String[] parts = s.split(":");
		return new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}

	public MovingObjectPosition asMovingPosition(int s, Vec3 vec) {
		return new MovingObjectPosition(xCoord, yCoord, zCoord, s, vec);
	}

}
