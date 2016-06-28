/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact;

import reika.dragonapi.mod.interact.itemhandlers.TwilightForestHandler;

public class ReikaTwilightHelper {

	//public static final int TWILIGHT_ID = 7;

	public static boolean isTwilightForestBoss(String name) {
		if (name == null)
			return false;
		if (name.equals("Ur-Ghast"))
			return true;
		if (name.equals("Hydra"))
			return true;
		if (name.equals("Naga"))
			return true;
		return false;
	}

	public static int getDimensionID() {
		return TwilightForestHandler.getInstance().dimensionID;
	}

}
