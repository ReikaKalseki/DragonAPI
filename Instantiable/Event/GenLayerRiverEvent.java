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

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class GenLayerRiverEvent extends Event {

	public final int originalBiomeID;
	public final boolean wasOriginallyAllowed;
	public int riverBiomeID = BiomeGenBase.river.biomeID;

	public GenLayerRiverEvent(int id, boolean a) {
		originalBiomeID = id;
		wasOriginallyAllowed = a;
	}

	public static boolean fire_1558(int biomeID) {
		boolean flag = biomeID != BiomeGenBase.ocean.biomeID && biomeID != BiomeGenBase.deepOcean.biomeID;
		GenLayerRiverEvent evt = new GenLayerRiverEvent(biomeID, flag);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case DENY:
				return false;
			case ALLOW:
				return true;
			case DEFAULT:
			default:
				return flag;
		}
	}

	public static void fire_1614(int[] biomeData, int[] riverData, int[] outputData, int idx) {
		int biomeID = biomeData[idx];
		int river = riverData[idx];
		GenLayerRiverEvent evt = new GenLayerRiverEvent(biomeID, true);
		MinecraftForge.EVENT_BUS.post(evt);
		int setID = -1;
		switch(evt.getResult()) {
			case DENY:
				setID = biomeID;
				break;
			case ALLOW:
			case DEFAULT:
				riverData[idx] &= 255;
				setID = evt.riverBiomeID & 255;
			default:
				break;
		}
		outputData[idx] = setID;
	}
	/*
	/** Completely replaces the method's for-loop body. *//*
	public static void fire_1614(int[] biomeData, int[] riverData, int[] outputData, int idx) {
		int biome = biomeData[idx];
		int river = riverData[idx];
		boolean flag = biome != BiomeGenBase.ocean.biomeID && biome != BiomeGenBase.deepOcean.biomeID;
		GenLayerRiverEvent evt = new GenLayerRiverEvent(biome, flag);
		if (biome == BiomeGenBase.icePlains.biomeID) {
			evt.riverBiomeID = BiomeGenBase.frozenRiver.biomeID;
		}
		MinecraftForge.EVENT_BUS.post(evt);

		switch(evt.getResult()) {
			case DENY:
				outputData[idx] = biome;
				return;
			case ALLOW:
				riverData[idx] &= 255;
				outputData[idx] = evt.riverBiomeID & 255;
				return;
			case DEFAULT: //continue to block below
				break;
		}

		if (biome != BiomeGenBase.ocean.biomeID && biome != BiomeGenBase.deepOcean.biomeID) {
			if (river == BiomeGenBase.river.biomeID) {
				if (biome == BiomeGenBase.icePlains.biomeID) {
					outputData[idx] = BiomeGenBase.frozenRiver.biomeID;
				}
				else if (biome != BiomeGenBase.mushroomIsland.biomeID && biome != BiomeGenBase.mushroomIslandShore.biomeID) {
					outputData[idx] = river & 255;
				}
				else {
					outputData[idx] = BiomeGenBase.mushroomIslandShore.biomeID;
				}
			}
			else {
				outputData[idx] = biome;
			}
		}
		else {
			outputData[idx] = biome;
		}
	}*/

}
