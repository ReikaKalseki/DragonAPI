/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui.slot;

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
