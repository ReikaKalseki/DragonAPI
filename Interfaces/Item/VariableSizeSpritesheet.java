package Reika.DragonAPI.Interfaces.Item;

import net.minecraft.item.ItemStack;

public interface VariableSizeSpritesheet extends IndexedItemSprites {

	/** In 16x16 cells */
	public int getSpriteSize(ItemStack is);

}
