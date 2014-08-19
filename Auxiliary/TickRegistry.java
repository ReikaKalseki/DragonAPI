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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;


public class TickRegistry {

	public static final TickRegistry instance = new TickRegistry();

	private ArrayList<TickHandler> playerTickers = new ArrayList();
	private ArrayList<TickHandler> worldTickers = new ArrayList();
	private ArrayList<TickHandler> renderTickers = new ArrayList();
	private ArrayList<TickHandler> clientTickers = new ArrayList();
	private ArrayList<TickHandler> serverTickers = new ArrayList();

	private TickRegistry() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent evt) {
		for (TickHandler h : playerTickers) {
			if (evt.phase == h.getPhase()) {
				h.tick(evt.player);
			}
		}
	}

	@SubscribeEvent
	public void renderTick(RenderTickEvent evt) {
		for (TickHandler h : renderTickers) {
			if (evt.phase == h.getPhase()) {
				h.tick(evt.renderTickTime);
			}
		}
	}

	@SubscribeEvent
	public void clientTick(ClientTickEvent evt) {
		for (TickHandler h : clientTickers) {
			if (evt.phase == h.getPhase()) {
				h.tick();
			}
		}
	}

	@SubscribeEvent
	public void worldTick(WorldTickEvent evt) {
		for (TickHandler h : worldTickers) {
			if (evt.phase == h.getPhase()) {
				h.tick(evt.world);
			}
		}
	}

	@SubscribeEvent
	public void serverTick(ServerTickEvent evt) {
		for (TickHandler h : serverTickers) {
			if (evt.phase == h.getPhase()) {
				h.tick();
			}
		}
	}

	public void registerTickHandler(TickHandler h, Side s) {
		TickType type = h.getType();
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
			break;
		}
	}

	public static interface TickHandler {

		public void tick(Object... tickData);

		public TickType getType();

		public Phase getPhase();

		public String getLabel();

	}

	public static enum TickType {
		/**
		 * Fired during the world evaluation loop
		 * server and client side
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
