/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;


public class TickRegistry {

	public static final TickRegistry instance = new TickRegistry();

	private ArrayList<TickHandler> playerTickers = new ArrayList();
	private ArrayList<TickHandler> worldTickers = new ArrayList();
	private ArrayList<TickHandler> renderTickers = new ArrayList();
	private ArrayList<TickHandler> clientTickers = new ArrayList();
	private ArrayList<TickHandler> serverTickers = new ArrayList();

	private static boolean posted = false;

	private TickRegistry() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent evt) {
		for (TickHandler h : playerTickers) {
			if (h.canFire(evt.phase)) {
				h.tick(TickType.PLAYER, evt.player, evt.phase);
			}
		}
	}

	@SubscribeEvent
	public void renderTick(RenderTickEvent evt) {
		for (TickHandler h : renderTickers) {
			if (h.canFire(evt.phase)) {
				h.tick(TickType.RENDER, evt.renderTickTime, evt.phase);
			}
		}
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent evt) {
		for (TickHandler h : clientTickers) {
			if (h.canFire(evt.phase)) {
				h.tick(TickType.CLIENT, evt.phase);
			}
		}
		if (!posted) {
			MinecraftForge.EVENT_BUS.post(new GameFinishedLoadingEvent());
			posted = true;
		}
	}

	@SubscribeEvent
	public void worldTick(WorldTickEvent evt) {
		for (TickHandler h : worldTickers) {
			if (h.canFire(evt.phase)) {
				h.tick(TickType.WORLD, evt.world, evt.phase);
			}
		}
	}

	@SubscribeEvent
	public void serverTick(ServerTickEvent evt) {
		for (TickHandler h : serverTickers) {
			if (h.canFire(evt.phase)) {
				h.tick(TickType.SERVER, evt.phase);
			}
		}
	}

	public void registerTickHandler(TickHandler h) {
		for (TickType type : h.getType()) {
			switch(type) {
				case CLIENT:
					clientTickers.add(h);
					break;
				case PLAYER:
					playerTickers.add(h);
					break;
				case RENDER:
					renderTickers.add(h);
					break;
				case SERVER:
					serverTickers.add(h);
					break;
				case WORLD:
					worldTickers.add(h);
					break;/*
				case ALL:
					clientTickers.add(h);
					playerTickers.add(h);
					renderTickers.add(h);
					serverTickers.add(h);
					worldTickers.add(h);
					break;*/
			}
		}
	}

	public static interface TickHandler {

		public void tick(TickType type, Object... tickData);

		public EnumSet<TickType> getType();

		public boolean canFire(Phase p);

		public String getLabel();

	}

	public static enum TickType {
		/**
		 * Fired during the world evaluation loop
		 * server side only! ("and client side" is false)
		 *
		 * arg 0 : The world that is ticking
		 */
		WORLD,
		/**
		 * client side
		 * Fired during the render processing phase
		 * arg 0 : float "partial render time"
		 */
		RENDER,
		/**
		 * client side only
		 * Fired once per client tick loop.
		 */
		CLIENT,
		/**
		 * client and server side.
		 * Fired whenever the players update loop runs.
		 * arg 0 : the player
		 * arg 1 : the world the player is in
		 */
		PLAYER,
		/**
		 * server side only.
		 * This is the server game tick.
		 * Fired once per tick loop on the server.
		 */
		SERVER;
	}

}
