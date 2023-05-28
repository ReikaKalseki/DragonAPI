/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;


public class VillageTradeHandler implements IVillageTradeHandler {

	public static final VillageTradeHandler instance = new VillageTradeHandler();

	private static final String NBT_KEY = "tradeChanceCache";

	private final ArrayList<TradeHandler> handlers = new ArrayList();

	private VillageTradeHandler() {

	}

	public void addHandler(TradeHandler h) {
		handlers.add(h);
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager ev, MerchantRecipeList li, Random rand) {
		if (ev.buyingList == null)
			ev.buyingList = new MerchantRecipeList();
		for (TradeHandler h : handlers)
			h.manipulateTradesForVillager(ev, li, this);
	}

	public boolean withRandomChance(EntityVillager ev, double c, String key) {
		NBTTagCompound tag = ev.getEntityData().getCompoundTag(NBT_KEY);
		if (tag.getBoolean(key))
			return false;
		tag.setBoolean(key, true);
		ev.getEntityData().setTag(NBT_KEY, tag);
		return ReikaRandomHelper.doWithChance(c);
	}

	public static interface TradeHandler {

		public void manipulateTradesForVillager(EntityVillager ev, MerchantRecipeList li, VillageTradeHandler h);

	}

	public static abstract class SimpleTradeHandler implements TradeHandler {

		@Override
		public void manipulateTradesForVillager(EntityVillager ev, MerchantRecipeList li, VillageTradeHandler h) {
			for (TradeToAdd e : this.getTradesToAdd()) {
				if (h.withRandomChance(ev, e.chanceToAdd, e.recipeID) && (e.validityCheck == null || e.validityCheck.apply(ev)) && !this.hasMatchingTrade(ev.buyingList, e)) {
					try {
						ev.buyingList.add(e.recipeType.newInstance());
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		private boolean hasMatchingTrade(MerchantRecipeList li, TradeToAdd e) {
			for (Object r : li) {
				if (r != null && e.recipeType.isAssignableFrom(r.getClass()))
					return true;
			}
			return false;
		}

		protected abstract Collection<TradeToAdd> getTradesToAdd();

	}

	public static class TradeToAdd {

		/** Must have a no-arg constructor */
		public final Class<? extends MerchantRecipe> recipeType;
		public final double chanceToAdd;
		public final String recipeID;
		public Function<EntityVillager, Boolean> validityCheck;

		public TradeToAdd(Class<? extends MerchantRecipe> c, double ch, String id) {
			recipeID = id;
			recipeType = c;
			chanceToAdd = ch;
		}

		public TradeToAdd setVillagerType(int type) {
			validityCheck = e -> e.getProfession() == type;
			return this;
		}

		public TradeToAdd setVillagerType(int... types) {
			validityCheck = e -> new HashSet(ReikaJavaLibrary.makeIntListFromArray(types)).contains(e.getProfession());
			return this;
		}

	}

}
