/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ControlledConfig {

	protected Configuration config;

	/** Change this to cause auto-deletion of users' config files to load new copies */
	private int CURRENT_CONFIG_ID = 0;
	private int readID;
	protected File configFile;

	protected final DragonAPIMod configMod;

	private ConfigList[] optionList;
	private IDRegistry[] blockList;
	private IDRegistry[] itemList;
	private IDRegistry[] IDList;

	protected Object[] controls;
	protected int[] blockIDs;
	protected int[] itemIDs;
	protected int[] otherIDs;

	public ControlledConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] blocks, IDRegistry[] items, IDRegistry[] id, int cfg) {
		configMod = mod;
		optionList = option;
		blockList = blocks;
		itemList = items;
		IDList = id;

		if (option != null)
			controls = new Object[optionList.length];
		else {
			controls = new Object[0];
			optionList = new ConfigList[0];
		}

		if (blocks != null)
			blockIDs = new int[blockList.length];
		else {
			blockIDs = new int[0];
			blockList = new IDRegistry[0];
		}

		if (items != null)
			itemIDs = new int[itemList.length];
		else {
			itemIDs = new int[0];
			itemList = new IDRegistry[0];
		}

		if (id != null)
			otherIDs = new int[IDList.length];
		else {
			otherIDs = new int[0];
			IDList = new IDRegistry[0];
		}

		CURRENT_CONFIG_ID = cfg;
	}

	public String getConfigPath() {
		return configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length()-4);
	}

	public Object getControl(int i) {
		return controls[i];
	}

	public int getBlockID(int i) {
		return blockIDs[i];
	}

	public int getItemID(int i) {
		return itemIDs[i];
	}

	public int getOtherID(int i) {
		return otherIDs[i];
	}

	private boolean checkReset(Configuration config) {
		readID = config.get("Control", "Config ID - Edit to have your config auto-deleted", CURRENT_CONFIG_ID).getInt();
		return readID != CURRENT_CONFIG_ID;
	}

	protected final void resetConfigFile() {
		String path = this.getConfigPath()+"_Old_Config_Backup.txt";
		File backup = new File(path);
		if (backup.exists())
			backup.delete();
		try {
			ReikaJavaLibrary.pConsole(configMod.getDisplayName().toUpperCase()+": Writing Backup File to "+path);
			ReikaJavaLibrary.pConsole(configMod.getDisplayName().toUpperCase()+": Use this to restore custom IDs if necessary.");
			backup.createNewFile();
			if (!backup.exists())
				ReikaJavaLibrary.pConsole(configMod.getDisplayName().toUpperCase()+": Could not create backup file at "+path+"!");
			else {
				PrintWriter p = new PrintWriter(backup);
				p.println("#####----------THESE ARE ALL THE OLD CONFIG SETTINGS YOU WERE USING----------#####");
				p.println("#####---IF THEY DIFFER FROM THE DEFAULTS, YOU MUST RE-EDIT THE CONFIG FILE---#####");

				p.close();
			}
		}
		catch (IOException e) {
			ReikaJavaLibrary.pConsole(configMod.getDisplayName().toUpperCase()+": Could not create backup file due to IOException!");
			e.printStackTrace();
		}
		configFile.delete();
	}

	private void versionCheck(FMLPreInitializationEvent event) {
		if (this.checkReset(config)) {
			ReikaJavaLibrary.pConsole(configMod.getDisplayName().toUpperCase()+": Config File Format Changed. Resetting...");
			this.resetConfigFile();
			this.initProps(event);
			return;
		}
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

	public final void initProps(FMLPreInitializationEvent event) {
		if (configFile == null)
			throw new MisuseException("Error loading "+configMod.getDisplayName()+": You must load a config file before reading it!");
		config = new Configuration(configFile);

		this.versionCheck(event);
		this.loadConfig();
	}

	private void loadConfig() {
		config.load();

		for (int i = 0; i < optionList.length; i++) {
			String label = optionList[i].getLabel();
			if (optionList[i].isBoolean())
				controls[i] = this.setState(optionList[i]);
			if (optionList[i].isNumeric())
				controls[i] = this.setValue(optionList[i]);
			if (optionList[i].isDecimal())
				controls[i] = this.setFloat(optionList[i]);
		}

		for (int i = 0; i < blockList.length; i++) {
			String name = blockList[i].getConfigName();
			blockIDs[i] = config.get(blockList[i].getCategory(), name, blockList[i].getDefaultID()).getInt();
		}

		for (int i = 0; i < itemList.length; i++) {
			String name = itemList[i].getConfigName();
			itemIDs[i] = config.get(itemList[i].getCategory(), name, itemList[i].getDefaultID()).getInt();
		}

		for (int i = 0; i < IDList.length; i++) {
			otherIDs[i] = this.getValueFromConfig(IDList[i], config);
		}

		this.loadAdditionalData();

		/*******************************/
		//save the data
		config.save();
	}

	private boolean setState(ConfigList cfg) {
		Property prop = config.get("Control Setup", cfg.getLabel(), cfg.getDefaultState());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultState());
		return prop.getBoolean(cfg.getDefaultState());
	}

	private int setValue(ConfigList cfg) {
		Property prop = config.get("Control Setup", cfg.getLabel(), cfg.getDefaultValue());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultValue());
		return prop.getInt();
	}

	private float setFloat(ConfigList cfg) {
		Property prop = config.get("Control Setup", cfg.getLabel(), cfg.getDefaultFloat());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultFloat());
		return (float)prop.getDouble(cfg.getDefaultFloat());
	}

	private int getValueFromConfig(IDRegistry id, Configuration config) {
		if (id.isBlock())
			return config.getBlock(id.getCategory(), id.getConfigName(), id.getDefaultID()).getInt();
		if (id.isItem())
			return config.getItem(id.getCategory(), id.getConfigName(), id.getDefaultID()).getInt();
		return config.get(id.getCategory(), id.getConfigName(), id.getDefaultID()).getInt();
	}

	protected void loadAdditionalData() {}
	/*
	public void reloadCategoryFromDefaults(String category) {
		config.load();
		ConfigCategory cat = config.getCategory(category);
		cat.clear();
		config.save();
		this.loadConfig();
	}*/

}
