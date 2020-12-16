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

import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;

import cpw.mods.fml.common.eventhandler.Event;

public class GenLayerBeachEvent extends Event {

	private static int[] cachedInts;
	private static int cachedIndex;
	private static boolean readyToCall = true;

	public final int originalBiomeID;
	public final int plannedBeachID;
	public int beachIDToPlace;

	public GenLayerBeachEvent(int plan) {
		originalBiomeID = cachedInts[cachedIndex];
		plannedBeachID = plan;
		beachIDToPlace = plan;
	}

	public void deleteBeach() {
		beachIDToPlace = originalBiomeID;
	}

	public static synchronized void setIntCache(int[] arr) {
		while (!readyToCall) {
			try {
				DragonAPICore.log("Caught concurrent shore genlayer calls, pausing to allow unstacking");
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		readyToCall = false;
		cachedInts = arr;
	}

	public static synchronized void setIntIndex(int idx) {
		readyToCall = false;
		cachedIndex = idx;
	}

	public static synchronized int fire(int place) {
		GenLayerBeachEvent evt = new GenLayerBeachEvent(place);
		MinecraftForge.EVENT_BUS.post(evt);
		readyToCall = true;
		return evt.beachIDToPlace;
	}

}
