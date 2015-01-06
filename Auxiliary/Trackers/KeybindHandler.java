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
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeybindHandler {

	private static final KeyTicker ticker = new KeyTicker();
	public static final KeybindHandler instance = new KeybindHandler();

	//private final MultiMap<Integer, KeyBinding> map = new MultiMap();

	private final Collection<KeyBinding> keys = new ArrayList();
	private final HashMap<KeyBinding, Boolean> pressed = new HashMap();

	private KeybindHandler() {
		//MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.instance.registerTickHandler(ticker, Side.CLIENT);
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {

	}

	public void addKeybind(KeyBinding kt) {
		//map.addValue(kt.getKey(), kt);
		ClientRegistry.registerKeyBinding(kt);
		keys.add(kt);
	}
	/*
	public static interface KeyTrigger {

		public int getKey();
		public void onPressed();

	}
	 */

	public static class KeyTicker implements TickHandler {

		private KeyTicker() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			for (KeyBinding key : instance.keys) {
				boolean press = key.getIsKeyPressed();
				if (press) {
					MinecraftForge.EVENT_BUS.post(new KeyPressEvent(key));
				}
				instance.pressed.put(key, press);
			}
		}

		@Override
		public TickType getType() {
			return TickType.CLIENT;
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return "Keybinds";
		}

	}

	@SideOnly(Side.CLIENT)
	public static class KeyPressEvent extends Event {

		public final int keyCode;
		public final KeyBinding key;
		public final boolean lastPressed;

		private KeyPressEvent(KeyBinding b) {
			key = b;
			keyCode = key.getKeyCode();
			lastPressed = instance.pressed.containsKey(key) && instance.pressed.get(b);
		}

	}
}
