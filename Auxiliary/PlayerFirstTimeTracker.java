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

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.PlayerTracker;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerFirstTimeTracker {

	private static final String BASE_TAG = "DragonAPI_PlayerTracker_";
	private static final ArrayList<PlayerTracker> list = new ArrayList();
	private static final ArrayList<String> tags = new ArrayList();

	public static void addTracker(PlayerTracker pt) {
		String s = pt.getID();
		if (tags.contains(s))
			throw new MisuseException("Duplicate PlayerTracker ID: "+s);
		ReikaJavaLibrary.pConsole("DRAGONAPI: Creating player tracker "+s);
		list.add(pt);
		tags.add(s);
	}

	public static void checkPlayer(EntityPlayer ep) {
		NBTTagCompound nbt = ep.getEntityData();
		for (int i = 0; i < list.size(); i++) {
			PlayerTracker pt = list.get(i);
			if (!hasPlayer(pt, ep)) {
				pt.onNewPlayer(ep);
				addPlayer(pt, ep);
			}
		}
	}

	private static void addPlayer(PlayerTracker pt, EntityPlayer ep) {
		String tag = BASE_TAG+pt.getID();
		ep.getEntityData().setBoolean(tag, true);
	}

	private static boolean hasPlayer(PlayerTracker pt, EntityPlayer ep) {
		String tag = BASE_TAG+pt.getID();
		return ep.getEntityData().getBoolean(tag);
	}
}