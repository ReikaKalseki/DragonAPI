/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class ContainerBasicStorage extends CoreContainer {

	protected IInventory lowerInv;

	public ContainerBasicStorage(EntityPlayer player, TileEntity te) {
		super(player, te);
		lowerInv = (IInventory)te;
		int numRows = lowerInv.getSizeInventory() / 9;
		lowerInv.openInventory();
		int dy = (numRows - 4) * 18;

		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(this.getSlotOfType(lowerInv, j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(this.getSlotOfType(player.inventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + dy));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(this.getSlotOfType(player.inventory, i, 8 + i * 18, 161 + dy));
		}
	}

	protected Slot getSlotOfType(IInventory ii, int id, int x, int y) {
		return new Slot(ii, id, x, y);
	}

	/**
	 * Callback for when the crafting gui is closed.
	 */
	@Override
	public final void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		lowerInv.closeInventory();
	}

	public final IInventory getLowerInventory()
	{
		return lowerInv;
	}

}
