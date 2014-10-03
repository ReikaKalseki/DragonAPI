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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import Reika.DragonAPI.APIPacketHandler;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KeyWatcher {

	public static final KeyWatcher instance = new KeyWatcher();

	private final EnumMap<Key, KeyState> keyStates = new EnumMap(Key.class);

	private KeyWatcher() {
		for (int i = 0; i < Key.keyList.length; i++) {
			keyStates.put(Key.keyList[i], new KeyState());
		}
	}

	public boolean isKeyDown(EntityPlayer ep, Key key) {
		return keyStates.get(key).getKeyState(ep);
	}

	public void setKey(EntityPlayer ep, Key key, boolean press) {
		keyStates.get(key).updateKey(ep, press);
	}

	public static enum Key {
		JUMP(),
		SNEAK(),
		FOWARD(),
		BACK(),
		LEFT(),
		RIGHT(),
		INVENTORY(),
		DROPITEM(),
		ATTACK(),
		USE(),
		CHAT(),
		LCTRL;

		public static final Key[] keyList = values();
	}

	@SideOnly(Side.CLIENT)
	private static enum Keys {
		JUMP(Minecraft.getMinecraft().gameSettings.keyBindJump),
		SNEAK(Minecraft.getMinecraft().gameSettings.keyBindSneak),
		FOWARD(Minecraft.getMinecraft().gameSettings.keyBindForward),
		BACK(Minecraft.getMinecraft().gameSettings.keyBindBack),
		LEFT(Minecraft.getMinecraft().gameSettings.keyBindLeft),
		RIGHT(Minecraft.getMinecraft().gameSettings.keyBindRight),
		INVENTORY(Minecraft.getMinecraft().gameSettings.keyBindInventory),
		DROPITEM(Minecraft.getMinecraft().gameSettings.keyBindDrop),
		ATTACK(Minecraft.getMinecraft().gameSettings.keyBindAttack),
		USE(Minecraft.getMinecraft().gameSettings.keyBindUseItem),
		CHAT(Minecraft.getMinecraft().gameSettings.keyBindChat),
		LCTRL(Keyboard.KEY_LCONTROL);

		private KeyBinding key;
		private int keyInt;

		public static final Keys[] keyList = values();

		private Keys(KeyBinding key) {
			this.key = key;
		}

		private Keys(int key) {
			keyInt = key;
		}

		public boolean pollKey() {
			return key != null ? key.getIsKeyPressed() : Keyboard.isKeyDown(keyInt);
		}

		public int keyID() {
			return key.getKeyCode();
		}

		public Key getServerKey() {
			return Key.keyList[this.ordinal()];
		}

		private void sendPacket() {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream(8);
			DataOutputStream data = new DataOutputStream(bytes);
			try {
				data.writeInt(APIPacketHandler.PacketIDs.KEYUPDATE.ordinal());
				data.writeInt(this.ordinal());
				data.writeInt(this.pollKey() ? 1 : 0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			ReikaPacketHelper.sendRawPacket(DragonAPIInit.packetChannel, bytes);
		}
	}

	private static class KeyState {

		private final HashMap<EntityPlayer, Boolean> data = new HashMap();

		public boolean getKeyState(EntityPlayer ep) {
			return data.containsKey(ep) && data.get(ep);
		}

		public void updateKey(EntityPlayer ep, boolean key) {
			data.put(ep, key);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class KeyTicker implements TickHandler {

		public static final KeyTicker instance = new KeyTicker();
		private final EnumMap<Keys, Boolean> keyStates = new EnumMap(Keys.class);

		private KeyTicker() {

		}

		@Override
		public void tick(Object... tickData) {

			for (int i = 0; i < Keys.keyList.length; i++) {
				Keys key = Keys.keyList[i];
				boolean wasPressed = keyStates.containsKey(key) && keyStates.get(key);
				boolean isPressed = key.pollKey();
				if (wasPressed != isPressed) {
					keyStates.put(key, isPressed);
					key.sendPacket();
					KeyWatcher.instance.setKey(Minecraft.getMinecraft().thePlayer, key.getServerKey(), isPressed);
				}
			}

		}

		@Override
		public TickType getType() {
			return TickType.CLIENT;
		}

		@Override
		public String getLabel() {
			return "KeyWatcher";
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

	}

}
