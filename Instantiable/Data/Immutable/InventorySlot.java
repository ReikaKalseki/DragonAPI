/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class InventorySlot {

	public final IInventory inventory;
	public final int slot;

	public InventorySlot(int slot, IInventory inv) {
		inventory = inv;
		this.slot = slot;
	}

	public ItemStack getStack() {
		return inventory.getStackInSlot(slot);
	}

	public int getStackSize() {
		ItemStack is = this.getStack();
		return is != null ? is.stackSize : 0;
	}

	public int decrement(int amt) {
		ItemStack is = this.getStack();
		int ret = Math.min(amt, is.stackSize);
		is.stackSize -= amt;
		if (is.stackSize <= 0)
			inventory.setInventorySlotContents(slot, null);
		return ret;
	}

	public int increment(int amt) {
		ItemStack is = this.getStack();
		int max = Math.min(is.getMaxStackSize(), inventory.getInventoryStackLimit());
		int ret = Math.min(amt, max-is.stackSize);
		is.stackSize += ret;
		if (is.stackSize <= 0)
			inventory.setInventorySlotContents(slot, null);
		return ret;
	}

	public ItemStack setSlot(ItemStack is) {
		ItemStack prev = this.getStack();
		inventory.setInventorySlotContents(slot, is);
		return prev;
	}

	public boolean isEmpty() {
		return this.getStack() == null;
	}

	@Override
	public String toString() {
		return "Slot "+slot+" of "+inventory;
	}

	public Slot toSlot(int x, int y) {
		return new Slot(inventory, slot, x, y);
	}

}
