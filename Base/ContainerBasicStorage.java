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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerBasicStorage extends CoreContainer {

	private IInventory lowerInv;

	public ContainerBasicStorage(EntityPlayer player, TileEntity te) {
		super(player, te);
		lowerInv = (IInventory)te;
		int numRows = lowerInv.getSizeInventory() / 9;
		lowerInv.openChest();
		int dy = (numRows - 4) * 18;

		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(lowerInv, j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + dy));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 161 + dy));
		}
	}

	/**
	 * Callback for when the crafting gui is closed.
	 */
	@Override
	public final void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
	{
		super.onCraftGuiClosed(par1EntityPlayer);
		lowerInv.closeChest();
	}

	public final IInventory getLowerInventory()
	{
		return lowerInv;
	}

}
