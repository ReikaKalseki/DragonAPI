package Reika.DragonAPI.Interfaces.Item;

import net.minecraft.item.ItemStack;


public interface EntityCapturingItem {

	public boolean hasEntity(ItemStack is);

	public String currentEntityName(ItemStack is);

}
