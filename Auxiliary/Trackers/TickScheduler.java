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

import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TickScheduler implements TickHandler {

	public static final TickScheduler instance = new TickScheduler();

	private final TimerMap<ScheduledTickEvent> data = new TimerMap();
	private static final Object lock = new Object();

	private TickScheduler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		synchronized(lock) {
			data.tick();
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
