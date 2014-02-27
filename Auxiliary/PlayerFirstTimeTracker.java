/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Base.PlayerTracker;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class PlayerFirstTimeTracker {

	private static final ArrayList<PlayerTracker> list = new ArrayList();

	public static void closeAndSave() {
		for (int i = 0; i < list.size(); i++) {
			PlayerTracker pt = list.get(i);
			pt.onUnload();
		}
	}

	public static void addTracker(PlayerTracker pt) {
		ReikaJavaLibrary.pConsole("DRAGONAPI: Creating player tracker "+pt.toString());
		list.add(pt);
	}

	public static void loadTrackers() {
		for (int i = 0; i < list.size(); i++) {
			PlayerTracker pt = list.get(i);
			pt.onLoad();
		}
	}

	public static void checkPlayer(EntityPlayer ep) {
		for (int i = 0; i < list.size(); i++) {
			PlayerTracker pt = list.get(i);
			if (!pt.hasPlayer(ep)) {
				//ReikaJavaLibrary.pConsole(pt+":"+ep.getEntityName());
				pt.onNewPlayer(ep);
				pt.addPlayer(ep);
			}
		}
	}
}
