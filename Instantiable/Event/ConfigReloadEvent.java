/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import Reika.DragonAPI.Base.DragonAPIMod;
import cpw.mods.fml.common.eventhandler.Event;


public class ConfigReloadEvent extends Event {

	public final DragonAPIMod mod;

	public ConfigReloadEvent() {
		this(null);
	}

	public ConfigReloadEvent(String s) {
		if (s != null) {
			mod = DragonAPIMod.getByName(s);
		}
		else {
			mod = null;
		}
	}

}
