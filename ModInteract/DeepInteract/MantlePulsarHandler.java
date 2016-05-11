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

import java.lang.reflect.Method;
import java.util.HashMap;

import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;


public class MantlePulsarHandler {

	private static boolean isLoaded;

	private static Method checkLoaded;

	private static final HashMap<Object, HashMap<String, Boolean>> cache = new HashMap();

	public static boolean isPulseLoaded(Object pulsar, String id) { //Cache for performance
		if (!isLoaded)
			return false;
		HashMap<String, Boolean> map = cache.get(pulsar);
		if (map == null) {
			map = new HashMap();
			cache.put(pulsar, map);
		}
		Boolean flag = map.get(id);
		if (flag == null) {
			flag = readLoaded(pulsar, id);
			map.put(id, flag);
		}
		return flag;
	}

	private static boolean readLoaded(Object pulsar, String id) {
		try {
			return (Boolean)checkLoaded.invoke(pulsar, id);
		}
		catch (Exception e) {
			return false;
		}
	}

	static {
		ModEntry mod = new BasicModEntry("Mantle");
		if (mod.isLoaded()) {
			try {
				Class c = Class.forName("mantle.pulsar.control.PulseManager");
				checkLoaded = c.getMethod("isPulseLoaded", String.class);
				isLoaded = true;
			}
			catch (Exception e) {
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				isLoaded = false;
			}
		}
	}

}
