/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.DragonAPI.Exception.UnreachableCodeException;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public abstract class ChunkSplicedGenerator {

	protected final Map<ChunkCoordIntPair, Map<Coordinate, BlockPlace>> data;
	protected final boolean isConcurrent;

	public ChunkSplicedGenerator(boolean concurrent) {
		isConcurrent = concurrent;
		data = isConcurrent ? new ConcurrentHashMap() : new HashMap();
	}

	public final void setBlock(int x, int y, int z, Block b) {
		this.place(x, y, z, new SetBlock(b));
	}

	public final void setBlock(int x, int y, int z, Block b, int meta) {
		this.place(x, y, z, new SetBlock(b, meta));
	}

	public final void setBlock(int x, int y, int z, BlockKey bk) {
		this.setBlock(x, y, z, bk.blockID, bk.metadata >= 0 ? bk.metadata : 0);
	}

	public void setAir(int x, int y, int z) {
		this.setBlock(x, y, z, Blocks.air);
	}

	public final void setTileEntity(int x, int y, int z, Block b, int meta, TileCallback call) {
		this.place(x, y, z, new TileSet(call, b, meta));
	}

	protected abstract void place(int x, int y, int z, BlockPlace sb);

	protected final void put(ChunkCoordIntPair key, Coordinate c, BlockPlace sb) {
		Map<Coordinate, BlockPlace> map = data.get(key);
		if (map == null) {
			map = isConcurrent ? new ConcurrentHashMap() : new HashMap();
			data.put(key, map);
		}
		map.put(c, sb);
	}

	public final void generate(World world, int chunkX, int chunkZ) {
		this.generate(world, new ChunkCoordIntPair(chunkX, chunkZ));
	}

	public final void generate(World world, ChunkCoordIntPair cp) {
		this.doGenerate(world, cp);
		data.remove(cp);
	}

	public final void generateAll(World world) {
		for (ChunkCoordIntPair cp : data.keySet()) {
			this.doGenerate(world, cp);
		}
		data.clear();
	}

	private void doGenerate(World world, ChunkCoordIntPair cp) {
		Map<Coordinate, BlockPlace> map = data.get(cp);
		if (map != null) {
			//ReikaJavaLibrary.pConsole("To generate: "+map);
			for (Coordinate c : map.keySet()) {
				BlockPlace bp = map.get(c);
				int x = (cp.chunkXPos << 4)+c.xCoord;
				int y = c.yCoord;
				int z = (cp.chunkZPos << 4)+c.zCoord;
				bp.place(world, x, y, z);
			}
		}
	}

	public final void clear() {
		data.clear();
	}

	public final void duplicate(ChunkSplicedGenerator c) {
		this.clear();
		data.putAll(c.data);
	}

	@Override
	public final String toString() {
		return data.toString();
	}

	public static ChunkCoordIntPair getKey(int x, int z) {
		return new ChunkCoordIntPair(x >> 4, z >> 4);
	}

	public static abstract class BlockPlace {

		public abstract void place(World world, int x, int y, int z);
		public abstract void writeToNBT(NBTTagCompound tag);
		public abstract BlockKey asBlockKey();

		public static BlockPlace readFromTag(NBTTagCompound tag) {
			String cl = tag.getString("type");
			switch(cl) {
				case "Block":
					return new SetBlock((Block)Block.blockRegistry.getObject(tag.getString("id")), tag.getInteger("meta"));
				case "Tile":
					return TileSet.construct(tag);
			}
			throw new UnreachableCodeException("Unrecognized BlockPlace type: "+cl);
		}

		public final NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			this.writeToNBT(tag);
			tag.setString("type", this.getTypeID());
			return tag;
		}

		protected abstract String getTypeID();

	}

	static class SetBlock extends BlockPlace {

		private final Block block;
		private final int metadata;

		SetBlock(Block b) {
			this(b, 0);
		}

		SetBlock(Block b, int m) {
			block = b;
			metadata = m;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			world.setBlock(x, y, z, block, metadata, 3);
			if (block.getLightValue(world, x, y, z) > 0) {
				world.markBlockForUpdate(x, y, z);
				world.func_147479_m(x, y, z);
			}
		}

		@Override
		public final BlockKey asBlockKey() {
			return new BlockKey(block, metadata);
		}

		@Override
		public final String toString() {
			return "SET "+this.asBlockKey().toString();
		}

		@Override
		public void writeToNBT(NBTTagCompound ret) {
			ret.setString("id", Block.blockRegistry.getNameForObject(block));
			ret.setInteger("meta", metadata);
		}

		@Override
		protected String getTypeID() {
			return "Block";
		}

	}

	static class TileSet extends SetBlock {

		private final TileCallback callback;

		TileSet(TileCallback c, Block b, int m) {
			super(b, m);
			callback = c;
		}

		@Override
		public void place(World world, int x, int y, int z) {
			super.place(world, x, y, z);
			callback.onTilePlaced(world, x, y, z, world.getTileEntity(x, y, z));
		}

		@Override
		public void writeToNBT(NBTTagCompound ret) {
			super.writeToNBT(ret);
			NBTTagCompound tag = new NBTTagCompound();
			callback.writeToNBT(tag);
			ret.setTag("tile", tag);
			ret.setString("tileType", callback.getClass().getName());
		}

		private static TileSet construct(NBTTagCompound data) {
			try {
				String cn = data.getString("tileType");
				Block b = (Block)Block.blockRegistry.getObject(data.getString("id"));
				int meta = data.getInteger("meta");
				TileCallback call = (TileCallback)Class.forName(cn).newInstance();
				call.readFromNBT(data.getCompoundTag("tile"));
				return new TileSet(call, b, meta);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected String getTypeID() {
			return "Tile";
		}

	}

	public static interface TileCallback {

		public void onTilePlaced(World world, int x, int y, int z, TileEntity te);

		public void readFromNBT(NBTTagCompound tag);
		public void writeToNBT(NBTTagCompound tag);

	}

}
