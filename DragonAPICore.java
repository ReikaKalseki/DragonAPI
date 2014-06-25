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
		String s = ((File)FMLInjectionData.data()[6]).getAbsolutePath();
		return new File(s.substring(0, s.length()-1));
	}

	private static boolean calculateReikasComputer() {
		try {
			String username = System.getProperty("user.name");
			boolean win = System.getProperty("os.name").equals("Windows 7");
			int cpus = Runtime.getRuntime().availableProcessors();
			String cpu = System.getProperty("os.arch");
			long diskSize = new File("/").getTotalSpace();
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
	}

	protected static Side getSide() {
		return FMLCommonHandler.instance().getEffectiveSide();
	}

	public static boolean isOnActualServer() {
		return getSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}

	public static boolean isSinglePlayer() {
		return getSide() == Side.SERVER && !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
	}
}
