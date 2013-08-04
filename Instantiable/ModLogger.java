/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public class ModLogger {

	private final boolean logLoading;
	private final boolean printDebug;

	private final DragonAPIMod mod;

	public ModLogger(DragonAPIMod mod, boolean load, boolean debug) {
		this.mod = mod;
		logLoading = load;
		printDebug = debug;
	}

	public void debug(Object o) {
		if (printDebug)
			ReikaJavaLibrary.pConsole(o);
	}

	public void log(Object o) {
		if (logLoading)
			ReikaJavaLibrary.pConsole(o);
	}

	public void logError(Object o) {
		ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": There was an error:\n");
		ReikaJavaLibrary.pConsole(o);
	}

	public boolean shouldLog() {
		return logLoading;
	}

	public boolean shouldDebug() {
		return printDebug;
	}

}
