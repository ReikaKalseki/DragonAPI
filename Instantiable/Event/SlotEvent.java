/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class SlotEvent extends Event {

	public final int slotID;
	public final IInventory inventory;

	public SlotEvent(int id, IInventory ii) {
		slotID = id;
		inventory = ii;
	}

	public static class AddToSlotEvent extends SlotEvent {

		private final ItemStack added;

		public AddToSlotEvent(int id, IInventory ii, ItemStack is) {
			super(id, ii);
			added = is;
		}

		public final ItemStack getItem() {
			return added != null ? added.copy() : added;
		}

	}

	public static class RemoveFromSlotEvent extends SlotEvent {

		private final ItemStack removed;
		public final EntityPlayer player;

		public RemoveFromSlotEvent(int id, IInventory ii, ItemStack is, EntityPlayer ep) {
			super(id, ii);
			removed = is;
			player = ep;
		}

		public final ItemStack getItem() {
			return removed != null ? removed.copy() : removed;
		}

	}
}
