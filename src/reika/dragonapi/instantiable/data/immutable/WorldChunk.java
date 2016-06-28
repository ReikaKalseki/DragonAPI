/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.immutable;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public final class WorldChunk {

	public final int dimensionID;
	public final ChunkCoordIntPair chunk;

	public WorldChunk(World world, Chunk ch) {
		this(world, ch.getChunkCoordIntPair());
	}

	public WorldChunk(World world, ChunkCoordIntPair ch) {
		this(world.provider.dimensionId, ch);
	}

	public WorldChunk(int dim, int x, int z) {
		this(dim, new ChunkCoordIntPair(x, z));
	}

	public WorldChunk(int dim, ChunkCoordIntPair ch) {
		dimensionID = dim;
		chunk = ch;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof WorldChunk) {
			WorldChunk c = (WorldChunk)o;
			return c.dimensionID == dimensionID && c.chunk.equals(chunk);
		}
		return false;
	}

	public Chunk load() {
		return DimensionManager.getWorld(dimensionID).getChunkFromChunkCoords(chunk.chunkXPos, chunk.chunkZPos);
	}

	@Override
	public int hashCode() {
		return chunk.hashCode()^dimensionID;
	}

	@Override
	public String toString() {
		return "Chunk "+chunk.toString()+" in DIM"+dimensionID;
	}

}
