/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Interfaces.Callbacks.EventWatchers.EventWatcher;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

/** Do NOT register global or persistent handlers here, or you will make ALL TEs run through it and hurt performance! */
public class TileUpdateEvent {

	private static TileUpdateWatcher[] watchers = null;
	private static int watcherSize = 0;

	public static void addWatcher(TileUpdateWatcher te) {
		watchers = ReikaArrayHelper.addToFastArray(watchers, te, TileUpdateWatcher.class);
		watcherSize = watchers != null ? watchers.length : 0;
	}

	public static void removeWatcher(TileUpdateWatcher te) {
		watchers = ReikaArrayHelper.removeFromFastArray(watchers, te);
		watcherSize = watchers != null ? watchers.length : 0;
	}

	public static boolean fire(TileEntity te) {
		if (te.isInvalid() || !te.hasWorldObj() || !te.worldObj.blockExists(te.xCoord, te.yCoord, te.zCoord))
			return true;
		if (watchers == null)
			return false;
		for (int i = 0; i < watcherSize; i++) {
			if (watchers[i].interceptTileUpdate(te))
				return true;
		}
		return false;
	}

	public static interface TileUpdateWatcher extends EventWatcher {

		public boolean interceptTileUpdate(TileEntity te);

	}

}
