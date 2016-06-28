/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumMap;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import reika.dragonapi.APIPacketHandler;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.auxiliary.trackers.TickRegistry.TickHandler;
import reika.dragonapi.auxiliary.trackers.TickRegistry.TickType;
import reika.dragonapi.instantiable.data.maps.PlayerMap;
import reika.dragonapi.instantiable.event.RawKeyPressEvent;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
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
		LCTRL(),
		PGUP(),
		PGDN(),
		TAB(),
		TILDE(),
		BACKSPACE(),
		HOME(),
		END(),
		INSERT(),
		DELETE();

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
		LCTRL(Minecraft.isRunningOnMac ? Keyboard.KEY_LMETA : Keyboard.KEY_LCONTROL),
		PGUP(Keyboard.KEY_PRIOR),
		PGDN(Keyboard.KEY_NEXT),
		TAB(Keyboard.KEY_TAB),
		TILDE(Keyboard.KEY_GRAVE), //Not on Euro keyboards
		BACKSPACE(Keyboard.KEY_BACK),
		HOME(Keyboard.KEY_HOME),
		END(Keyboard.KEY_END),
		INSERT(Keyboard.KEY_INSERT),
		DELETE(Keyboard.KEY_DELETE);

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
			boolean flag = false;
			try {
				data.writeInt(APIPacketHandler.PacketIDs.KEYUPDATE.ordinal());
				data.writeInt(this.ordinal());
				data.writeInt(this.pollKey() ? 1 : 0);
				flag = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			if (flag)
				ReikaPacketHelper.sendRawPacket(DragonAPIInit.packetChannel, bytes);
			else
				DragonAPICore.log("Could not send key "+this+" packet, as it was malformed.");
		}
	}

	private static class KeyState {

		private final PlayerMap<Boolean> data = new PlayerMap();

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
		public void tick(TickType type, Object... tickData) {

			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			if (ep != null) {
				for (int i = 0; i < Keys.keyList.length; i++) {
					Keys key = Keys.keyList[i];
					boolean wasPressed = keyStates.containsKey(key) && keyStates.get(key);
					boolean isPressed = key.pollKey();
					if (wasPressed != isPressed) {
						keyStates.put(key, isPressed);
						key.sendPacket();
						KeyWatcher.instance.setKey(ep, key.getServerKey(), isPressed);
						MinecraftForge.EVENT_BUS.post(new RawKeyPressEvent(key.getServerKey(), ep));
					}
				}
			}

		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.CLIENT);
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
