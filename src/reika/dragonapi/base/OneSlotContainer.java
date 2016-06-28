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
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class OneSlotContainer extends CoreContainer {

	private IInventory inv;

	public OneSlotContainer(EntityPlayer player, TileEntity te) {
		this(player, te, 0);
	}

	public OneSlotContainer(EntityPlayer player, TileEntity te, int offsetY) {
		super(player, te);
		ep = player;
		inv = (IInventory)te;
		this.addSlotToContainer(new Slot(inv, 0, 80, 35));

		this.addPlayerInventoryWithOffset(player, 0, offsetY);
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
	public void updateProgressBar(int par1, int par2)
	{
		switch(par1) {

		}
	}
}
