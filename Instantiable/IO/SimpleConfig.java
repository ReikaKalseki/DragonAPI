/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public final class SimpleConfig {

	private Configuration config;
	private File configFile;

	private final DragonAPIMod configMod;

	private final HashMap<String, Object> data = new HashMap();

	private boolean isReading = false;

	public SimpleConfig(DragonAPIMod mod) {
		configMod = mod;
	}

	public void loadCustomConfigFile(FMLPreInitializationEvent event, String file) {
		configFile = new File(file);
	}

	public void loadSubfolderedConfigFile(FMLPreInitializationEvent event) {
		String name = ReikaStringParser.stripSpaces(configMod.getDisplayName());
		String author = ReikaStringParser.stripSpaces(configMod.getModAuthorName());
		String file = event.getModConfigurationDirectory()+"/"+author+"/"+name+".cfg";
		configFile = new File(file);
	}

	public void loadDefaultConfigFile(FMLPreInitializationEvent event) {
		configFile = event.getSuggestedConfigurationFile();
	}

	public final void loadDataFromFile(FMLPreInitializationEvent event) {
		if (isReading)
			throw new MisuseException("Already reading!");
		if (configFile == null)
			throw new MisuseException("Error loading "+configMod.getDisplayName()+": You must load a config file before reading it!");
		config = new Configuration(configFile);

		config.load();
		isReading = true;
	}

	public final void finishReading() {
		if (!isReading)
			throw new MisuseException("You cannot stop reading before you start!");
		config.save();
		isReading = false;
	}

	public int getInteger(String category, String key, int defaultValue) {
		boolean flag = !isReading;
		if (data.containsKey(key))
			return (Integer)data.get(key);
		else {
			if (flag)
				this.loadIfNecessary();
			int param = config.get(category, key, defaultValue).getInt();
			data.put(key, param);
			if (flag)
				this.saveIfNecessary();
			return param;
		}
	}

	public boolean getBoolean(String category, String key, boolean defaultValue) {
		boolean flag = !isReading;
		if (data.containsKey(key))
			return (Boolean)data.get(key);
		else {
			if (flag)
				this.loadIfNecessary();
			boolean param = config.get(category, key, defaultValue).getBoolean(defaultValue);
			data.put(key, param);
			if (flag)
				this.saveIfNecessary();
			return param;
		}
	}

	public ArrayList<Integer> getIntList(String category, String key, int... defaults) {
		boolean flag = !isReading;
		if (data.containsKey(key))
			return (ArrayList<Integer>)data.get(key);
		else {
			if (flag)
				this.loadIfNecessary();
			ArrayList<Integer> param = this.getList(config.get(category, key, defaults));
			data.put(key, param);
			if (flag)
				this.saveIfNecessary();
			return param;
		}
	}

	private ArrayList<Integer> getList(Property p) {
		ArrayList li = new ArrayList();
		int[] data = p.getIntList();
		for (int i = 0; i < data.length; i++) {
			li.add(data[i]);
		}
		return li;
	}

	private void loadIfNecessary() {
		if (!isReading) {
			config.load();
			isReading = true;
		}
	}

	private void saveIfNecessary() {
		if (isReading) {
			isReading = false;
			config.save();
		}
	}
}
