/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.util.Arrays;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;

public class RouterHelper {

	private static Class config;
	private static Field blacklist;
	private static String[] cache;

	static {
		try {
			config = Class.forName("router.reborn.cfg");
			blacklist = config.getField("Blacklist");
			cache = (String[])blacklist.get(null);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Router blacklisting!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.ROUTER, e);
		}
	}

	public static boolean blacklistTileEntity(Class c, String label, String name) {
		try {
			String[] in = (String[])blacklist.get(null);

			String[] in2 = new String[in.length+1];
			System.arraycopy(in, 0, in2, 1, in.length);
			in2[0] = name; //Put it at the beginning, because of his broken regex system

			blacklist.set(null, in2);
			cache = new String[in2.length];
			System.arraycopy(in, 0, cache, 0, in.length);
			DragonAPICore.log("DRAGONAPI: Blacklisted TileEntity "+label+" from the router; blacklist now is "+Arrays.toString(cache));
			return true;
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not blacklist TileEntity "+label+" from the router!");
			e.printStackTrace();
			return false;
		}
	}

}
