package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.PlayerUsageSnooper;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnvironmentPackager {

	public static final EnvironmentPackager instance = new EnvironmentPackager();

	private EnvironmentPackager() {

	}

	private File getFile() {
		return new File(new File(DragonAPICore.getMinecraftDirectory(), "DragonAPI"), "gameenv.export");
	}

	private File getSettingsFile() {
		return new File(DragonAPICore.getMinecraftDirectory(), "options.txt");
	}

	public File export() {
		try {
			File f = this.getFile();
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();

			ArrayList<String> data = new ArrayList();
			data.addAll(ReikaFileReader.getFileAsLines(this.getSettingsFile(), true));
			Collections.sort(data);
			data.add(this.getDivider());
			for (ModContainer mc : Loader.instance().getActiveModList()) {
				data.add(mc.getModId()+" # "+mc.getVersion());
			}
			data.add(this.getDivider());
			data.addAll(DragonAPIInit.config.getSettingsAsLines());
			for (ControlledConfig cfg : ControlledConfig.getConfigs()) {
				if (cfg.configMod == DragonAPIInit.instance)
					continue;
				data.add(this.getDivider());
				data.addAll(cfg.getSettingsAsLines());
			}
			data.add(this.getDivider());
			/*
			data.add("OS: "+System.getProperty("os.name")+" - "+System.getProperty("os.version"));
			data.add("CPUs: "+Runtime.getRuntime().availableProcessors());
			data.add("CPU Model: "+System.getProperty("os.arch"));
			data.add("Java Version: "+System.getProperty("java.version"));
			 */
			PlayerUsageSnooper snooper = Minecraft.getMinecraft().getPlayerUsageSnooper();
			Map<String, String> map = snooper.getCurrentStats();
			for (Entry<String, String> e : map.entrySet()) {
				data.add(e.getKey()+" = "+e.getValue());
			}
			data.add(this.getDivider());
			data.add("Checksum:");
			data.add(this.computeHash(data));

			ReikaFileReader.writeLinesToFile(f, data, true);

			return f;
		}
		catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}

	private String getDivider() {
		return ReikaStringParser.getNOf("=", 20);
	}

	private String computeHash(ArrayList<String> data) {
		return data.toString();
	}

	private static class DataSection {

		private final String title;
		private final ArrayList<String> data = new ArrayList();

		private DataSection(String s) {
			title = s;
		}

	}

}
