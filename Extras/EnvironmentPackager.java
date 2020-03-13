package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.IO.ReikaFileReader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnvironmentPackager {

	public static final EnvironmentPackager instance = new EnvironmentPackager();

	private EnvironmentPackager() {

	}

	private File getFile() {
		return new File(new File(DragonAPICore.getMinecraftDirectory(), "DragonAPI"), "settings.cache");
	}

	private File getSettingsFile() {
		return new File(DragonAPICore.getMinecraftDirectory(), "options.text");
	}

	public void export() {
		try {
			File f = this.getFile();
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();

			ArrayList<String> data = new ArrayList();
			data.addAll(ReikaFileReader.getFileAsLines(this.getSettingsFile(), true));
			Collections.sort(data);
			data.add("");
			data.add("Checksum:");
			data.add(this.computeHash(data));

			ReikaFileReader.writeLinesToFile(f, data, true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String computeHash(ArrayList<String> data) {

	}

}
