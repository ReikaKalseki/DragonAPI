/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class VariableMaterialRecipe {

	public VariableMaterialRecipe(ItemStack out, Object... in) {

	}

	private class PreferentialItemStack {

		private LinkedList<String> oreNames = new LinkedList();
		private final ItemStack fallbackItem;

		public PreferentialItemStack(ItemStack backup, String... items) {
			if (backup == null || backup.getItem() == null)
				throw new IllegalArgumentException("Invalid (nonexistent) fallback item!");
			fallbackItem = backup;
			for (int i = 0; i < items.length; i++) {
				oreNames.add(items[i]);
			}
		}

		public ItemStack getItem() {
			for (int i = 0; i < oreNames.size(); i++) {
				String ore = oreNames.get(i);
				ArrayList<ItemStack> li = OreDictionary.getOres(ore);
			}
			return null;
		}
	}

}
