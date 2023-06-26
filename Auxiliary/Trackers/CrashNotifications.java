/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;

import cpw.mods.fml.common.FMLCommonHandler;



public class CrashNotifications {

	public static final CrashNotifications instance = new CrashNotifications();

	private final MultiMap<Class, CrashNotification> entries = new MultiMap();

	private CrashNotifications() {

	}

	/** Supply a null class to wildcard and parse all error types. */
	public void addNotification(Class<? extends Throwable> crashType, CrashNotification n) {
		entries.addValue(crashType, n);
	}

	public void notifyCrash(CrashReport c) {
		Throwable e = c.getCrashCause();
		for (CrashNotification n : entries.get(e.getClass())) {
			String s = n.addMessage(e);
			if (s != null)
				c.getCategory().addCrashSection(n.getLabel(), s);
		}
		for (CrashNotification n : entries.get(null)) {
			String s = n.addMessage(e);
			if (s != null)
				c.getCategory().addCrashSection(n.getLabel(), s);
		}
	}

	public static void fireOF(CrashReport c) {
		instance.notifyCrash(c);
	}

	public static void fire(FMLCommonHandler fml, CrashReport c, CrashReportCategory cat) {
		instance.notifyCrash(c);
		fml.enhanceCrashReport(c, cat);
	}

	public static interface CrashNotification {

		public String getLabel();

		/** Return null to append nothing. */
		public String addMessage(Throwable crash);

	}

}
