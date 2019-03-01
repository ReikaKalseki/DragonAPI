/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.EnumSet;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class RebootScheduler implements TickHandler {

	public static final RebootScheduler instance = new RebootScheduler();

	private final long rebootInterval = DragonOptions.AUTOREBOOT.getValue()*1000;

	private RebootScheduler() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> getType() {
		return null;
	}

	@Override
	public boolean canFire(Phase p) {
		return false;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
