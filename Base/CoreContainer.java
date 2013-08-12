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
import Reika.DragonAPI.Libraries.ReikaMathLibrary;

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

	protected void addPlayerInventory(EntityPlayer player) {
		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 142));
		}
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
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.isStandard8mReach(par1EntityPlayer);
	}

	public final boolean isStandard8mReach(EntityPlayer player) {
		double dist = ReikaMathLibrary.py3d(tile.xCoord+0.5-player.posX, tile.yCoord+0.5-player.posY, tile.zCoord+0.5-player.posZ);
		return (dist <= 8);
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack var3 = null;
		Slot var4 = (Slot)inventorySlots.get(par2);
		if (!(tile instanceof IInventory))
			return null;
		int invsize = ((IInventory)tile).getSizeInventory();

		if (var4 != null && var4.getHasStack())
		{
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if (par2 < invsize)
			{
				if (!this.mergeItemStack(var5, invsize, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(var5, 0, invsize, false))
			{
				return null;
			}

			if (var5.stackSize == 0)
			{
				var4.putStack((ItemStack)null);
			}
			else
			{
				var4.onSlotChanged();
			}
		}

		return var3;
	}

	@Override
	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4)
	{
		boolean flag1 = false;
		int k = par2;

		if (par4)
		{
			k = par3 - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (par1ItemStack.isStackable())
		{
			while (par1ItemStack.stackSize > 0 && (!par4 && k < par3 || par4 && k >= par2))
			{
				slot = (Slot)inventorySlots.get(k);
				itemstack1 = slot.getStack();

				//ReikaJavaLibrary.pConsole(par1ItemStack+" to "+slot+" ("+itemstack1+") - "+slot.isItemValid(par1ItemStack));

				if (ii.isStackValidForSlot(k, par1ItemStack) && slot.isItemValid(par1ItemStack) && itemstack1 != null && itemstack1.itemID == par1ItemStack.itemID && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1))
				{
					int l = itemstack1.stackSize + par1ItemStack.stackSize;

					if (l <= par1ItemStack.getMaxStackSize())
					{
						par1ItemStack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					}
					else if (itemstack1.stackSize < par1ItemStack.getMaxStackSize())
					{
						par1ItemStack.stackSize -= par1ItemStack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = par1ItemStack.getMaxStackSize();
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

		if (par1ItemStack.stackSize > 0)
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

				if (ii.isStackValidForSlot(k, par1ItemStack) && slot.isItemValid(par1ItemStack) && itemstack1 == null)
				{
					slot.putStack(par1ItemStack.copy());
					slot.onSlotChanged();
					par1ItemStack.stackSize = 0;
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
