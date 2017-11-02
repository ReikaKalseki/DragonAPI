/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import java.util.Collection;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;


/** Generally not used for actual profiling handling, but for the massive number of hooks it provides into vanilla code. Check the profiler's calls
  to see potential uses. */
public class ProfileEvent {

	private static final MultiMap<String, ProfileEventWatcher> watchedTags = new MultiMap().setNullEmpty();

	public static void fire(String tag) {
		Collection<ProfileEventWatcher> c = watchedTags.get(tag);
		if (c != null) {
			for (ProfileEventWatcher p : c) {
				p.onCall(tag);
			}
		}
		//MinecraftForge.EVENT_BUS.post(new ProfileEventObject(tag));
	}

	public static void registerHandler(String tag, ProfileEventWatcher w) {
		watchedTags.addValue(tag, w);
	}
	/*
	private static class ProfileEventObject {

		public final String sectionName;

		public ProfileEventObject(String s) {
			sectionName = s;
		}
	}*/

	public static interface ProfileEventWatcher {

		public void onCall(String tag);

	}
}
