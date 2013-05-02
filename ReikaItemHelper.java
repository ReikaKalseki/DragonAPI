package Reika.DragonAPI;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ReikaItemHelper {
	
	private ReikaItemHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	/** Returns true if the block or item has metadata variants. Args: ID */
	public static boolean hasMetadata(int id) {
		if (id > 255)
			return Item.itemsList[id].getHasSubtypes();
		else {
			return Item.itemsList[id-256].getHasSubtypes();
		}
	}
	
	/** Like .equals for comparing ItemStacks, but does not care about size.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b */
	public static boolean matchStacks(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return (a.itemID == b.itemID && a.getItemDamage() == b.getItemDamage());
	}
}
