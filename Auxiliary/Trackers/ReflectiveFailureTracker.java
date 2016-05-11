/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.Collection;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;

public class ReflectiveFailureTracker {

	public static final ReflectiveFailureTracker instance = new ReflectiveFailureTracker();

	private final MultiMap<ModEntry, ExceptionLog> data = new MultiMap();

	private ReflectiveFailureTracker() {

	}

	public void logModReflectiveFailure(ModEntry mod, Exception e) {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] tr = Thread.currentThread().getStackTrace();
		//0 is Thread, 1 is ReflectiveFailureTracker, 2&3 are the class
		for (int i = 2; i <= 3; i++) {
			if (i < tr.length) {
				sb.append(tr[i].getClassName());
				if (i < tr.length-1 && i < 3) {
					sb.append(" / ");
				}
			}
		}
		String className = sb.toString();
		data.addValue(mod, new ExceptionLog(className, e));
	}

	public void print() {
		if (!data.isEmpty()) {
			DragonAPICore.log("===============================================================================================");

			this.log("Some reflective mod handlers have failed.");
			this.log("Please try updating all involved mods, and if this fails to fix the issue, notify the author of the handlers.");

			for (ModEntry mod : data.keySet()) {
				Collection<ExceptionLog> c = data.get(mod);
				this.log(String.format("%d failure%s for %s ('%s'):", c.size(), c.size() > 1 ? "s" : "", mod.getDisplayName(), mod.getModLabel()));
				for (ExceptionLog e : c) {
					this.log(e.toString());
				}
				DragonAPICore.log("");
			}


			this.log("For further information, including full stacktraces, consult the loading logs, and search for the erroring classes' names.");
			DragonAPICore.log("===============================================================================================");
		}
	}

	private void log(String s) {
		DragonAPICore.logError(s);
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
