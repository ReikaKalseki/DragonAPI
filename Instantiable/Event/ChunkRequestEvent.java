/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.eventhandler.Event;


/** This is fired BEFORE the chunk is actually provided (and generated/loaded from disk if necessary! DO NOT attempt to access
 * the chunk's data! */
public class ChunkRequestEvent extends Event {

	public final WorldServer world;
	private final ChunkProviderServer provider;
	public final int chunkX;
	public final int chunkZ;

	public ChunkRequestEvent(WorldServer world, ChunkProviderServer p, int x, int z) {
		chunkX = x;
		chunkZ = z;
		this.world = world;
		provider = p;
	}

	public boolean chunkIsLoaded() {
		return provider.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ)) != null;
	}

	/** i.e. does NOT need to be generated/decorated. */
	public boolean chunkExistsOnDisk() {
		return ReikaWorldHelper.isChunkGeneratedChunkCoords(world, chunkX, chunkZ);
	}

	public static void fire(WorldServer world, ChunkProviderServer provider, int x, int z) {
		MinecraftForge.EVENT_BUS.post(new ChunkRequestEvent(world, provider, x, z));
	}

}
