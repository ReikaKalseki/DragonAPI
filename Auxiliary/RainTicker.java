/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class RainTicker implements TickHandler {

	private int updateLCG = new Random().nextInt();
	private static final int TICKS_PER_CHUNK = 2; // base vanilla is 3

	public static final RainTicker instance = new RainTicker();

	private RainTicker() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world.isRaining()) {
			for (ChunkCoordIntPair c : ((Set<ChunkCoordIntPair>)world.activeChunkSet)) {
				Chunk chunk = world.getChunkFromChunkCoords(c.chunkXPos, c.chunkZPos);
				int cx = c.chunkXPos << 4;
				int cz = c.chunkZPos << 4;
				ExtendedBlockStorage[] ext = chunk.getBlockStorageArray();

				for (int idx = 0; idx < ext.length; ++idx) {
					ExtendedBlockStorage extb = ext[idx];

					if (extb != null && extb.getNeedsRandomTick()) {
						int cy = extb.getYLocation();
						for (int n = 0; n < TICKS_PER_CHUNK; n++) {
							updateLCG = updateLCG * 3 + 1013904223;
							int pos = updateLCG >> 2;
							int dx = pos & 15;
							int dz = pos >> 8 & 15;
							BiomeGenBase b = world.getBiomeGenForCoords(dx, dz);
							if (b.canSpawnLightningBolt()) {
								int dy = pos >> 16 & 15;
								if (world.canBlockSeeTheSky(dx, dy + 1, dz)) {
									Block block = extb.getBlockByExtId(dx, dy, dz);

									if (block.getTickRandomly()) {
										block.updateTick(world, cx + dx, cy + dy, cz + dz, world.rand);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Rain Tick";
	}

}
