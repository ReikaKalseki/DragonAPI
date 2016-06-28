/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.deepinteract;

import java.util.HashSet;

import moze_intel.projecte.api.event.EMCRemapEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.ModList;
import reika.dragonapi.asm.DependentMethodStripper.ModDependent;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.data.KeyedItemStack;
import reika.dragonapi.instantiable.data.collections.OneWayCollections.OneWayMap;
import reika.dragonapi.instantiable.data.collections.OneWayCollections.OneWaySet;
import reika.dragonapi.instantiable.data.immutable.ImmutableArray;
import reika.dragonapi.instantiable.event.AddRecipeEvent;
import reika.dragonapi.mod.interact.ReikaEEHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Register progression/balance-sensitive items here to blacklist normal recipe systems from adding new recipes for them. */
public final class SensitiveItemRegistry {

	public static final SensitiveItemRegistry instance = new SensitiveItemRegistry();

	private final OneWaySet<KeyedItemStack> keys = new OneWaySet();
	private final OneWaySet<KeyedItemStack> recipeDisallowed = new OneWaySet();
	private final OneWayMap<DragonAPIMod, OneWaySet<KeyedItemStack>> byMod = new OneWayMap();

	private SensitiveItemRegistry() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void registerItem(DragonAPIMod mod, Block item, boolean allowRecipes) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}

		this.onRegisterItem(mod, new KeyedItemStack(item), allowRecipes);
	}

	public void registerItem(DragonAPIMod mod, Item item, boolean allowRecipes) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}

		this.onRegisterItem(mod, new KeyedItemStack(item), allowRecipes);
	}

	public void registerItem(DragonAPIMod mod, ItemStack item, boolean allowRecipes) {
		for (int i = 0; i < Interactions.list.length; i++) {
			Interactions it = Interactions.list.get(i);
			if (it.isLoaded) {
				it.blacklist(item);
			}
		}

		this.onRegisterItem(mod, new KeyedItemStack(item), allowRecipes);
	}

	private void onRegisterItem(DragonAPIMod mod, KeyedItemStack item, boolean allowRecipes) {
		keys.add(item.setSimpleHash(true).lock());
		if (!allowRecipes)
			recipeDisallowed.add(item.setSimpleHash(true).lock());
		byMod.get(mod);
	}

	public HashSet<KeyedItemStack> getItemsForMod(DragonAPIMod mod) {
		return byMod.get(mod);
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

	@SubscribeEvent
	@ModDependent(ModList.PROJECTE)
	public void watchProjectE(EMCRemapEvent evt) {
		for (KeyedItemStack ks : keys) {
			ItemStack is = ks.getItemStack();
			ReikaEEHelper.blacklistItemStack(is);
		}
	}

	@SubscribeEvent
	public void preventDisallowedRecipes(AddRecipeEvent evt) {
		if (!evt.isVanillaPass) {
			this.removeDisallowedRecipe(evt);
		}
	}

	private void removeDisallowedRecipe(AddRecipeEvent evt) {
		ItemStack out = evt.recipe.getRecipeOutput();
		if (out != null && out.getItem() != null) {
			if (recipeDisallowed.contains(new KeyedItemStack(out).setSimpleHash(true))) {
				evt.setCanceled(true);
			}
		}
	}

}
