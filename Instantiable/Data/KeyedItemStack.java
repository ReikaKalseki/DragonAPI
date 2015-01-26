/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class KeyedItemStack {

	private final ItemStack item;
	private boolean sized = false;

	public KeyedItemStack(ItemStack is) {
		if (is == null || is.getItem() == null)
			throw new MisuseException("You cannot key a null itemstack!");
		item = is.copy();
	}

	public KeyedItemStack setSized() {
		sized = true;
		return this;
	}

	@Override
	public final int hashCode() {
		return Item.getIdFromItem(item.getItem())+(item.getItemDamage() << 16)+(item.stackTagCompound != null ? item.stackTagCompound.hashCode() : 0);
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof KeyedItemStack) {
			KeyedItemStack ks = (KeyedItemStack)o;
			if (ReikaItemHelper.matchStacks(item, ks.item) && ItemStack.areItemStackTagsEqual(item, ks.item)) {
				return !sized || item.stackSize == ks.item.stackSize;
			}
			return false;
		}
		return false;
	}

	public ItemStack getItemStack() {
		return item.copy();
	}

	@Override
	public String toString() {
		return item.toString();
	}

}
