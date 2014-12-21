/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class DummyInventory implements IInventory {

	@Override
	public int getSizeInventory() {return 0;}
	@Override
	public ItemStack getStackInSlot(int slot) {return null;}
	@Override
	public ItemStack decrStackSize(int slot, int decr) {return null;}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {return null;}
	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {}
	@Override
	public String getInventoryName() {return "Dummy";}
	@Override
	public boolean hasCustomInventoryName() {return false;}
	@Override
	public int getInventoryStackLimit() {return 0;}
	@Override
	public void markDirty() {}
	@Override
	public boolean isUseableByPlayer(EntityPlayer ep) {return false;}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {return false;}

}
