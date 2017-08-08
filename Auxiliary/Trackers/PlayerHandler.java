/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
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
		if (ReikaPlayerAPI.isFake(evt.player))
			return;
		for (PlayerTracker p : trackers) {
			p.onPlayerLogin(evt.player);
		}
	}

	@SubscribeEvent
	public void onLogout(PlayerEvent.PlayerLoggedOutEvent evt) {
		if (ReikaPlayerAPI.isFake(evt.player))
			return;
		for (PlayerTracker p : trackers) {
			p.onPlayerLogout(evt.player);
		}
	}

	@SubscribeEvent
	public void onRespawn(PlayerEvent.PlayerRespawnEvent evt) {
		if (ReikaPlayerAPI.isFake(evt.player))
			return;
		for (PlayerTracker p : trackers) {
			p.onPlayerRespawn(evt.player);
		}
	}

	@SubscribeEvent
	public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent evt) {
		if (ReikaPlayerAPI.isFake(evt.player))
			return;
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
