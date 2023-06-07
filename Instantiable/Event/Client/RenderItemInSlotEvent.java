/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemInSlotEvent extends Event {

	private final ItemStack item;
	public final int slotIndex;
	public final int slotX;
	public final int slotY;
	private final GuiContainer gui;
	public final Slot slot;

	private static long currentHoveredRenderItem;

	public RenderItemInSlotEvent(GuiContainer c, Slot s) {
		item = s.getStack();
		slotIndex = s.getSlotIndex();
		slotX = s.xDisplayPosition;
		slotY = s.yDisplayPosition;
		gui = c;
		slot = s;
		if (item != null && this.isHovered())
			currentHoveredRenderItem = System.identityHashCode(item);
	}

	protected RenderItemInSlotEvent(GuiContainer c, ItemStack is, int x, int y) {
		item = is;
		slotIndex = -1;
		slotX = x;
		slotY = y;
		gui = c;
		slot = null;
	}

	public ItemStack getItem() {
		return item != null ? item.copy() : null;
	}

	public boolean hasItem() {
		return item != null;
	}

	public static boolean isRenderingStackHovered(ItemStack is) {
		return is != null && System.identityHashCode(is) == currentHoveredRenderItem;
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

	public static void firePre(GuiContainer gc, Slot s) {
		MinecraftForge.EVENT_BUS.post(new Pre(gc, s));
	}

	public static ItemStack fireMid(ItemStack is, GuiContainer gc, Slot s) {
		Mid mid = new Mid(gc, s, is);
		MinecraftForge.EVENT_BUS.post(mid);
		return mid.itemToRender;
	}

	public static void firePost(GuiContainer gc, Slot s) {
		MinecraftForge.EVENT_BUS.post(new Post(gc, s));
	}

	public static class Pre extends RenderItemInSlotEvent {

		public Pre(GuiContainer c, Slot s) {
			super(c, s);
		}

	}

	public static class Mid extends RenderItemInSlotEvent {

		public ItemStack itemToRender;

		public Mid(GuiContainer c, Slot s, ItemStack is) {
			super(c, s);
			itemToRender = is;
		}

	}

	public static class Post extends RenderItemInSlotEvent {

		public Post(GuiContainer c, Slot s) {
			super(c, s);
			currentHoveredRenderItem = 0;
		}

	}

}
