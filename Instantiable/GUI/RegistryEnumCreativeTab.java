/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.util.Comparator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.Registry.BlockEnum;
import Reika.DragonAPI.Interfaces.Registry.RegistryEntry;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;

public abstract class RegistryEnumCreativeTab extends SortedCreativeTab {

	public RegistryEnumCreativeTab(String name) {
		super(name);
	}

	private static final EnumItemSorter sorter = new EnumItemSorter();

	private static final class EnumItemSorter implements Comparator<ItemStack> {

		@Override
		public int compare(ItemStack is1, ItemStack is2) {
			Item i1 = is1.getItem();
			Item i2 = is2.getItem();
			RegistryEntry r1 = ReikaRegistryHelper.getRegistryForObject(i1);
			RegistryEntry r2 = ReikaRegistryHelper.getRegistryForObject(i2);
			if (r1 != null && r2 != null) {
				int d1 = r1.ordinal();
				int d2 = r2.ordinal();
				if (r1 instanceof BlockEnum)
					d1 -= 1000;
				if (r2 instanceof BlockEnum)
					d2 -= 1000;
				return d1 - d2;
			}
			if (r1 != null) {
				return Integer.MAX_VALUE;
			}
			if (r2 != null) {
				return Integer.MIN_VALUE;
			}
			return 0;
		}
	}

	@Override
	protected final Comparator<ItemStack> getComparator() {
		return sorter;
	}
}
