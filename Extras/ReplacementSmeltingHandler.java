/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

/** Because apparently Notch is retarded, his original code DEFINITELY is retarded, and everyone is sick of wasting a huge amount
 * of tick time on every smelter iterating over 4000 recipes three times a tick EACH. */
public class ReplacementSmeltingHandler {

	private static HashMap<KeyedItemStack, FurnaceRecipe> smeltingList = new HashMap();
	private static HashMap<KeyedItemStack, Float> outputToExperienceMap = new HashMap();
	private static Collection<FurnaceRecipe> vanillaRecipes = new ArrayList(); //for the rebuild function

	private static FurnaceRecipes loadingInstance;
	private static boolean isInitialized;
	private static boolean isCompiled;

	public static void onSmeltingInit(FurnaceRecipes obj) {
		if (isInitialized)
			return;

		loadingInstance = obj;

		Map<ItemStack, ItemStack> map = getInstance().getSmeltingList();
		for (Entry<ItemStack, ItemStack> e : map.entrySet()) {
			ItemStack in = e.getKey();
			ItemStack out = e.getValue();
			FurnaceRecipe r = new FurnaceRecipe(in, out, getInstance().func_151398_b(out));
			vanillaRecipes.add(r);
		}

		loadingInstance = null;
		isInitialized = true;
	}

	private static FurnaceRecipes getInstance() {
		return isInitialized ? FurnaceRecipes.smelting() : loadingInstance;
	}

	public void applyMinetweakerChanges() {

	}

	public static void build() {
		if (isCompiled)
			return;

		Map<ItemStack, ItemStack> map = getInstance().getSmeltingList();
		for (Entry<ItemStack, ItemStack> e : map.entrySet()) {
			ItemStack in = e.getKey();
			ItemStack out = e.getValue();
			AddSmeltingEvent.isVanillaPass = vanillaRecipes.contains(new FurnaceRecipe(in, out, 0));
			addRecipe(in, out, getInstance().func_151398_b(out));
		}
		AddSmeltingEvent.isVanillaPass = false;

		isCompiled = true;
	}

	private static void addRecipe(ItemStack in, ItemStack out, float xp) {
		//ReikaJavaLibrary.pConsole("Registering smelting of "+in+" to "+out+" giving "+xp+" XP; is vanilla = "+AddSmeltingEvent.isVanillaPass);
		AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
		MinecraftForge.EVENT_BUS.post(evt);
		if (!evt.isCanceled()) {
			FurnaceRecipe f = new FurnaceRecipe(in, out, evt.experienceValue);
			smeltingList.put(createKey(in), f);
			if (xp > 0)
				outputToExperienceMap.put(createKey(out), Math.max(getSmeltingXPByOutput(out), xp));
		}
	}

	public static void fireEventsForVanillaRecipes() {
		AddSmeltingEvent.isVanillaPass = true;
		HashMap<ItemStack, Object[]> map = new HashMap();
		for (FurnaceRecipe r : vanillaRecipes) {
			AddSmeltingEvent evt = new AddSmeltingEvent(r.getInput(), r.getOutput(), r.getExperience());
			MinecraftForge.EVENT_BUS.post(evt);
			if (evt.isCanceled()) {
				removeRecipe(r.getInput());
			}
			else if (r.getExperience() != evt.experienceValue) {
				r.experience = evt.experienceValue;
				modifyExperience(r.getInput(), evt.experienceValue);
			}
		}
		AddSmeltingEvent.isVanillaPass = false;
	}

	public static void removeRecipe(ItemStack in) {
		/*outputToExperienceMap.remove(*/smeltingList.remove(createKey(in))/*.output)*/;
	}

	private static void modifyExperience(ItemStack is, float newXP) {
		FurnaceRecipe rec = smeltingList.get(createKey(is));
		if (rec != null) {
			rec.experience = newXP;
		}
	}

	public static ItemStack getResult(ItemStack is) {
		if (!isCompiled) {
			return getInstance().getSmeltingResult(is);
		}
		if (is == null)
			throw new IllegalArgumentException("You cannot fetch the smelting recipes for null!");
		if (is.getItem() == null)
			throw new IllegalArgumentException("You cannot fetch the smelting recipes for a stack with a null item!");
		FurnaceRecipe rec = smeltingList.get(createKey(is));
		return rec != null ? rec.output.copy() : null;
	}

	public static float getSmeltingXPByOutput(ItemStack output) {
		if (!isCompiled) {
			return getInstance().func_151398_b(output);
		}
		Float rec = /*smeltingList.get(*/outputToExperienceMap.get(createKey(output))/*)*/;
		return rec != null ? rec.floatValue() : 0;
	}

	public static Map getList() {
		if (!isCompiled) {
			return getInstance().getSmeltingList();
		}
		return buildGettableList();
	}

	private static Map buildGettableList() {
		HashMap map = new HashMap();
		for (FurnaceRecipe r : smeltingList.values()) {
			map.put(r.getInput(), r.getOutput());
		}
		return map;
	}

	private static KeyedItemStack createKey(ItemStack is) {
		return new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(true).setIgnoreMetadata(is.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}

	public static boolean isCompiled() {
		return isCompiled;
	}

	public static class FurnaceRecipe {

		private final ItemStack input;
		private final ItemStack output;

		private float experience;

		private FurnaceRecipe(ItemStack in, ItemStack out) {
			this(in, out, 0);
		}

		private FurnaceRecipe(ItemStack in, ItemStack out, float xp) {
			input = in;
			output = out;
			experience = xp;
		}

		public ItemStack getInput() {
			return input.copy();
		}

		public ItemStack getOutput() {
			return output.copy();
		}

		public float getExperience() {
			return experience;
		}

		@Override
		public int hashCode() {
			return createKey(input).hashCode() ^ createKey(output).hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof FurnaceRecipe) {
				FurnaceRecipe f = (FurnaceRecipe)o;
				return ReikaItemHelper.matchStacks(input, f.input) && ReikaItemHelper.matchStacks(output, f.output);
			}
			return false;
		}

		@Override
		public String toString() {
			return input+" > "+output+" + "+experience+" xp";
		}

	}
}
