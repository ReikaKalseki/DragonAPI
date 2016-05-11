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

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

public class PlayerFirstTimeTracker {

	private static final String BASE_TAG = "DragonAPI_PlayerTracker_";
	private static final ArrayList<PlayerTracker> list = new ArrayList();
	private static final ArrayList<String> tags = new ArrayList();

	public static void addTracker(PlayerTracker pt) {
		String s = pt.getID();
		if (tags.contains(s))
			throw new MisuseException("Duplicate PlayerTracker ID: "+s);
		DragonAPICore.log("Creating player tracker "+s);
		list.add(pt);
		tags.add(s);
	}

	public static void checkPlayer(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData();
		for (PlayerTracker pt : list) {
			if (!hasPlayer(pt, ep)) {
				pt.onNewPlayer(ep);
				addPlayer(pt, ep);
			}
		}
	}

	private static void addPlayer(PlayerTracker pt, EntityPlayer ep) {
		String tag = BASE_TAG+pt.getID();
		ReikaPlayerAPI.getDeathPersistentNBT(ep).setBoolean(tag, true);
	}

	private static boolean hasPlayer(PlayerTracker pt, EntityPlayer ep) {
		String tag = BASE_TAG+pt.getID();
		return ReikaPlayerAPI.getDeathPersistentNBT(ep).getBoolean(tag);
	}

	public static interface PlayerTracker {

		public void onNewPlayer(EntityPlayer ep);

		/** This MUST be unique! */
		public String getID();
	}
}
