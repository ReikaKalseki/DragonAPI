package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.PlayerUsageSnooper;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
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

			ArrayList<DataSection> data = new ArrayList();
			DataSection sec = new DataSection(DataSections.OPTIONS);
			sec.addAll(ReikaFileReader.getFileAsLines(this.getSettingsFile(), true));
			data.add(sec);
			sec = new DataSection(DataSections.MODS);
			for (ModContainer mc : Loader.instance().getActiveModList()) {
				sec.add(mc.getModId()+" # "+mc.getVersion());
			}
			data.add(sec);
			for (ControlledConfig cfg : ControlledConfig.getConfigs()) {
				sec = new ConfigDataSection(cfg);
				data.add(sec);
				sec.addAll(cfg.getSettingsAsLines());
			}
			sec = new DataSection(DataSections.JVM);
			/*
			data.add("OS: "+System.getProperty("os.name")+" - "+System.getProperty("os.version"));
			data.add("CPUs: "+Runtime.getRuntime().availableProcessors());
			data.add("CPU Model: "+System.getProperty("os.arch"));
			data.add("Java Version: "+System.getProperty("java.version"));
			 */
			PlayerUsageSnooper snooper = Minecraft.getMinecraft().getPlayerUsageSnooper();
			Map<String, String> map = snooper.getCurrentStats();
			for (Entry<String, String> e : map.entrySet()) {
				sec.add(e.getKey()+" = "+e.getValue());
			}
			data.add(sec);
			Collections.sort(data);

			ArrayList<String> li = new ArrayList();

			for (DataSection s : data) {
				li.add(this.getDivider());
				li.add(s.title());
				li.add(this.getDivider());
				li.addAll(s.data);
				li.add("");
				li.add("");
			}

			li.add("Checksum:");
			li.add(this.computeHash(li));

			ReikaFileReader.writeLinesToFile(f, li, true);

			return f;
		}
		catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}

	private String getDivider() {
		return ReikaStringParser.getNOf("-", 20);
	}

	private String computeHash(ArrayList<String> li) {
		return li.toString();
	}

	private static class ConfigDataSection extends DataSection {

		private final DragonAPIMod modname;

		protected ConfigDataSection(ControlledConfig cfg) {
			super(DataSections.CONFIGS);
			modname = cfg.configMod;
		}

		@Override
		public String title() {
			return super.title()+modname.getDisplayName();
		}

		@Override
		public int compareTo(DataSection o) {
			if (o instanceof ConfigDataSection) { //same type
				return this.compareMods(modname, ((ConfigDataSection)o).modname);
			}
			else {
				return super.compareTo(o);
			}
		}

		private int compareMods(DragonAPIMod mod1, DragonAPIMod mod2) {
			String id1 = mod1.getModContainer().getModId().toLowerCase(Locale.ENGLISH);
			String id2 = mod2.getModContainer().getModId().toLowerCase(Locale.ENGLISH);
			if (id1.equals(id2))
				return 0;
			else if (id1.equals("dragonapi"))
				return -1;
			else if (id2.equals("dragonapi"))
				return 1;
			else
				return id1.compareToIgnoreCase(id2);
		}

	}

	private static class DataSection implements Comparable<DataSection> {

		private final DataSections type;
		private final ArrayList<String> data = new ArrayList();

		protected DataSection(DataSections s) {
			type = s;
		}

		protected final void add(String s) {
			data.add(s);
			Collections.sort(data);
		}

		protected final void addAll(Collection<String> c) {
			data.addAll(c);
			Collections.sort(data);
		}

		@Override
		public int compareTo(DataSection o) {
			return type.compareTo(o.type);
		}

		public String title() {
			return type.title;
		}

	}

	private static enum DataSections {
		OPTIONS("Game Settings"),
		MODS("Mods"),
		CONFIGS("Config - "),
		JVM("JVM/OS Parameters");

		public final String title;

		private DataSections(String s) {
			title = s;
		}
	}

}
