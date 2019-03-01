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

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public abstract class BasicInventory implements IInventory {

	public final int inventorySize;
	public final int stackLimit;
	public final String name;

	protected ItemStack[] inv;

	public BasicInventory(String n, int size) {
		this(n, size, 64);
	}

	public BasicInventory(String n, int size, int limit) {
		inventorySize = size;
		stackLimit = limit;
		inv = new ItemStack[size];
		name = n;
	}

	@Override
	public final int getSizeInventory() {
		return inventorySize;
	}

	@Override
	public final ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public final ItemStack decrStackSize(int slot, int decr) {
		return ReikaInventoryHelper.decrStackSize(this, slot, decr);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slot) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, slot);
	}

	@Override
	public final void setInventorySlotContents(int slot, ItemStack is) {
		inv[slot] = is;
	}

	@Override
	public final String getInventoryName() {
		return name;
	}

	@Override
	public final boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public final int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {return false;}

	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	public final ItemStack[] getItems() {
		return Arrays.copyOf(inv, inv.length);
	}

}
