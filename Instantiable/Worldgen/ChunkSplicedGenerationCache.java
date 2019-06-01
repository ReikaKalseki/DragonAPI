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
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class ChunkSplicedGenerationCache {

	private final HashMap<ChunkCoordIntPair, HashMap<Coordinate, BlockPlace>> data = new HashMap();
	private final HashSet<ChunkCoordIntPair> generated = new HashSet();

	private boolean isWrapping = false;
	private int wrapDistanceX;
	private int wrapDistanceZ;

	public ChunkSplicedGenerationCache() {

	}

	public ChunkSplicedGenerationCache setWrapping(int distX, int distZ) {
		isWrapping = true;
		wrapDistanceX = distX >> 4;
		wrapDistanceZ = distZ >> 4;
		return this;
	}

	public void setBlock(int x, int y, int z, Block b) {
		this.place(x, y, z, new SetBlock(b));
	}

	public void setBlock(int x, int y, int z, Block b, int meta) {
		this.place(x, y, z, new SetBlock(b, meta));
	}

	public void setBlock(int x, int y, int z, BlockKey bk) {
		this.setBlock(x, y, z, bk.blockID, bk.metadata >= 0 ? bk.metadata : 0);
	}

	public void setTileEntity(int x, int y, int z, Block b, int meta, TileCallback call) {
		this.place(x, y, z, new TileSet(call, b, meta));
	}

	public void setAir(int x, int y, int z) {
		this.setBlock(x, y, z, Blocks.air);
	}

	public void place(int x, int y, int z, BlockPlace sb) {
		ChunkCoordIntPair key = this.getKey(x, z);
		if (isWrapping) {
			key = this.wrap(key);
		}
		HashMap<Coordinate, BlockPlace> map = data.get(key);
		if (map == null) {
			map = new HashMap();
			data.put(key, map);
		}
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		map.put(new Coordinate(x, y, z), sb);
	}

	private ChunkCoordIntPair wrap(ChunkCoordIntPair key) {
		return new ChunkCoordIntPair((key.chunkXPos+wrapDistanceX)%wrapDistanceX, (key.chunkZPos+wrapDistanceZ)%wrapDistanceZ);
	}

	public static int modAndAlign(int c) {
		c = c%16;
		if (c < 0) {
			c += 16;
			if (c%16 == 0)
				c += 16;
		}
		return c;
	}

	public BlockKey getBlock(int x, int y, int z) {
		ChunkCoordIntPair key = this.getKey(x, z);
		HashMap<Coordinate, BlockPlace> map = data.get(key);
		if (map == null)
			return null;
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		BlockPlace p = map.get(new Coordinate(x, y, z));
		return p != null ? p.asBlockKey() : null;
	}

	public boolean hasBlock(int x, int y, int z) {
		ChunkCoordIntPair key = this.getKey(x, z);
		HashMap<Coordinate, BlockPlace> map = data.get(key);
		if (map == null)
			return false;
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		return map.containsKey(new Coordinate(x, y, z));
	}

	public HashSet<Coordinate> getLocationsOf(BlockKey key) {
		HashSet<Coordinate> set = new HashSet();
		for (ChunkCoordIntPair p : data.keySet()) {
			HashMap<Coordinate, BlockPlace> map = data.get(p);
			for (Coordinate c : map.keySet()) {
				BlockPlace bp = map.get(c);
				if (bp.asBlockKey().equals(key)) {
					set.add(c.offset(p.chunkXPos*16, 0, p.chunkZPos*16));
				}
			}
		}
		return set;
	}

	public void generate(World world, int chunkX, int chunkZ) {
		this.generate(world, new ChunkCoordIntPair(chunkX, chunkZ));
	}

	public void generate(World world, ChunkCoordIntPair cp) {
		this.doGenerate(world, cp);
		data.remove(cp);
	}

	public void generateAll(World world) {
		for (ChunkCoordIntPair cp : data.keySet()) {
			this.doGenerate(world, cp);
		}
		data.clear();
	}

	private void doGenerate(World world, ChunkCoordIntPair cp) {
		HashMap<Coordinate, BlockPlace> map = data.get(cp);
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
		generated.add(cp);
	}

	public boolean isChunkGenerated(int x, int z) {
		return generated.contains(new ChunkCoordIntPair(x >> 4, z >> 4));
	}

	public void clear() {
		data.clear();
	}

	public void duplicate(ChunkSplicedGenerationCache c) {
		this.clear();
		data.putAll(c.data);
	}

	public void addDataFromColumnData(int chunkX, int chunkZ, Block[] data) {
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				int x = chunkX*16+dx;
				int z = chunkZ*16+dz;
				int d = (dx*16+dz);
				int posIndex = d*data.length/256;
				for (int y = 0; y < 256; y++) {
					Block b = data[y+posIndex];
					this.setBlock(x, y, z, b);
				}
			}
		}
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static ChunkCoordIntPair getKey(int x, int z) {
		return new ChunkCoordIntPair(x >> 4, z >> 4);
	}

	public static interface BlockPlace {

		public void place(World world, int x, int y, int z);
		public BlockKey asBlockKey();

	}

	static class SetBlock implements BlockPlace {

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

	}

	public static interface TileCallback {

		public void onTilePlaced(World world, int x, int y, int z, TileEntity te);

	}

	public static final class RelayCache extends ChunkSplicedGenerationCache {

		private final World world;

		public RelayCache(World world) {
			this.world = world;
		}

		@Override
		public void place(int x, int y, int z, BlockPlace sb) {
			sb.place(world, x, y, z);
		}

	}

}
