package Reika.DragonAPI.Interfaces.Entity;

import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;

public interface ChunkLoadingEntity {

	Collection<ChunkCoordIntPair> getChunksToLoad();

	/** Override setDead and call this in it, to unload your chunks. */
	void onDestroy();

}
