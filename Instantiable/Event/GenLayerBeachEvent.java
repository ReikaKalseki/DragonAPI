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
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class GenLayerBeachEvent extends Event {

	public final int originalBiomeID;
	public final boolean wasOriginallyAllowed;

	public GenLayerBeachEvent(int id, boolean a) {
		originalBiomeID = id;
		wasOriginallyAllowed = a;
	}

	public static boolean fire(int biomeID) {
		boolean flag = biomeID != BiomeGenBase.extremeHills.biomeID && biomeID != BiomeGenBase.extremeHillsPlus.biomeID && biomeID != BiomeGenBase.extremeHillsEdge.biomeID;
		GenLayerBeachEvent evt = new GenLayerBeachEvent(biomeID, flag);
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

	public static class BeachTypeEvent extends Event {

		public final int sourceBiomeID;
		public final int originalBiomeID = ReikaASMHelper.forgeVersion_Build == 1614 ? BiomeGenBase.beach.biomeID : BiomeGenBase.stoneBeach.biomeID;
		public int biomeID = originalBiomeID;

		public BeachTypeEvent(int id) {
			sourceBiomeID = id;
		}

		public void deleteBeach() {
			biomeID = sourceBiomeID;
		}

		public static int fire(int biomeID) {
			if (ReikaASMHelper.forgeVersion_Build > 1558 && !GenLayerBeachEvent.fire(biomeID))
				return biomeID;
			BeachTypeEvent evt = new BeachTypeEvent(biomeID);
			MinecraftForge.EVENT_BUS.post(evt);
			return evt.biomeID;
		}

	}

}
