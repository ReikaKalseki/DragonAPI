/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayMap;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;
import Reika.DragonAPI.Instantiable.Data.Immutable.ImmutableArray;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import matteroverdrive.api.MOApi;
import moze_intel.projecte.api.event.EMCRemapEvent;

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
					MOApi.instance().getMatterRegistry().addToBlacklist(item);
					break;
				case EE:
					ReikaEEHelper.blacklistBlock(item);
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
					MOApi.instance().getMatterRegistry().addToBlacklist(item);
					break;
				case EE:
					ReikaEEHelper.blacklistItem(item);
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
					MOApi.instance().getMatterRegistry().addToBlacklist(item);
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
