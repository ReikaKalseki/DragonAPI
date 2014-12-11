/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class PlayerHandler {

	public static final PlayerHandler instance = new PlayerHandler();

	private final ArrayList<PlayerTracker> trackers = new ArrayList();

	private PlayerHandler() {
		FMLCommonHandler.instance().bus().register(this);
	}

	public void registerTracker(PlayerTracker p) {
		trackers.add(p);
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent evt) {
		for (PlayerTracker p : trackers) {
			p.onPlayerLogin(evt.player);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedOutEvent evt) {
		for (PlayerTracker p : trackers) {
			p.onPlayerLogout(evt.player);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerRespawnEvent evt) {
		for (PlayerTracker p : trackers) {
			p.onPlayerRespawn(evt.player);
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerChangedDimensionEvent evt) {
		for (PlayerTracker p : trackers) {
			p.onPlayerChangedDimension(evt.player, evt.fromDim, evt.toDim);
		}
	}

	public static interface PlayerTracker {

		public void onPlayerLogin(EntityPlayer ep);

		public void onPlayerLogout(EntityPlayer player);

		public void onPlayerChangedDimension(EntityPlayer player, int dimFrom, int dimTo);

		public void onPlayerRespawn(EntityPlayer player);

	}

}
