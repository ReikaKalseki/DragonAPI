package Reika.DragonAPI.Extras;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraftforge.common.ForgeVersion;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.IO.ReikaFileReader.HashType;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnvironmentPackager {

	public static final EnvironmentPackager instance = new EnvironmentPackager();

	private static final String HASH_SALT = "@HASH_SALT@";

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
			DataSection sec = new DataSection(DataSections.INFO);
			sec.add("Game Version: "+Loader.MC_VERSION);
			sec.add("Forge Version: "+ForgeVersion.getVersion());
			sec.add("Java Version: "+ReikaJVMParser.getFullJavaInfo());
			sec.add("Current Time: "+new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(Calendar.getInstance().getTime()));
			sec.add("Is Dev Env: "+ReikaObfuscationHelper.isDeObfEnvironment());
			sec.add("Server Mode: "+(DragonAPICore.isSinglePlayerFromClient() ? "Singleplayer" : "Server"));
			sec.add("JVM Args: "+ReikaJVMParser.getAllArguments());
			sec.add("Launcher: "+ReikaJVMParser.getLauncher());
			sec.add("Root Classloader: "+LaunchClassLoader.class.getClassLoader());
			sec.add("Game Classloader: "+LaunchClassLoader.getSystemClassLoader());
			sec.add("Engine Overhauls: "+CoreModDetection.getStatus());
			data.add(sec);
			sec = new DataSection(DataSections.CRASHREP);

			CrashReport report = CrashReport.makeCrashReport(new Throwable() {
				@Override public String getMessage(){ return "Generating game environment data..."; }
				@Override public void printStackTrace(final PrintWriter s) { }
				@Override public void printStackTrace(final PrintStream s) { }
			}, "Environment Dump");

			String[] split = report.getCompleteReport().split("\\\\n");
			sec.addAll(ReikaJavaLibrary.makeListFromArray(split));
			data.add(sec);
			sec = new DataSection(DataSections.OPTIONS);
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
		/*
		StringBuilder mid = new StringBuilder();
		for (String s : li) {
			mid.append(HashType.SHA256.hash(s));
		}
		return HashType.SHA256.hash(mid.toString());*/

		int maxlen = -1;
		for (String s : li) {
			maxlen = Math.max(maxlen, s.length());
		}
		long[] hashes = new long[maxlen];
		Random rand = new Random(HASH_SALT.hashCode());
		rand.nextBoolean();
		rand.nextBoolean();
		for (int i = 0; i < hashes.length; i++) {
			hashes[i] = rand.nextLong();
		}
		int rotate = 0;
		for (String s : li) {
			for (int i = 0; i < Math.min(s.length(), hashes.length); i++) {
				int c = s.charAt(i) & 127;
				c = Integer.rotateLeft(c, rotate);
				rotate += 3;
				int high = (i%4)*8;
				int val = c << high;
				hashes[i] ^= val;
			}
		}
		ByteBuffer buf = ByteBuffer.allocate(hashes.length*8);
		for (long val : hashes) {
			buf.putLong(val);
		}
		return HashType.SHA256.hash(buf.array());
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
			if (type.isSorted())
				Collections.sort(data);
		}

		protected final void addAll(Collection<String> c) {
			data.addAll(c);
			if (type.isSorted())
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
		INFO("General Info"),
		CRASHREP("System Report"),
		OPTIONS("Game Settings"),
		MODS("Mods"),
		CONFIGS("Config - "),
		JVM("JVM/OS Parameters");

		public final String title;

		private DataSections(String s) {
			title = s;
		}

		public boolean isSorted() {
			switch(this) {
				case OPTIONS:
				case MODS:
				case CONFIGS:
					return true;
				default:
					return false;
			}
		}
	}

}
