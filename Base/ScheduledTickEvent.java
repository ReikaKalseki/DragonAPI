/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import cpw.mods.fml.common.eventhandler.Event;

public abstract class ScheduledTickEvent extends Event {

	private final Object[] data;

	public ScheduledTickEvent(Object... dat) {
		data = dat;
	}

	public Object getData(int i) {
		return data[i];
	}

}
