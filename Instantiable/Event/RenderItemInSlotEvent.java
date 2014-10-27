/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import cpw.mods.fml.common.eventhandler.Event;

public class RenderItemInSlotEvent extends Event {

	private final ItemStack item;
	public final int slotIndex;
	public final int slotX;
	public final int slotY;
	private final GuiContainer gui;
	private final Slot slot;

	public RenderItemInSlotEvent(GuiContainer c, Slot s) {
		item = s.getStack();
		slotIndex = s.getSlotIndex();
		slotX = s.xDisplayPosition;
		slotY = s.yDisplayPosition;
		gui = c;
		slot = s;
	}

	public ItemStack getItem() {
		return item != null ? item.copy() : null;
	}

	public boolean hasItem() {
		return item != null;
	}

	public boolean isHovered() {
		int i = Mouse.getX() * gui.width / gui.mc.displayWidth;
		int j = gui.height - Mouse.getY() * gui.height / gui.mc.displayHeight - 1;
		return gui.isMouseOverSlot(slot, i, j);
	}

	public int getRelativeMouseX() {
		return ReikaGuiAPI.instance.getMouseRealX()-slotX-gui.guiLeft;
	}

	public int getRelativeMouseY() {
		return ReikaGuiAPI.instance.getMouseRealY()-slotY-gui.guiTop;
	}

	public boolean guiInstanceOf(Class c) {
		return c.isAssignableFrom(gui.getClass());
	}

	public Class getGuiClass() {
		return gui.getClass();
	}

	public IInventory getSlotInventory() {
		return slot.inventory;
	}

}
