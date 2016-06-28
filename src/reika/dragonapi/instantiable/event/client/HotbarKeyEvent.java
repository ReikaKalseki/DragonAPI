/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class HotbarKeyEvent extends Event {

	public final GuiContainer gui;

	public final int hotbarSlot;
	public final int slotNumber;

	public final int keyCode;

	public HotbarKeyEvent(GuiContainer g, Slot s, int idx, int key) {
		gui = g;
		hotbarSlot = idx;
		slotNumber = s.slotNumber;
		keyCode = key;
	}

	public ItemStack getItem() {
		return Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(hotbarSlot);
	}

	public static boolean fire(GuiContainer gui, Slot s, int idx, int key) {
		return key == Minecraft.getMinecraft().gameSettings.keyBindsHotbar[idx].getKeyCode() && !MinecraftForge.EVENT_BUS.post(new HotbarKeyEvent(gui, s, idx, key));
	}

}
