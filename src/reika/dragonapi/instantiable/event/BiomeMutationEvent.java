/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class BiomeMutationEvent extends Event {

	public final GenLayer generator;

	public final int posX;
	public final int posZ;

	public final int originalBiomeID;
	public final int originalMutatedID;

	public BiomeMutationEvent(GenLayer gen, int x, int z, int id) {
		generator = gen;
		posX = x;
		posZ = z;
		originalBiomeID = id;
		originalMutatedID = id+128;
	}

	public static boolean fireTry(GenLayer gen, int chunkX, int chunkZ, int x, int z, int originalID) {
		BiomeMutationEvent evt = new BiomeMutationEvent(gen, (chunkX << 4)+x, (chunkZ << 4)+z, originalID);
		return !MinecraftForge.EVENT_BUS.post(evt) && BiomeGenBase.biomeList[evt.originalMutatedID] != null && BiomeGenBase.biomeList[evt.originalMutatedID] instanceof BiomeGenMutated;
	}

	public static class GetMutatedBiomeEvent extends BiomeMutationEvent {
		public int biomeID;

		public GetMutatedBiomeEvent(GenLayer gen, int x, int z, int id) {
			super(gen, x, z, id);
			biomeID = originalMutatedID;
		}

		public static int fireGet(GenLayer gen, int chunkX, int chunkZ, int x, int z, int originalID) {
			GetMutatedBiomeEvent evt = new GetMutatedBiomeEvent(gen, (chunkX << 4)+x, (chunkZ << 4)+z, originalID);
			MinecraftForge.EVENT_BUS.post(evt);
			return evt.biomeID;
		}

	}

}
