/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Libraries.ReikaInventoryHelper;


public class RelayInventory implements IInventory {

	public final int size;
	public ItemStack[] items;

	private final HashSet<Integer> filledSlots = new HashSet();

	public RelayInventory(int size) {
		this.size = size;
		items = new ItemStack[size];
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return items[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return ReikaInventoryHelper.decrStackSize(this, slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		items[slot] = is;
		if (is == null) {
			filledSlots.remove(slot);
		}
		else {
			filledSlots.add(slot);
		}
	}

	@Override
	public String getInventoryName() {
		return "relay";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {
		return false;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}

	public Set<Integer> getFilledSlots() {
		return Collections.unmodifiableSet(filledSlots);
	}

	public Collection<ItemStack> getItems() {
		Collection<ItemStack> li = new ArrayList();
		for (int slot : filledSlots) {
			li.add(items[slot]);
		}
		return li;
	}

}
