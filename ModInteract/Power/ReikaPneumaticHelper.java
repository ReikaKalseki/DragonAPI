/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Power;

import java.lang.reflect.Field;

import Reika.DragonAPI.ModList;

public class ReikaPneumaticHelper {

	private static int airPerRF;

	public static int getAirPerRF() {
		return 1;//airPerRF;
	}

	public static int getWattsPerAir() {
		double wattPerRF = ReikaBuildCraftHelper.getWattsPerMJ()/10;
		return (int)(wattPerRF/airPerRF);
	}

	static {
		if (ModList.PNEUMATICRAFT.isLoaded()) {
			try {
				Class c = Class.forName("pneumaticCraft.common.Config");
				Field f = c.getField("fluxCompressorEfficiency");
				airPerRF = 20*f.getInt(null)/100;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
