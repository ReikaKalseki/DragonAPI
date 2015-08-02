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
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;
import Reika.DragonAPI.Instantiable.Data.Immutable.ImmutableArray;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import cpw.mods.fml.common.Loader;

/** Register progression/balance-sensitive items here to blacklist normal recipe systems from adding new recipes for them. */
public final class SensitiveItemRegistry {

	public static final SensitiveItemRegistry instance = new SensitiveItemRegistry();

	private final OneWaySet<KeyedItemStack> keys = new OneWaySet();

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

		keys.add(new KeyedItemStack(item).lock());
	}

	public void registerItem(Item item) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}

		keys.add(new KeyedItemStack(item).lock());
	}

	public void registerItem(ItemStack item) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}

		keys.add(new KeyedItemStack(item).setSimpleHash(true).lock());
	}

	private static enum Interactions {
		MINETWEAKER(MTInteractionManager.isMTLoaded()),
		CRAFTMANAGER(ModList.CRAFTMANAGER.isLoaded()),
		MATTEROVERDRIVE(Loader.isModLoaded("mo")),
		EE(true);

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
					CraftingManagerBlacklisting.registerItem(item);
					break;
				case MATTEROVERDRIVE:
					MatterOverdriveHandler.blacklist(item);
					break;
				case EE:
					ReikaEEHelper.blacklistItemStack(new ItemStack(item));
					break;
			}
		}

		private void blacklist(Item item) {
			switch(this) {
				case MINETWEAKER:
					MTInteractionManager.instance.blacklistNewRecipesFor(item);
					break;
				case CRAFTMANAGER:
					CraftingManagerBlacklisting.registerItem(item);
					break;
				case MATTEROVERDRIVE:
					MatterOverdriveHandler.blacklist(item);
					break;
				case EE:
					ReikaEEHelper.blacklistItemStack(new ItemStack(item));
					break;
			}
		}

		private void blacklist(ItemStack item) {
			switch(this) {
				case MINETWEAKER:
					MTInteractionManager.instance.blacklistNewRecipesFor(item);
					break;
				case CRAFTMANAGER:
					CraftingManagerBlacklisting.registerItem(item);
					break;
				case MATTEROVERDRIVE:
					MatterOverdriveHandler.blacklist(item);
					break;
				case EE:
					ReikaEEHelper.blacklistItemStack(item);
					break;
			}
		}
	}

	public boolean contains(ItemStack is) {
		return keys.contains(new KeyedItemStack(is).setSimpleHash(true));
	}

	public boolean contains(KeyedItemStack ks) {
		return keys.contains(ks.copy().setSimpleHash(true));
	}

}
