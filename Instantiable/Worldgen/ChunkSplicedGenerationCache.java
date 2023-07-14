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

import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class ChunkSplicedGenerationCache extends ChunkSplicedGenerator {

	private final HashSet<ChunkCoordIntPair> generated = new HashSet();

	private boolean isWrapping = false;
	private int wrapDistanceX;
	private int wrapDistanceZ;

	public ChunkSplicedGenerationCache() {
		super(false);
	}

	public ChunkSplicedGenerationCache setWrapping(int distX, int distZ) {
		isWrapping = true;
		wrapDistanceX = distX >> 4;
		wrapDistanceZ = distZ >> 4;
		return this;
	}

	@Override
	public void place(int x, int y, int z, BlockPlace sb) {
		ChunkCoordIntPair key = this.getKey(x, z);
		if (isWrapping) {
			key = this.wrap(key);
		}
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		Coordinate c = new Coordinate(x, y, z);
		this.put(key, c, sb);
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
		Map<Coordinate, BlockPlace> map = data.get(key);
		if (map == null)
			return null;
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		BlockPlace p = map.get(new Coordinate(x, y, z));
		return p != null ? p.asBlockKey() : null;
	}

	public boolean hasBlock(int x, int y, int z) {
		ChunkCoordIntPair key = this.getKey(x, z);
		Map<Coordinate, BlockPlace> map = data.get(key);
		if (map == null)
			return false;
		x = this.modAndAlign(x);
		z = this.modAndAlign(z);
		return map.containsKey(new Coordinate(x, y, z));
	}

	public HashSet<Coordinate> getLocationsOf(BlockKey key) {
		HashSet<Coordinate> set = new HashSet();
		for (ChunkCoordIntPair p : data.keySet()) {
			Map<Coordinate, BlockPlace> map = data.get(p);
			for (Coordinate c : map.keySet()) {
				BlockPlace bp = map.get(c);
				if (bp.asBlockKey().equals(key)) {
					set.add(c.offset(p.chunkXPos*16, 0, p.chunkZPos*16));
				}
			}
		}
		return set;
	}

	public boolean isChunkGenerated(int x, int z) {
		return generated.contains(new ChunkCoordIntPair(x >> 4, z >> 4));
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
	/*
	public void readFromNBT(NBTTagCompound NBT) {
		this.clear();
		NBTTagList li = NBT.getTagList("chunks", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound tag = (NBTTagCompound)o;
			ChunkCoordIntPair cp = new ChunkCoordIntPair(tag.getInteger("chunkX"), tag.getInteger("chunkZ"));
			NBTTagList blocks = tag.getTagList("blocks", NBTTypes.COMPOUND.ID);
			for (Object o2 : li.tagList) {
				NBTTagCompound entry = (NBTTagCompound)o2;
				Coordinate c = Coordinate.readFromNBT("location", entry);
				BlockPlace bp = BlockPlace.readFromNBT(entry.getCompoundTag("block"));
				this.pl
			}
		}
	}*/

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
