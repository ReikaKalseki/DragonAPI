/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import cpw.mods.fml.common.Loader;


public class ModSeasonHandler { //if other mods have seasons, add to here

	private static boolean isLoaded;

	private static Field isActive;
	private static Method getSeasonTemp;
	private static Method getSeasonRain;

	public static float getSeasonHumidityModifier(World world) {
		if (!isLoaded)
			return 0;
		try {
			if (!isActive.getBoolean(null))
				return 0;
			return (Float)getSeasonRain.invoke(null, world.getTotalWorldTime()); //0.2+0.4*sin in his code
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static float getSeasonTemperatureModifier(World world) {
		if (!isLoaded)
			return 0;
		try {
			if (!isActive.getBoolean(null))
				return 0;
			return -25+50*(Float)getSeasonTemp.invoke(null, world.getTotalWorldTime()); //0.6*sin in his code
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	static {
		if (Loader.isModLoaded("HarderWildlife")) {
			try {
				Class c = Class.forName("com.draco18s.wildlife.WildlifeEventHandler");

				getSeasonTemp = c.getDeclaredMethod("getSeasonTemp", long.class);
				getSeasonRain = c.getDeclaredMethod("getSeasonRain", long.class);
				isActive = c.getDeclaredField("doYearCycle");

				isLoaded = true;
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(new BasicModEntry("HarderWildlife"), e);
				isLoaded = false;
			}
		}
	}

	public static boolean isLoaded() {
		return isLoaded;
	}

}
