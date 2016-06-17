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

import java.util.EnumSet;
import java.util.HashMap;

import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TickScheduler implements TickHandler {

	public static final TickScheduler instance = new TickScheduler();

	private final HashMap<ScheduledTickEvent, Integer> data = new HashMap();
	private static final Object lock = new Object();

	private TickScheduler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		synchronized(lock) {
			HashMap<ScheduledTickEvent, Integer> map = new HashMap();
			if (!data.isEmpty()) {
				for (ScheduledTickEvent evt : data.keySet()) {
					int val = data.get(evt);
					val--;
					if (val == 0) {
						evt.fire();
					}
					else {
						map.put(evt, val);
					}
				}
				data.clear();
				data.putAll(map);
			}
		}
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.SERVER, TickType.CLIENT);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

	@Override
	public String getLabel() {
		return "Scheduled Tick Handler";
	}

	public void scheduleEvent(ScheduledTickEvent evt, int ticks) {
		synchronized(lock) {
			data.put(evt, ticks);
		}
	}

}
