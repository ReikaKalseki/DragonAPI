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

import java.util.EnumSet;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Maps.TimerMap;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public class TickScheduler implements TickHandler {

	public static final TickScheduler instance = new TickScheduler();

	private final TimerMap<ScheduledTickEvent> serverData = new TimerMap();
	private final TimerMap<ScheduledTickEvent> clientData = new TimerMap();
	private static final Object lock = new Object();

	private TickScheduler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		synchronized(lock) {
			this.getData(type).tick();
		}
	}

	private TimerMap<ScheduledTickEvent> getData(TickType type) {
		return type == TickType.SERVER ? serverData : clientData;
	}

	public void clear() {
		synchronized(lock) {
			serverData.clear();
			clientData.clear();
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
		if (ticks <= 0) {
			DragonAPICore.logError("Something tried scheduling a delayed event with zero delay!");
			Thread.dumpStack();
			return;
		}
		synchronized(lock) {
			if (evt.runOnSide(Side.SERVER))
				serverData.put(evt, ticks);
			if (evt.runOnSide(Side.CLIENT))
				clientData.put(evt, ticks);
		}
	}

}
