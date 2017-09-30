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
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** Because apparently Notch is retarded, his original code DEFINITELY is retarded, and everyone is sick of wasting a huge amount
 * of tick time on every smelter iterating over 4000 recipes three times a tick EACH. */
public class ReplacementSmeltingHandler {

	private static ConcurrentHashMap<KeyedItemStack, FurnaceRecipe> smeltingList = new ConcurrentHashMap();
	private static ConcurrentHashMap<KeyedItemStack, Float> outputToExperienceMap = new ConcurrentHashMap();
	private static Collection<FurnaceRecipe> vanillaRecipes = new ArrayList(); //for the rebuild function
	private static final ArrayList<MapChange> directMapChanges = new ArrayList();

	private static boolean buildingGettableList = false;

	static {
		AddSmeltingEvent.isVanillaPass = true;
		func_151393_a(Blocks.iron_ore, new ItemStack(Items.iron_ingot), 0.7F);
		func_151393_a(Blocks.gold_ore, new ItemStack(Items.gold_ingot), 1.0F);
		func_151393_a(Blocks.diamond_ore, new ItemStack(Items.diamond), 1.0F);
		func_151393_a(Blocks.sand, new ItemStack(Blocks.glass), 0.1F);
		func_151396_a(Items.porkchop, new ItemStack(Items.cooked_porkchop), 0.35F);
		func_151396_a(Items.beef, new ItemStack(Items.cooked_beef), 0.35F);
		func_151396_a(Items.chicken, new ItemStack(Items.cooked_chicken), 0.35F);
		func_151393_a(Blocks.cobblestone, new ItemStack(Blocks.stone), 0.1F);
		func_151396_a(Items.clay_ball, new ItemStack(Items.brick), 0.3F);
		func_151393_a(Blocks.clay, new ItemStack(Blocks.hardened_clay), 0.35F);
		func_151393_a(Blocks.cactus, new ItemStack(Items.dye, 1, 2), 0.2F);
		func_151393_a(Blocks.log, new ItemStack(Items.coal, 1, 1), 0.15F);
		func_151393_a(Blocks.log2, new ItemStack(Items.coal, 1, 1), 0.15F);
		func_151393_a(Blocks.emerald_ore, new ItemStack(Items.emerald), 1.0F);
		func_151396_a(Items.potato, new ItemStack(Items.baked_potato), 0.35F);
		func_151393_a(Blocks.netherrack, new ItemStack(Items.netherbrick), 0.1F);

		ItemFishFood.FishType[] fish = ItemFishFood.FishType.values();
		for (int i = 0; i < fish.length; i++) {
			ItemFishFood.FishType fishtype = fish[i];
			if (fishtype.func_150973_i()) {
				func_151394_a(new ItemStack(Items.fish, 1, fishtype.func_150976_a()), new ItemStack(Items.cooked_fished, 1, fishtype.func_150976_a()), 0.35F);
			}
		}

		func_151393_a(Blocks.coal_ore, new ItemStack(Items.coal), 0.1F);
		func_151393_a(Blocks.redstone_ore, new ItemStack(Items.redstone), 0.7F);
		func_151393_a(Blocks.lapis_ore, new ItemStack(Items.dye, 1, 4), 0.2F);
		func_151393_a(Blocks.quartz_ore, new ItemStack(Items.quartz), 0.2F);
		AddSmeltingEvent.isVanillaPass = false;
	}

	public static void func_151393_a(Block in, ItemStack out, float xp) {
		func_151396_a(Item.getItemFromBlock(in), out, xp);
	}

	public static void func_151396_a(Item in, ItemStack out, float xp) {
		func_151394_a(new ItemStack(in, 1, OreDictionary.WILDCARD_VALUE), out, xp);
	}

	public static void func_151394_a(ItemStack in, ItemStack out, float xp) {
		addRecipe(in, out, xp);
	}

	public static void addRecipe(ItemStack in, ItemStack out) {
		addRecipe(in, out, 0);
	}

