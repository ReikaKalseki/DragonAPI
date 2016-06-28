/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.deepinteract;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.auxiliary.trackers.ReflectiveFailureTracker;


public class TransvectorHandler {

	private static Class tileClass;
	private static Method getRelayTile;

	public static TileEntity getRelayedTile(TileEntity tv) {
		try {
			return tileClass != null && tileClass.isAssignableFrom(tv.getClass()) ? (TileEntity)getRelayTile.invoke(tv) : tv;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		if (ModList.THAUMICTINKER.isLoaded()) {
			try {
				tileClass = Class.forName("thaumic.tinkerer.common.block.tile.transvector.TileTransvector");
				getRelayTile = tileClass.getMethod("getTile");
			}
			catch (Exception e) {
				DragonAPICore.logError("Error loading Transvector Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.THAUMICTINKER, e);
			}
		}
	}
}
