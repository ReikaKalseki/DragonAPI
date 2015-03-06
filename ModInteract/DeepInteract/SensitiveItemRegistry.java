package Reika.DragonAPI.ModInteract.DeepInteract;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModList;

import com.creativemd.craftingmanager.api.utils.sorting.items.BlockSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemStackSorting;

/** Register progression/balance-sensitive items here to blacklist normal recipe systems from adding new recipes for them. */
public class SensitiveItemRegistry {

	public static void registerItem(Block item) {
		if (MTInteractionManager.isMTLoaded()) {
			MTInteractionManager.instance.blacklistNewRecipesFor(item);
		}
		if (ModList.CRAFTMANAGER.isLoaded()) {
			CraftingManagerBlacklisting.registerItem(new BlockSorting(item));
		}
	}

	public static void registerItem(Item item) {
		if (MTInteractionManager.isMTLoaded()) {
			MTInteractionManager.instance.blacklistNewRecipesFor(item);
		}
		if (ModList.CRAFTMANAGER.isLoaded()) {
			CraftingManagerBlacklisting.registerItem(new ItemSorting(item));
		}
	}

	public static void registerItem(ItemStack item) {
		if (MTInteractionManager.isMTLoaded()) {
			MTInteractionManager.instance.blacklistNewRecipesFor(item);
		}
		if (ModList.CRAFTMANAGER.isLoaded()) {
			CraftingManagerBlacklisting.registerItem(new ItemStackSorting(item));
		}
	}

}
