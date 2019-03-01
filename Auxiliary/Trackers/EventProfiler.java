/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.terraingen.ChunkProviderEvent.ReplaceBiomeBlocks;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Event.BlockTickEvent;
import Reika.DragonAPI.Instantiable.Event.EntityAboutToRayTraceEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Instantiable.Event.TileUpdateEvent;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.IEventListener;

/** Only works on the server side for now. */
public class EventProfiler {

	//public static final EventProfiler instance = new EventProfiler();

	private static HashMap<String, Class<? extends Event>> fullNameShortcuts = new HashMap();

	static {
		addShortcut(ReplaceBiomeBlocks.class);
		addShortcut(PopulateChunkEvent.Populate.class);
		addShortcut(EntityJoinWorldEvent.class);
		addShortcut(LivingHurtEvent.class);
		addShortcut(EntityItemPickupEvent.class);
		addShortcut(ItemTooltipEvent.class);
		addShortcut(LivingUpdateEvent.class);

		//CLIENT
		addShortcut(RenderGameOverlayEvent.class);
		addShortcut(RenderWorldEvent.class);

		//DRAGONAPI
		addShortcut(SetBlockEvent.class);
		addShortcut(BlockTickEvent.class);
		addShortcut(EntityAboutToRayTraceEvent.class);
		addShortcut(TileUpdateEvent.class);
		addShortcut(ItemUpdateEvent.class);
	}

	public static void addShortcut(Class<? extends Event> eventType) {
		fullNameShortcuts.put(eventType.getSimpleName(), eventType);
	}

	private static Class currentProfile;
	private static HashMap<IEventListener, EventProfile> profileData = new HashMap(); //not class as keys, since all are basically ASMEventHandler
	private static int totalCount;

	//private EventProfiler() {
	//
	//}

	public static void startProfiling(Class<? extends Event> c) {
		if (currentProfile != null) {
			DragonAPICore.logError("You cannot start profiling while profiling is running!");
			Thread.dumpStack();
			return;
		}
		currentProfile = c;
	}

	public static ProfileStartStatus startProfiling(String eventType) {
		if (currentProfile != null)
			return ProfileStartStatus.ALREADYRUNNING;
		profileData.clear();
		totalCount = 0;
		try {
			if (fullNameShortcuts.containsKey(eventType))
				startProfiling(fullNameShortcuts.get(eventType));
			else
				startProfiling((Class<? extends Event>)Class.forName(eventType));
			return ProfileStartStatus.SUCCESS;
		}
		catch (ClassNotFoundException e) {
			return ProfileStartStatus.NOSUCHCLASS;
		}
		catch (ClassCastException e) {
			return ProfileStartStatus.NOTANEVENT;
		}
	}

	public static void finishProfiling() {
		currentProfile = null;
	}

	public static ArrayList<EventProfile> getProfilingData() {
		ArrayList<EventProfile> li = new ArrayList(profileData.values());
		Iterator<EventProfile> it = li.iterator();
		while (it.hasNext()) {
			EventProfile e = it.next();
			if (e.identifier == null)
				it.remove();
		}
		Collections.sort(li);
		return li;
	}

	public static long getTotalProfilingTime() {
		//return totalProfiledTime;
		long total = 0;
		for (EventProfile g : profileData.values()) {
			if (g.identifier != null)
				total += g.getTotalTime();
		}
		return total;
	}

	public static String getProfiledEventType() {
		return currentProfile.getName();
	}

	public static int getEventFireCount() {
		return totalCount/profileData.size(); //since count is incremented once per handle, not per fire
	}

	private static EventProfile getOrCreateProfile(IEventListener e) {
		EventProfile a = profileData.get(e);
		if (a == null) {
			a = new EventProfile(e);
			profileData.put(e, a);
		}
		return a;
	}

	public static void firePre(Event e, IEventListener listener) {
		if (e.getClass() == currentProfile) {
			EventProfile a = getOrCreateProfile(listener);
			if (a.identifier != null) {
				a.startTiming();
				totalCount++;
			}
		}
	}

	public static void firePost(Event e, IEventListener listener) {
		if (e.getClass() == currentProfile) {
			EventProfile a = getOrCreateProfile(listener);
			if (a.identifier != null) {
				a.stopTiming();
			}
		}
	}

	public static enum ProfileStartStatus {
		SUCCESS(),
		ALREADYRUNNING(),
		NOSUCHCLASS(),
		NOTANEVENT();
	}

	public static class EventProfile implements Comparable<EventProfile> {

		//private final String eventType;
		public final String identifier;
		public final Class identifyingClass;

		private long totalTime;
		private int fireCount;
		private long lastStart;

		private EventProfile(IEventListener e) {
			identifyingClass = e.getClass();
			String s = e.toString(); //not getClass
			String arg = currentProfile.getName().replace(".", "/");
			s = s.replace("(L"+arg+";)V", "()");
			if (s.startsWith("ASM: "))
				s = s.substring("ASM: ".length());
			else
				s = null;//"FORGE PRIORITY WRAPPER: "+s; //null these ones out entirely
			identifier = s;
		}

		private void startTiming() {
			fireCount++;
			lastStart = System.nanoTime();
		}

		private void stopTiming() {
			totalTime += System.nanoTime()-lastStart;
		}

		public long getTotalTime() {
			return totalTime;
		}

		public long getAverageTime() {
			return totalTime/fireCount;
		}

		@Override
		public int compareTo(EventProfile o) {
			return -Long.compare(totalTime, o.totalTime);
		}

	}

}
