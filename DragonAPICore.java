/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import net.minecraftforge.common.ForgeVersion;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;

public class DragonAPICore {

	protected DragonAPICore() {throw new MisuseException("The class "+this.getClass()+" cannot be instantiated!");}

	protected static final Random rand = new Random();

	private static final boolean reika = calculateReikasComputer();

	public static boolean debugtest = false;

	private static final String MINFORGE = "required-after:Forge@[10.13.0.1205,);";
	public static final String dependencies = MINFORGE+"after:BuildCraft|Energy;after:IC2;after:ThermalExpansion;after:Thaumcraft;"+
			"after:powersuits;after:GalacticCraft;after:Mystcraft;after:UniversalElectricity;after:Forestry;after:MagicBees;"+
			"after:BinnieCore;after:Natura;after:TConstruct";

	public static final String FORUM_PAGE = "http://www.minecraftforum.net/topic/1969694-";

	public static URL getReikaForumPage() {
		try {
			return new URL(FORUM_PAGE);
		}
		catch (MalformedURLException e) {
			ReikaJavaLibrary.pConsole("The mod provided a malformed URL for its documentation site!");
			e.printStackTrace();
			return null;
		}
	}

	public static final boolean hasAllClasses() {
		return true;
	}

	public static File getMinecraftDirectory() {
		return (File)FMLInjectionData.data()[6];
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

		int recbuild = 1208;
		if (build < recbuild) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: The version of Forge you are using is compatible but not recommended.");
			ReikaJavaLibrary.pConsole(String.format("Consider updating to at least %d.%d.%d.%d.", major, minor, rev, recbuild));
		}
	}

	public static boolean isOnActualServer() {
		return getSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}

	public static boolean isSinglePlayer() {
		return getSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}
}
