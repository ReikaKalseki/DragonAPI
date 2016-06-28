/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.deepinteract;

import java.lang.reflect.Method;

import net.minecraft.world.World;
import reika.dragonapi.auxiliary.trackers.ReflectiveFailureTracker;
import reika.dragonapi.instantiable.BasicModEntry;
import cpw.mods.fml.common.Loader;


public class ModSeasonHandler { //if other mods have seasons, add to here

	private static boolean isLoaded;

	private static Method getSeasonTemp;
	private static Method getSeasonRain;

	public static float getSeasonHumidityModifier(World world) {
		if (!isLoaded)
			return 0;
		try {
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