	public static void addRecipe(ItemStack in, ItemStack out, float xp) {
		/*
		if (buildingGettableList) {
			ReikaChatHelper.write("Cannot add smelting recipe while being iterated! Check your logs for errors.");
			DragonAPICore.log("Cannot add smelting recipe while being iterated!");
			Thread.dumpStack();
			return;
		}
		 */
		AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
		MinecraftForge.EVENT_BUS.post(evt);
		if (!evt.isCanceled()) {
			FurnaceRecipe f = new FurnaceRecipe(in, out, evt.experienceValue);
			smeltingList.put(createKey(in), f);
			if (xp > 0)
				outputToExperienceMap.put(createKey(out), Math.max(func_151398_b(out), xp));
			if (AddSmeltingEvent.isVanillaPass)
				vanillaRecipes.add(f);
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
		/*
		if (buildingGettableList) {
			ReikaChatHelper.write("Cannot add smelting recipe while being iterated! Check your logs for errors.");
			DragonAPICore.log("Cannot add smelting recipe while being iterated!");
			Thread.dumpStack();
			return;
		}*/
		/*outputToExperienceMap.remove(*/smeltingList.remove(createKey(in))/*.output)*/;
	}

	public static void modifyExperience(ItemStack is, float newXP) {
		FurnaceRecipe rec = smeltingList.get(createKey(is));
		if (rec != null) {
			rec.experience = newXP;
		}
	}

	//public static ItemStack getSmeltingResult(ItemStack is) { //redirect to obf
	//	return func_151395_a(is);
	//}

	public static ItemStack func_151395_a(ItemStack is) { //obf name - wtf; it works in the dev env too, and dev env does not work with both?!
		handleDirectMapChanges();
		FurnaceRecipe rec = smeltingList.get(createKey(is));
		return rec != null ? rec.output.copy() : null;
	}

	//public static Map getSmeltingList() {
	//	return func_77599_b();
	//}

	public static Map func_77599_b() { //obf name
		return buildGettableList();
	}

	/** Get recipe experience */
	public static float func_151398_b(ItemStack is) {
		return getSmeltingXPByOutput(is);
	}

	public static float getSmeltingXPByOutput(ItemStack output) {
		handleDirectMapChanges();
		Float rec = /*smeltingList.get(*/outputToExperienceMap.get(createKey(output))/*)*/;
		return rec != null ? rec.floatValue() : 0;
	}

	private static KeyedItemStack createKey(ItemStack is) {
		return new KeyedItemStack(is).setSimpleHash(true).setIgnoreNBT(true).setIgnoreMetadata(is.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}

	private static Map buildGettableList() {
		buildingGettableList = true;
		BackWritableHashMap map = new BackWritableHashMap();
		for (FurnaceRecipe r : smeltingList.values()) {
			map.put(r.getInput(), r.getOutput());
		}
		map.tracking = true;
		buildingGettableList = false;
		return map;
	}

	private static void handleDirectMapChanges() {
		if (!directMapChanges.isEmpty()) {
			for (MapChange chg : directMapChanges) {
				switch(chg.operation) {
					case ADD:
						addRecipe((ItemStack)chg.key, (ItemStack)chg.value);
						break;
					case REMOVE:
						removeRecipe((ItemStack)chg.key);
						break;
				}
			}
		}
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

	}

	private static class BackWritableHashMap<K, V> extends HashMap<K, V> {

		//private final ArrayList<MapChange> changes = new ArrayList();

		private boolean tracking = false;

		@Override
		public V put(K key, V val) {
			if (tracking) {
				directMapChanges.add(new MapChange(Operations.ADD, key, val));
				this.log("Logged a direct furnace recipe map operation, adding "+key+" > "+val);
			}
			return super.put(key, val);
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> map) {
			if (tracking) {
				for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
					directMapChanges.add(new MapChange(Operations.ADD, e.getKey(), e.getValue()));
					this.log("Logged a direct furnace recipe map operation, adding "+e.getKey()+" > "+e.getValue());
				}
			}
			super.putAll(map);
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException(".....Why?!");
		}

		@Override
		public V remove(Object key) {
			if (tracking) {
				directMapChanges.add(new MapChange(Operations.REMOVE, key, null));
				this.log("Logged a direct furnace recipe map operation, removing "+key);
			}
			return super.remove(key);
		}

		private void log(String s) {
			//if (DragonAPIInit.instance != null && DragonAPIInit.instance.getModLogger() != null) { //called too early to be safe
			//	DragonAPICore.log(s);
			//}
			//else {
			ReikaJavaLibrary.pConsole("DRAGONAPI: "+s);
			//}
		}

	}

	private static class MapChange<K, V> {

		private final Operations operation;
		private final K key;
		private final V value;

		private MapChange(Operations o, K k, V v) {
			operation = o;
			key = k;
			value = v;
		}

	}

	private static enum Operations {
		ADD(),
		REMOVE();
	}
}
