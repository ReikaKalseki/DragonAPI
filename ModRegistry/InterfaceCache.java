/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
//package Reika.DragonAPI.Auxiliary;
package Reika.DragonAPI.ModRegistry;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
/*
public class InterfaceCache {

	public static final InterfaceCache instance = new InterfaceCache();

	private final HashMap<String, Boolean> data = new HashMap();
	private final HashMap<String, Class> classes = new HashMap();

	private final HashMap<String, String> shortcuts = new HashMap();

	private InterfaceCache() {
		this.addShortcut("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider");
		this.addShortcut("micdoodle8.mods.galacticraft.api.world.ISolarLevel");
		this.addShortcut("ic2.api.item.IElectricItem");
		this.addShortcut("icbm.api.IMissile");
		this.addShortcut("net.machinemuse.api.electricity.MuseElectricItem");
		this.addShortcut("universalelectricity.api.item.IEnergyItem");
		this.addShortcut("cofh.api.energy.IEnergyContainerItem");
	}

	private void addShortcut(String s) {
		try {
			Class c = Class.forName(s);
			String[] sp = s.split("\\.");
			shortcuts.put(sp[sp.length-1], s);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean classExists(String s) {
		Boolean b = data.get(s);
		if (b == null) {
			try {
				Class c = Class.forName(s);
				b = c != null;//.isInterface();
				classes.put(s, c);
				if (shortcuts.containsKey(s))
					classes.put(shortcuts.get(s), c);
			}
			catch (ClassNotFoundException e) {
				b = false;
			}
			data.put(s, b);
			if (shortcuts.containsKey(s))
				data.put(shortcuts.get(s), b);
		}
		return b.booleanValue();
	}

	public boolean instanceOf(String s, Object o) {
		return this.classExists(s) && classes.get(s).isAssignableFrom(o.getClass());
	}
 */
//private static class Interface {
public enum InterfaceCache {

	IGALACTICWORLD("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider"),
	ISOLARLEVEL("micdoodle8.mods.galacticraft.api.world.ISolarLevel"),
	IELECTRICITEM("ic2.api.item.IElectricItem"),
	IMISSILE("icbm.api.IMissile"),
	MUSEELECTRICITEM("net.machinemuse.api.electricity.MuseElectricItem"),
	RFENERGYITEM("cofh.api.energy.IEnergyContainerItem"),
	UEENERGYITEM("universalelectricity.api.item.IEnergyItem");

	//public final ModList parent;
	private final String classpath;
	public final String name;
	private final Class object;

	private InterfaceCache(/*ModList mod, */String s) {
		//parent = mod;
		classpath = s;
		String[] sp = s.split("\\.");
		name = sp[sp.length-1];
		object = ReikaJavaLibrary.getClassNoException(s);
	}

	public boolean exists() {
		return object != null;
	}

	public boolean instanceOf(Object o) {
		return object != null && object.isAssignableFrom(o.getClass());
	}
}
//}
