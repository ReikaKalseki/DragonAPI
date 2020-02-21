package Reika.DragonAPI.Interfaces.Item;

import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Instantiable.ItemFilter;

public interface CustomMatchingItem {

	public boolean match(ItemStack is);

	public ItemFilter getFilter(ItemStack is);

}
