package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

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

	/** Returns true if any settings were changed. */
	public boolean checkAndUpdateSettingsCache() {
		File f = this.getFile();
		SettingCache current = new SettingCache(f);
		current.load();
		SettingCache repl = new SettingCache(f, Minecraft.getMinecraft().gameSettings);
		if (!current.equals(repl)) {
			repl.save();
			return true;
		}
		return false;
	}

	public boolean hasSettingsCache() {
		File f = this.getFile();
		return f.exists();
	}

	private static class SettingCache {

		private final File file;

		private final ArrayList<String> data = new ArrayList();

		public SettingCache(File f) {
			file = f;
		}

		public SettingCache(File f, GameSettings s) {
			this(f);

			this.loadSettings(s);
		}

		private void load() {
			data.clear();
			data.addAll(ReikaFileReader.getFileAsLines(instance.getFile(), true));
			Collections.sort(data);
		}

		private void save() {
			try {
				File f = instance.getFile();
				f.delete();
				f.getParentFile().mkdirs();
				f.createNewFile();
				ReikaFileReader.writeLinesToFile(f, data, true);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void loadSettings(GameSettings s) {
			data.clear();
			data.addAll(ReikaFileReader.getFileAsLines(instance.getSettingsFile(), true));
			Collections.sort(data);
		}

		private String computeHash() {
			return data.toString();
		}

	}

}
