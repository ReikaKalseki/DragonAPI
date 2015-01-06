/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ControlledConfig {

	protected Configuration config;

	private int readID;
	protected File configFile;

	protected final DragonAPIMod configMod;

	private ConfigList[] optionList;
	private IDRegistry[] IDList;

	protected Object[] controls;
	protected int[] otherIDs;

	public ControlledConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id, int cfg) {
		configMod = mod;
		optionList = option;
		IDList = id;

		if (option != null)
			controls = new Object[optionList.length];
		else {
			controls = new Object[0];
			optionList = new ConfigList[0];
		}

		if (id != null)
			otherIDs = new int[IDList.length];
		else {
			otherIDs = new int[0];
			IDList = new IDRegistry[0];
		}
	}

	private final String getConfigPath() {
		return configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length()-4);
	}

	public final File getConfigFolder() {
		return configFile.getParentFile();
	}

	public Object getControl(int i) {
		return controls[i];
	}

	public int getOtherID(int i) {
		return otherIDs[i];
	}

	private boolean checkReset(Configuration config) {
		//readID = config.get("Control", "Config ID - Edit to have your config auto-deleted", CURRENT_CONFIG_ID).getInt();
		//return readID != CURRENT_CONFIG_ID;
		return false;
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
		this.onInit();
		this.loadConfig();
	}

	protected void onInit() {}

	private void loadConfig() {
		config.load();

		for (int i = 0; i < optionList.length; i++) {
			String label = optionList[i].getLabel();
			if (optionList[i].shouldLoad()) {
				if (optionList[i].isBoolean())
					controls[i] = this.setState(optionList[i]);
				if (optionList[i].isNumeric())
					controls[i] = this.setValue(optionList[i]);
				if (optionList[i].isDecimal())
					controls[i] = this.setFloat(optionList[i]);
			}
			else {
				if (optionList[i].isBoolean())
					controls[i] = optionList[i].getDefaultState();
				if (optionList[i].isNumeric())
					controls[i] = optionList[i].getDefaultValue();
				if (optionList[i].isDecimal())
					controls[i] = optionList[i].getDefaultFloat();
			}
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
