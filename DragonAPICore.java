/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import net.minecraftforge.common.ForgeVersion;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DragonAPICore {

	protected DragonAPICore() {throw new MisuseException("The class "+this.getClass()+" cannot be instantiated!");}

	public static final Random rand = new Random();

	private static final boolean reika = calculateReikasComputer();

	public static final String last_API_Version = "@MAJOR_VERSION@"+"@MINOR_VERSION@";

	public static boolean debugtest = false;

	private static boolean loaded;

	private static final long launchTime = ManagementFactory.getRuntimeMXBean().getStartTime();

	private static final String MINFORGE = "required-after:Forge@[10.13.4.1558,);"; //was 1205/1231/1291
	public static final String dependencies = MINFORGE+"after:BuildCraft|Energy;after:IC2;after:ThermalExpansion;after:Thaumcraft;"+
			"after:powersuits;after:GalacticCraft;after:Mystcraft;after:UniversalElectricity;after:Forestry;after:MagicBees;"+
			"after:BinnieCore;after:Natura;after:TConstruct;after:ProjRed|Core;after:bluepower;after:Waila;after:funkylocomotion;after:chisel;"+
			"after:ComputerCraft;after:ThermalFoundation;after:CarpentersBlocks";

	public static final String FORUM_PAGE = "http://www.minecraftforum.net/topic/1969694-";
	public static final UUID Reika_UUID = UUID.fromString("e5248026-6874-4954-9a02-aa8910d08f31");

	public static URL getReikaForumPage() {
		try {
			return new URL(FORUM_PAGE);
		}
		catch (MalformedURLException e) {
			throw new RegistrationException(DragonAPIInit.instance, "Reika's mods provided a malformed URL for their documentation site!", e);
		}
	}

	public static final boolean hasAllClasses() {
		return true;
	}

	public static File getMinecraftDirectory() {
		return (File)FMLInjectionData.data()[6];
	}

	public static String getMinecraftDirectoryString() {
		String s = getMinecraftDirectory().getAbsolutePath();
		if (s.endsWith("/.") || s.endsWith("\\.")) {
			s = s.substring(0, s.length()-2);
		}
		s = s.replaceAll("\\\\", "/");
		return s;
	}

	private static boolean calculateReikasComputer() {
		try {
			String username = System.getProperty("user.name");
			boolean win = System.getProperty("os.name").equals("Windows 7");
			int cpus = Runtime.getRuntime().availableProcessors();
			String cpu = System.getProperty("os.arch");
			long diskSize = new File("c:").getTotalSpace();

			if (win && "amd64".equals(cpu)) {
				if (diskSize == 484964069376L && cpus == 4 && "RadicalOne".equals(username))
					return true;
				if (diskSize == 119926681600L && cpus == 8 && "Reika".equals(username))
					return true;
			}
			return false;
		}
		catch (Throwable e) {
			return false;
		}
	}

	public static boolean isReikasComputer() {
		return reika;
	}

	static {
		if (isReikasComputer())
			ReikaJavaLibrary.pConsole("DRAGONAPI: Loading on Reika's computer; Dev features enabled.");

		//ReikaMathCacher.initalize();
		validateForgeVersions();
	}

	protected static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}

	private static void validateForgeVersions() {
		int major = ForgeVersion.majorVersion;
		int minor = ForgeVersion.minorVersion;
		int rev = ForgeVersion.revisionVersion;
		int build = ForgeVersion.buildVersion;

		int recbuild = 1291;
		if (build < recbuild) {
			log("The version of Forge you are using is compatible but not recommended.");
			log(String.format("Consider updating to at least %d.%d.%d.%d.", major, minor, rev, recbuild));
		}
	}

	public static boolean isOnActualServer() {
		return getSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}

	public static boolean isSinglePlayer() {
		return getSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}

	public static void debugPrint(Object o) {
		ReikaJavaLibrary.pConsole(o);
		if (!ReikaObfuscationHelper.isDeObfEnvironment())
			Thread.dumpStack();
	}

	public static void debug(Object s) {
		DragonAPIInit.instance.getModLogger().debug(s);
	}

	public static void log(Object s) {
		DragonAPIInit.instance.getModLogger().log(s);
	}

	public static void logError(Object s) {
		DragonAPIInit.instance.getModLogger().logError(s);
	}

	public static void logError(Object o, Side side) {
		if (FMLCommonHandler.instance().getEffectiveSide() == side)
			logError(o);
	}

	public static class DragonAPILoadWatcher {

		public static final DragonAPILoadWatcher instance = new DragonAPILoadWatcher();

		private DragonAPILoadWatcher() {

		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void load(GameFinishedLoadingEvent evt) {
			loaded = true;
		}

	}

	public static boolean hasGameLoaded() {
		return loaded;
	}

	public static long getLaunchTime() {
		return launchTime;
	}

	public static int getSystemTimeAsInt() {
		long t = System.currentTimeMillis();
		return (int)(t%(Integer.MAX_VALUE+1));
	}
}
