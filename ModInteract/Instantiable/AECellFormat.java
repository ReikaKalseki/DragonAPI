/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Instantiable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AECellFormat implements IInventory {

	private ItemStack cell;
	private String tag;
	private NBTTagCompound tagCompound;
	private int size;
	private ItemStack[] slots;
	private boolean dirty = false;

	public AECellFormat(ItemStack is, String tag, int s) {
		cell = is;
		this.tag = tag;
		size = s;
		if (cell.stackTagCompound == null)
			cell.stackTagCompound = new NBTTagCompound();
		cell.stackTagCompound.setTag(tag, cell.stackTagCompound.getCompoundTag(tag));
		tagCompound = cell.stackTagCompound.getCompoundTag(tag);
		this.openInventory();
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slotId, ItemStack itemStack) {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return slots[slotId];
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack content) {
		ItemStack slotContent = slots[slotId];
		if (slotContent != content) {
			slots[slotId] = content;
			this.markDirty();
		}
	}

	@Override
	public void openInventory() {
		slots = new ItemStack[size];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = ItemStack.loadItemStackFromNBT(tagCompound.getCompoundTag("ItemStack#"+i));
		}
	}

	@Override
	public void closeInventory() {
		if (dirty) {
			for (int i = 0; i < slots.length; i++) {
				tagCompound.removeTag("ItemStack#"+i);
				ItemStack content = slots[i];
				if (content != null) {
					tagCompound.setTag("ItemStack#"+i, new NBTTagCompound());
					content.writeToNBT(tagCompound.getCompoundTag("ItemStack#"+i));
				}
			}
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack in = slots[slot];
		if (in == null)
			return null;
		int size = in.stackSize;
		if (size <= 0)
			return null;
		int left;
		if (amt >= size) {
			left = size;
			slots[slot] = null;
		}
		else {
			slots[slot].stackSize -= amt;
			left = amt;
		}
		ItemStack ret = in.copy();
		ret.stackSize = amt;
		this.markDirty();

		return ret;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotId) {
		return this.getStackInSlot(slotId);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void markDirty() {
		dirty = true;
		this.closeInventory();
		dirty = false;
	}
}
