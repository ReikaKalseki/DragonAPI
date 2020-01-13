/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CachedInvSlot {

	public final IInventory inventory;
	public final int slotNumber;
	private final ItemStack item;

	public CachedInvSlot(int slot, IInventory inv) {
		inventory = inv;
		slotNumber = slot;
		ItemStack is = inv.getStackInSlot(slot);
		item = is != null ? is.copy() : null;
	}

	public ItemStack getStack() {
		return item != null ? item.copy() : null;
	}

	public boolean isEmpty() {
		return item == null;
	}

	@Override
	public String toString() {
		return "Slot "+slotNumber+" of "+inventory;
	}

	public static ArrayList<CachedInvSlot> getFromInventory(IInventory ii, boolean skipEmpty) {
		ArrayList<CachedInvSlot> ret = new ArrayList();
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (skipEmpty && ii.getStackInSlot(i) == null)
				continue;
			ret.add(new CachedInvSlot(i, ii));
		}
		return ret;
	}

}
