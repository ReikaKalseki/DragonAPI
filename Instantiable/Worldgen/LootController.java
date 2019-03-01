/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaGenHelper;

public class LootController {

	private List<LootStack> items = new ArrayList();

	public void registerToWorldGen(DragonAPIMod mod, int tier) {
		if (tier <= 0) {
			mod.getModLogger().log("Skipping dungeon loot generation, as it has been disabled.");
			return;
		}
		for (LootStack ls : items) {
			String itemName = ls.toString();
			if (ls.lootTier <= tier) {
				ReikaGenHelper.addChestLoot(ls.chestLocation.tag, ls.item, ls.minSize, ls.maxSize, ls.weight);
				mod.getModLogger().log("Adding "+itemName+" to "+ls.chestLocation);
			}
			else {
				mod.getModLogger().log("Not adding "+itemName+" to "+ls.chestLocation+", as its generation tier ("+ls.lootTier+") is greater than the configured tier of "+tier+".");
			}
		}
	}

	public void addItem(int tier, Location loc, ItemStack is, int min, int max, int chance) {
		LootStack ls = new LootStack(tier, loc, is, min, max, chance);
		if (!items.contains(ls))
			items.add(ls);
	}

	public void addItem(int tier, Location loc, ItemStack is, int chance) {
		this.addItem(tier, loc, is, 1, 1, chance);
	}

	public void addItems(int tier, Location loc, List<ItemStack> li, int chance) {
		for (int i = 0; i < li.size(); i++) {
			this.addItem(tier, loc, li.get(i), chance);
		}
	}

	@Override
	public String toString() {
		return items.toString();
	}

	class LootStack {
		public final int lootTier;
		public final Location chestLocation;
		private final ItemStack item;
		public final int minSize;
		public final int maxSize;
		public final int weight;

		public LootStack(int tier, Location loc, ItemStack is, int min, int max, int chance) {
			lootTier = tier;
			chestLocation = loc;
			item = is;
			minSize = min;
			maxSize = max;
			weight = chance;
		}

		public ItemStack getItemStack() {
			return item.copy();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof LootStack) {
				LootStack ls = (LootStack)o;
				return ls.chestLocation == chestLocation && ReikaItemHelper.matchStacks(item, ls.item);
			}
			return false;
		}

		@Override
		public String toString() {
			if (maxSize == minSize)
				return item.getDisplayName()+" with size "+minSize;
			else
				return item.getDisplayName()+" with size "+minSize+"-"+maxSize;
		}
	}

	public enum Location {
		BONUS(ChestGenHooks.BONUS_CHEST),
		VILLAGE(ChestGenHooks.VILLAGE_BLACKSMITH),
		DUNGEON(ChestGenHooks.DUNGEON_CHEST),
		MINESHAFT(ChestGenHooks.MINESHAFT_CORRIDOR),
		STRONGHOLD_LIBRARY(ChestGenHooks.STRONGHOLD_LIBRARY),
		STRONGHOLD_CROSSING(ChestGenHooks.STRONGHOLD_CROSSING),
		STRONGHOLD_HALLWAY(ChestGenHooks.STRONGHOLD_CORRIDOR),
		PYRAMID(ChestGenHooks.PYRAMID_DESERT_CHEST),
		JUNGLE_DISPENSER(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER),
		JUNGLE_PUZZLE(ChestGenHooks.PYRAMID_JUNGLE_CHEST);

		public final String tag;

		private Location(String sg) {
			tag = sg;
		}
	}

}
