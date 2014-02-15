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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Instantiable.GUI.SlotFullStack;

public class ContainerStackingStorage extends ContainerBasicStorage {

	public ContainerStackingStorage(EntityPlayer player, TileEntity te) {
		super(player, te);
	}

	@Override
	protected Slot getSlotOfType(IInventory ii, int id, int x, int y) {
		return new SlotFullStack(ii, id, x, y);
	}

	@Override
	public ItemStack slotClick(int ID, int par2, int par3, EntityPlayer ep) {
		ItemStack held = ep.inventory.getItemStack();
		int prev = -1;
		if (held != null && ID < lowerInv.getSizeInventory()) {
			prev = held.getMaxStackSize();
			held.getItem().setMaxStackSize(lowerInv.getInventoryStackLimit());
		}
		ItemStack ret = super.slotClick(ID, par2, par3, ep);
		if (prev > 0) {
			held.getItem().setMaxStackSize(prev);
		}
		return ret;
	}

}
