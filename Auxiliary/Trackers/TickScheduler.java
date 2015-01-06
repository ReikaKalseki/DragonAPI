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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TickScheduler implements TickHandler {

	public static final TickScheduler instance = new TickScheduler();

	private final HashMap<ScheduledTickEvent, Integer> data = new HashMap();

	private TickScheduler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		Collection<ScheduledTickEvent> remove = new ArrayList();
		for (ScheduledTickEvent evt : data.keySet()) {
			int val = data.get(evt);
			val--;
			if (val == 0) {
				evt.fire();
				remove.add(evt);
			}
			else {
				data.put(evt, val);
			}
		}
		for (ScheduledTickEvent evt : remove) {
			data.remove(evt);
		}
	}

	@Override
	public TickType getType() {
		return TickType.ALL;
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
		data.put(evt, ticks);
	}

}
