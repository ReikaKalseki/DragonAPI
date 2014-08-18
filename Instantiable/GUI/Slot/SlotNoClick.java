/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI.Slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Identical to Slot but disallows item insertion. */
public class SlotNoClick extends Slot {

	public SlotNoClick(IInventory ii, int id, int x, int y) {
		super(ii, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack is)
	{
		return false;
	}

}
