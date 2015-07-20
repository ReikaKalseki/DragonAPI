/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Collection;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.ModEntry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ReflectiveFailureTracker {

	public static final ReflectiveFailureTracker instance = new ReflectiveFailureTracker();

	private final MultiMap<ModEntry, ExceptionLog> data = new MultiMap();

	private ReflectiveFailureTracker() {

	}

	public void logModReflectiveFailure(ModEntry mod, Exception e) {
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		data.addValue(mod, new ExceptionLog(className, e));
	}

	public void print() {
		if (!data.isEmpty()) {
			ReikaJavaLibrary.pConsole("===============================================================================================");

			this.log("Some reflective mod handlers have failed.");
			this.log("Please try updating all involved mods, and if this fails to fix the issue, notify the author of the handlers.");

			for (ModEntry mod : data.keySet()) {
				Collection<ExceptionLog> c = data.get(mod);
				this.log(String.format("%d failure%s for %s ('%s'):", c.size(), c.size() > 1 ? "s" : "", mod.getDisplayName(), mod.getModLabel()));
				for (ExceptionLog e : c) {
					this.log(e.toString());
				}
				this.log("");
			}


			this.log("For further information, including full stacktraces, consult the loading logs, and search for the erroring classes' names.");
			ReikaJavaLibrary.pConsole("===============================================================================================");
		}
	}

	private void log(String s) {
		ReikaJavaLibrary.pConsole("DRAGONAPI: "+s);
	}

	private static class ExceptionLog {

		private final String erroredClass;
		private final Exception error;

		private ExceptionLog(String c, Exception e) {
			error = e;
			erroredClass = c;
		}

		@Override
		public final String toString() {
			return error.getClass().getSimpleName()+" \""+error.getMessage()+"\" thrown from "+erroredClass;
		}

	}

}
