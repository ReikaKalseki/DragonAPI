package Reika.DragonAPI.Instantiable;

import java.util.Comparator;

import net.minecraft.item.ItemStack;

public class AlphabeticItemComparator implements Comparator<ItemStack> {

	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		return String.CASE_INSENSITIVE_ORDER.compare(o1.getDisplayName(), o2.getDisplayName());
	}

}
