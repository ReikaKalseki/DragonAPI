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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;

import cpw.mods.fml.common.gameevent.TickEvent;

//MultiThread-safe PlayerChunkLoadTracker
//Can be used to disable chunk loading around a specific player.
public class PlayerChunkTracker implements TickRegistry.TickHandler {

	// Singleton instance
	public static final PlayerChunkTracker instance = new PlayerChunkTracker();

	// Ticks until player gets unregistered.
	private static final int UNREGISTER_THRESHOLD = 40; // 2 sec
	private final HashMap<EntityPlayer, TrackerEntry> trackedPlayers = new HashMap(),
			queuedWaitingEntries = new HashMap();

	// Flag if the Tracker is currently in the tick of checking all entries'
	// conditions.
	// Used to check where to add new tracker entries.
	private boolean isInTick = false;

	private PlayerChunkTracker() {
	}

	@Override
	public void tick(TickRegistry.TickType type, Object... tickData) {
		isInTick = true;
		synchronized (trackedPlayers) {
			Iterator<Map.Entry<EntityPlayer, TrackerEntry>> iterator = trackedPlayers.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<EntityPlayer, TrackerEntry> entry = iterator.next();
				TrackerEntry tracker = entry.getValue();
				if (entry.getKey().isDead || !tracker.condition.shouldBeTracked(entry.getKey())) {
					tracker.timeout--;
				}
				else {
					tracker.timeout = UNREGISTER_THRESHOLD;
				}
				if (tracker.timeout <= 0) {
					iterator.remove();
					entry.getValue().condition.onUntrack(entry.getKey());

					// Triggering chunk reload.
					// "move" the previously managed distance FAR away so
					// Minecraft thinks it needs to create new chunks around the
					// player.
					if (entry.getKey() instanceof EntityPlayerMP) {
						EntityPlayerMP emp = (EntityPlayerMP)entry.getKey();
						emp.managedPosX = Double.MAX_VALUE;
						emp.managedPosZ = Double.MAX_VALUE;
					}
				}
			}
		}
		isInTick = false;
		synchronized (queuedWaitingEntries) {
			trackedPlayers.putAll(queuedWaitingEntries);
			queuedWaitingEntries.clear();
		}
	}

	// Check inserted via ASM into World.setActivePlayerChunksAndCheckLight
	// If this returns true, the chunks around the player are not added to the
	// list of chunks that have to be loaded.
	public static boolean shouldStopChunkloadingFor(EntityPlayer player) {
		return PlayerChunkTracker.instance.trackedPlayers.containsKey(player);
	}

	// Adds the player with his condition to the
	public static void startTrackingPlayer(EntityPlayer player, TrackingCondition condition) {
		PlayerChunkTracker pct = PlayerChunkTracker.instance;
		TrackerEntry newEntry = new TrackerEntry(UNREGISTER_THRESHOLD, condition);
		if (pct.isInTick) {
			synchronized (pct.queuedWaitingEntries) {
				pct.queuedWaitingEntries.put(player, newEntry);
			}
		}
		else {
			synchronized (pct.trackedPlayers) {
				pct.trackedPlayers.put(player, newEntry);
			}
		}
	}

	// Should only execute for the server.
	@Override
	public EnumSet<TickRegistry.TickType> getType() {
		return EnumSet.of(TickRegistry.TickType.SERVER);
	}

	@Override
	public boolean canFire(TickEvent.Phase p) {
		return p == TickEvent.Phase.END;
	}

	@Override
	public String getLabel() {
		return "Player Chunk Tracker";
	}

	public static void tryUpdatePlayerPertinentChunks(PlayerManager pm, EntityPlayerMP mp) {
		if (!instance.shouldStopChunkloadingFor(mp))
			pm.updatePlayerPertinentChunks(mp);
	}

	public static class TrackerEntry {

		private int timeout;
		private TrackingCondition condition;

		public TrackerEntry(int timeout, TrackingCondition condition) {
			this.timeout = timeout;
			this.condition = condition;
		}

	}

	public static interface TrackingCondition {

		// Return true, if the player should remain tracked, false otherwise.
		public boolean shouldBeTracked(EntityPlayer player);

		// Called once a player gets removed from the tracker.
		public void onUntrack(EntityPlayer player);

	}

}
