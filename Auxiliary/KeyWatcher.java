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
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import Reika.DragonAPI.APIPacketHandler;
import Reika.DragonAPI.DragonAPIInit;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
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
		CHAT();

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
		CHAT(Minecraft.getMinecraft().gameSettings.keyBindChat);

		private final KeyBinding key;

		public static final Keys[] keyList = values();

		private Keys(KeyBinding key) {
			this.key = key;
		}

		public boolean pollKey() {
			return key.pressed;
		}

		public int keyID() {
			return key.keyCode;
		}

		public Key getServerKey() {
			return Key.keyList[this.ordinal()];
		}

		private void sendPacket() {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream(8);
			DataOutputStream data = new DataOutputStream(bytes);
			try {
				data.writeInt(PacketTypes.RAW.ordinal());
				data.writeInt(APIPacketHandler.PacketIDs.KEYUPDATE.ordinal());
				data.writeInt(this.ordinal());
				data.writeInt(this.pollKey() ? 1 : 0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = DragonAPIInit.packetChannel;
			packet.data = bytes.toByteArray();
			packet.length = bytes.size();
			PacketDispatcher.sendPacketToServer(packet);
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
	public static class KeyTicker implements ITickHandler {

		public static final KeyTicker instance = new KeyTicker();
		private final EnumMap<Keys, Boolean> keyStates = new EnumMap(Keys.class);

		private KeyTicker() {

		}

		@Override
		public void tickStart(EnumSet<TickType> type, Object... tickData) {

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
		public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		}

		@Override
		public EnumSet<TickType> ticks() {
			return EnumSet.of(TickType.CLIENT);
		}

		@Override
		public String getLabel() {
			return "KeyWatcher";
		}

	}

}
