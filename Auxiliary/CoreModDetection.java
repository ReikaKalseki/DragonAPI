/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.launchwrapper.Launch;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class CoreModDetection {

	private static final Class liteClass;
	private static final Class optiClass;
	private static final Class fastClass;

	public static boolean liteLoaderInstalled() {
		return liteClass != null;
	}

	public static boolean optifineInstalled() {
		return optiClass != null;
	}

	public static boolean fastCraftInstalled() {
		return Launch.blackboard.get("fcVersion") != null;//fastClass != null;
	}

	static {
		Class c = null;
		try {
			c = Class.forName("com.mumfrey.liteloader.core.LiteLoader");
			ReikaJavaLibrary.pConsole("DRAGONAPI: LiteLoader detected. Loading compatibility features.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tNote that some parts of the game, especially sounds and textures, may error out.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tTry reloading resources (F3+T) to fix this.");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: LiteLoader not detected.");
		}
		liteClass = c;

		c = null;
		try {
			c = Class.forName("optifine.OptiFineTweaker");
			ReikaJavaLibrary.pConsole("DRAGONAPI: Optifine detected. Loading compatibility features.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tNote that some parts of the game, especially rendering and textures, may error out.");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Optifine not detected.");
		}
		optiClass = c;

		c = null;
		try {
			c = Class.forName("fastcraft.J");
			ReikaJavaLibrary.pConsole("DRAGONAPI: FastCraft detected. Loading compatibility features.");
			ReikaJavaLibrary.pConsole("DRAGONAPI: \t\tNote that some parts of the game, especially render and block changes, may error out.");
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: FastCraft not detected.");
		}
		fastClass = c;
	}
}
