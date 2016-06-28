/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.power;

import java.lang.reflect.Field;

import reika.dragonapi.ModList;

public class ReikaPneumaticHelper {

	private static final int airPerRF = 1;

	public static int getAirPerRF() {
		return airPerRF;
	}

	public static int getWattsPerAir() {
		return ReikaRFHelper.getWattsPerRF()/airPerRF;
	}

	static {
		if (ModList.PNEUMATICRAFT.isLoaded()) {
			try {
				Class c = Class.forName("pneumaticCraft.common.Config");
				Field f = c.getField("fluxCompressorEfficiency");
				//airPerRF = 20*f.getInt(null)/100;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
