/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.ImmutableArray;

import com.creativemd.craftingmanager.api.utils.sorting.items.BlockSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemStackSorting;

/** Register progression/balance-sensitive items here to blacklist normal recipe systems from adding new recipes for them. */
public final class SensitiveItemRegistry {

	public static final SensitiveItemRegistry instance = new SensitiveItemRegistry();

	private SensitiveItemRegistry() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void registerItem(Block item) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}
	}

	public void registerItem(Item item) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}
	}

	public void registerItem(ItemStack item) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}
	}

	private static enum Interactions {
		MINETWEAKER(MTInteractionManager.isMTLoaded()),
		CRAFTMANAGER(ModList.CRAFTMANAGER.isLoaded());

		private final boolean isLoaded;

		private static final ImmutableArray<Interactions> list = new ImmutableArray(values());

		private Interactions(boolean b) {
			isLoaded = b;
		}

		private void blacklist(Block item) {
			switch(this) {
			case MINETWEAKER:
				MTInteractionManager.instance.blacklistNewRecipesFor(item);
				break;
			case CRAFTMANAGER:
				CraftingManagerBlacklisting.registerItem(new BlockSorting(item));
				break;
			}
		}

		private void blacklist(Item item) {
			switch(this) {
			case MINETWEAKER:
				MTInteractionManager.instance.blacklistNewRecipesFor(item);
				break;
			case CRAFTMANAGER:
				CraftingManagerBlacklisting.registerItem(new ItemSorting(item));
				break;
			}
		}

		private void blacklist(ItemStack item) {
			switch(this) {
			case MINETWEAKER:
				MTInteractionManager.instance.blacklistNewRecipesFor(item);
				break;
			case CRAFTMANAGER:
				CraftingManagerBlacklisting.registerItem(new ItemStackSorting(item));
				break;
			}
		}
	}

}
