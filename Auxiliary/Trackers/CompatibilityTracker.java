/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.ModIncompatibilityException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class CompatibilityTracker {

	public static CompatibilityTracker instance = new CompatibilityTracker();

	private ArrayList<Incompatibility> issues = new ArrayList();

	private CompatibilityTracker() {

	}

	public void registerIncompatibility(ModList source, ModList issue, Severity level, String desc) {
		Incompatibility inc = new Incompatibility(source, issue, level, desc);
		if (!issues.contains(inc))
			issues.add(inc);
	}

	public void registerConditionalIncompatibility(ModList source, ModList issue, Severity level, String desc, boolean condition) {
		Incompatibility inc = new Incompatibility(source, issue, level, desc).setConditional(condition);
		if (!issues.contains(inc))
			issues.add(inc);
	}

	public void registerHostileIncompatibility(ModList source, ModList issue, String desc, boolean crash) {
		Incompatibility inc = new Incompatibility(source, issue, Severity.HOSTILITY, desc);
		if (crash)
			inc.setAlwaysCrash();
		if (!issues.contains(inc)) {
			issues.add(inc);
		}
	}

	public void test() {
		for (int i = 0; i < issues.size(); i++) {
			Incompatibility inc = issues.get(i);
			if (inc.isActive()) {
				Severity s = inc.severity;
				if (inc.alwaysCrash) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: "+inc.sourceMod.getDisplayName()+" has detected hostile modifications from "+inc.issueMod.getDisplayName()+" and was set to disallow continuing.\n");
					ReikaJavaLibrary.pConsole("It cannot function correctly. Check "+inc.sourceMod.getDisplayName()+" configs for an option to bypass this test and run anyways.");
					throw new RuntimeException();
				}
				else if (s == Severity.HOSTILITY) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: "+inc.sourceMod.getDisplayName()+" has detected hostile modifications from "+inc.issueMod.getDisplayName()+" but was set to allow continuing.\n");
					ReikaJavaLibrary.pConsole("However, it cannot function correctly. Contact the authors of the mods.");
				}
				else if (s.throwException) {
					new ModIncompatibilityException(inc.sourceMod, inc.issueMod, inc.message, s.isFatal);
				}
				else {
					ReikaJavaLibrary.pConsole("DRAGONAPI: "+inc.sourceMod.getDisplayName()+" has logged a compatibility issue with "+inc.issueMod.getDisplayName()+".\n");
					ReikaJavaLibrary.pConsole("However, it is able to continue without ill effects.");
				}
			}
		}
	}

	private static class Incompatibility {

		public final ModList sourceMod;
		public final ModList issueMod;

		public final Severity severity;

		public final String message;

		private boolean conditions = true;

		private boolean alwaysCrash;

		public Incompatibility(ModList source, ModList issue, Severity level, String msg) {
			sourceMod = source;
			issueMod = issue;
			severity = level;
			message = msg;
			alwaysCrash = false;
		}

		public Incompatibility setConditional(boolean isMet) {
			conditions = isMet;
			return this;
		}

		public Incompatibility setAlwaysCrash() {
			alwaysCrash = true;
			return this;
		}

		public boolean isActive() {
			return sourceMod.isLoaded() && issueMod.isLoaded() && conditions;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Incompatibility))
				return false;
			Incompatibility inc = (Incompatibility)o;
			if (inc.sourceMod != sourceMod || inc.issueMod != issueMod)
				return false;
			return inc.severity == severity;
		}

		public boolean shouldCrash() {
			return (alwaysCrash || severity.isFatal) && this.isActive();
		}

	}

	public enum Severity {
		NOEFFECT(false, false),
		GLITCH(true, false),
		HOSTILITY(true, false),
		FATAL(true, true);

		public boolean isFatal;
		public boolean throwException;

		private Severity(boolean excp, boolean fatal) {
			isFatal = fatal;
			throwException = excp;
		}
	}
}
