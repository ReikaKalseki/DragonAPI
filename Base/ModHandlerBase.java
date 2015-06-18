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

import java.util.Random;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** Reflection tools to read other mods. */
public abstract class ModHandlerBase {

	protected final Random rand = new Random();

	protected ModHandlerBase() {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Loading handler for mod "+this.getMod());
	}

	public abstract boolean initializedProperly();

	public abstract ModList getMod();

	protected void noMod() {
		//throw new ModHandlerException(this.getMod());
	}

	public boolean hasMod() {
		return this.getMod().isLoaded();
	}

	protected final void logFailure(Exception e) {
		ReflectiveFailureTracker.instance.logModReflectiveFailure(this.getMod(), e);
	}

}
