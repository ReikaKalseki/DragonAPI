/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import Reika.DragonAPI.Interfaces.XPProducer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class CoreContainer extends Container {

	protected final TileEntity tile;
	int posX; int posY; int posZ;
	protected EntityPlayer ep;

	protected ItemStack[] oldInv;

	protected final IInventory ii;

	public CoreContainer(EntityPlayer player, TileEntity te)
	{
		tile = te;
		posX = tile.xCoord;
		posY = tile.yCoord;
		posZ = tile.zCoord;
		ep = player;
		//this.detectAndSendChanges();

		if (te instanceof IInventory)
			ii = (IInventory)te;
		else
			ii = null;
	}

	public boolean hasInventoryChanged(ItemStack[] inv) {
		for (int i = 0; i < oldInv.length; i++)
			if (!ItemStack.areItemStacksEqual(oldInv[i], inv[i]))
				return true;
		return false;
	}

	protected void updateInventory(ItemStack[] inv) {
		for (int i = 0; i < oldInv.length; i++)
			oldInv[i] = inv[i];
	}

	protected void addPlayerInventoryWithOffset(EntityPlayer player, int dx, int dy) {
		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, dx+8 + k * 18, dy+84 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			this.addSlotToContainer(new Slot(player.inventory, j, dx+8 + j * 18, dy+142));
		}
	}

	protected void addPlayerInventory(EntityPlayer player) {
		this.addPlayerInventoryWithOffset(player, 0, 0);
	}


	/**
	 * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
	 */
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.isStandard8mReach(player);
	}

	public final boolean isStandard8mReach(EntityPlayer player) {
		double dist = ReikaMathLibrary.py3d(tile.xCoord+0.5-player.posX, tile.yCoord+0.5-player.posY, tile.zCoord+0.5-player.posZ);
		return (dist <= 8);
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack is = null;
		Slot fromSlot = (Slot)inventorySlots.get(slot);
		if (!(tile instanceof IInventory))
			return null;
		int invsize = ((IInventory)tile).getSizeInventory();

		if (fromSlot != null && fromSlot.getHasStack())
		{
			ItemStack inslot = fromSlot.getStack();
			is = inslot.copy();
			boolean toPlayer = slot < invsize;
			if (toPlayer) {
				for (int i = invsize; i < inventorySlots.size() && is.stackSize > 0; i++) {
					Slot toSlot = (Slot)inventorySlots.get(i);
					if (toSlot.isItemValid(is) && this.canAdd(is, toSlot.getStack())) {
						if (!toSlot.getHasStack()) {
							toSlot.putStack(is.copy());
							is.stackSize = 0;
						}
						else {
							ItemStack inToSlot = toSlot.getStack();
							int add = inToSlot.getMaxStackSize()-inToSlot.stackSize;
							if (add > is.stackSize)
								add = is.stackSize;
							toSlot.putStack(new ItemStack(is.itemID, inToSlot.stackSize+add, is.getItemDamage()));
							is.stackSize -= add;
						}
						if (tile instanceof XPProducer) {
							((XPProducer)tile).addXPToPlayer(player);
							((XPProducer)tile).clearXP();
						}
					}
				}
				if (is.stackSize <= 0) {
					fromSlot.putStack(null);
				}
				else {
					fromSlot.putStack(is.copy());
				}
				is = null;
				return is;
			}
			else {
				for (int i = 0; i < invsize && is.stackSize > 0; i++) {
					Slot toSlot = (Slot)inventorySlots.get(i);
					int lim = ((IInventory)tile).getInventoryStackLimit();
					if (toSlot.isItemValid(is) && (((IInventory)tile).isItemValidForSlot(i, is)) && this.canAdd(is, toSlot.getStack())) {
						if (!toSlot.getHasStack()) {
							if (is.stackSize <= lim) {
								toSlot.putStack(is.copy());
								is.stackSize = 0;
							}
							else {
								toSlot.putStack(new ItemStack(is.itemID, lim, is.getItemDamage()));
								is.stackSize -= lim;
							}
						}
						else {
							ItemStack inToSlot = toSlot.getStack();
							int add = Math.min(inToSlot.getMaxStackSize()-inToSlot.stackSize, lim);
							if (add > is.stackSize)
								add = is.stackSize;
							toSlot.putStack(new ItemStack(is.itemID, inToSlot.stackSize+add, is.getItemDamage()));
							is.stackSize -= add;
						}
					}
				}
				if (is.stackSize <= 0) {
					fromSlot.putStack(null);
				}
				else {
					fromSlot.putStack(is.copy());
				}
				is = null;
				return is;
			}
		}

		return null;
	}

	private boolean canAdd(ItemStack is, ItemStack inslot) {
		if (inslot == null)
			return true;
		return ReikaItemHelper.canCombineStacks(is, inslot);
	}

	@Override //To avoid a couple crashes with some mods not checking array bounds
	public Slot getSlot(int index)
	{
		if (index >= inventorySlots.size() || index < 0) {
			Object o = "A mod tried to access an invalid slot "+index+" for "+this;
			ReikaJavaLibrary.pConsole(o);
			ReikaChatHelper.write(o);
			Thread.dumpStack();
			return new Slot(new TileEntityChest(), index, -20, -20); //create new slot off screen; hacky fix, but should work
		}
		return (Slot)inventorySlots.get(index);
	}

}
