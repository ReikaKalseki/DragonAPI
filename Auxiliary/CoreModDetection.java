/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.launchwrapper.Launch;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public enum CoreModDetection {

	OPTIFINE("optifine.OptiFineTweaker", "rendering and textures", ""),
	LITELOADER("com.mumfrey.liteloader.core.LiteLoader", "sounds and textures", "Try reloading resources (F3+T) to fix this."),
	FASTCRAFT(Launch.blackboard.get("fcVersion") != null, "render and block changes", ""),
	VIVE("com.mtbs3d.minecrift.api.IRoomscaleAdapter", "rendering and interface", ""),
	COLOREDLIGHTS("coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin", "rendering", "");

	private final Class refClass;
	private final boolean isLoaded;
	private final String warning;
	private final String message;

	public static final CoreModDetection[] list = values();

	private CoreModDetection(String s, String w, String m) {
		this(ReikaJavaLibrary.getClassNoException(s), w, m);
	}

	private CoreModDetection(Class c, String w, String m) {
		this(c, c != null, w, m);
	}

	private CoreModDetection(boolean flag, String w, String m) {
		this(null, flag, w, m);
	}

	private CoreModDetection(Class c, boolean flag, String w, String m) {
		refClass = c;
		isLoaded = flag;
		warning = w;
		message = m;
	}

	public boolean isInstalled() {
		return isLoaded;
	}

	static {
		for (int i = 0; i < values().length; i++) {
			CoreModDetection c = values()[i];
			if (c.isInstalled()) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+c+" detected. Loading compatibility features.");
				ReikaJavaLibrary.pConsole("\t\tNote that some parts of the game, especially "+c.warning+", may error out.");
				ReikaJavaLibrary.pConsole("\t\t"+c.message);
			}
			else {
				ReikaJavaLibrary.pConsole("DRAGONAPI: "+c+" not detected.");
			}
		}
	}

	public static String getStatus() {
		Collection<CoreModDetection> li = new ArrayList();
		for (CoreModDetection cm : list) {
			if (cm.isInstalled())
				li.add(cm);
		}
		return li.isEmpty() ? "None" : li.toString();
	}
}
