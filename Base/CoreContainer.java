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
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

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
		Slot sl = (Slot)inventorySlots.get(slot);
		if (!(tile instanceof IInventory))
			return null;
		int invsize = ((IInventory)tile).getSizeInventory();

		if (sl != null && sl.getHasStack())
		{
			ItemStack inslot = sl.getStack();
			is = inslot.copy();

			if (slot < invsize)
			{
				if (!this.mergeItemStack(inslot, invsize, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(inslot, 0, invsize, false))
			{
				return null;
			}

			if (inslot.stackSize == 0)
			{
				sl.putStack((ItemStack)null);
			}
			else
			{
				sl.onSlotChanged();
			}
		}

		return is;
	}

	@Override
	protected boolean mergeItemStack(ItemStack is, int par2, int par3, boolean par4)
	{
		boolean flag1 = false;
		int k = par2;

		if (par4)
		{
			k = par3 - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (is.isStackable())
		{
			while (is.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2))
			{
				slot = (Slot)inventorySlots.get(k);
				itemstack1 = slot.getStack();

				//ReikaJavaLibrary.pConsole(is+" to "+slot+" ("+itemstack1+") - "+slot.isItemValid(is));

				if (ii.isStackValidForSlot(k, is) && slot.isItemValid(is) && itemstack1 != null && itemstack1.itemID == is.itemID && (!is.getHasSubtypes() || is.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(is, itemstack1))
				{
					int l = itemstack1.stackSize + is.stackSize;

					if (l <= is.getMaxStackSize())
					{
						is.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					}
					else if (itemstack1.stackSize < is.getMaxStackSize())
					{
						is.stackSize -= is.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = is.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (par4)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		if (is.stackSize > 0)
		{
			if (par4)
			{
				k = par3 - 1;
			}
			else
			{
				k = par2;
			}

			while (!par4 && k < par3 || par4 && k >= par2)
			{
				slot = (Slot)inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (ii.isStackValidForSlot(k, is) && slot.isItemValid(is) && itemstack1 == null)
				{
					slot.putStack(is.copy());
					slot.onSlotChanged();
					is.stackSize = 0;
					flag1 = true;
					break;
				}

				if (par4)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		return flag1;
	}

}
