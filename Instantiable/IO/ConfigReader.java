/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;

public class ConfigReader {

	private String modName;
	private String filepath;
	private static final String ext = ".cfg";

	private Configuration config;
	private File configFile;

	public ConfigReader(String mod) {
		this(mod, mod+ext);
	}

	public ConfigReader(String mod, String file) {
		modName = mod;
		filepath = "?"+file;
		this.setFile();
	}

	private void setFile() {
		configFile = new File(filepath);
		if (!configFile.exists()) {
			//throw new MisuseException("Config for "+modName+" does not exist at "+filepath+"!\nThis reader is only designed for force-reading of existing data!");
			DragonAPICore.logError("Config for "+modName+" does not exist at "+filepath+"!\nThis reader is only designed for force-reading of existing data!");
			return;
		}
		config = new Configuration(configFile);
	}

	public int getConfigInt(String cat, String key) {
		if (!config.hasKey(cat, key))
			throw new MisuseException("Config for "+modName+" at "+filepath+" does not contain category "+cat+" and key "+key+"\nThis reader is only designed for force-reading of existing data!");
		return config.get(cat, key, 0).getInt();
	}

	public boolean getConfigBoolean(String cat, String key) {
		if (!config.hasKey(cat, key))
			throw new MisuseException("Config for "+modName+" at "+filepath+" does not contain category "+cat+" and key "+key+"\nThis reader is only designed for force-reading of existing data!");
		return config.get(cat, key, false).getBoolean(false);
	}

}
