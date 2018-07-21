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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;

/** Because apparently Notch is retarded, his original code DEFINITELY is retarded, and everyone is sick of wasting a huge amount
 * of tick time on every smelter iterating over 4000 recipes three times a tick EACH. */
public class ReplacementSmeltingHandler {

	private static final HashMap<KeyedItemStack, FurnaceRecipe> smeltingList = new HashMap();
	private static final HashMap<KeyedItemStack, Float> outputToExperienceMap = new HashMap();
	private static final Collection<FurnaceRecipe> vanillaRecipes = new ArrayList();

	private static final HashMap<KeyedItemStack, FurnaceRecipe> unpermutedSmeltingList = new HashMap();

	private static final ArrayList<MapChange<ItemStack, ItemStack>> MTChanges = new ArrayList();
	private static final HashMap<KeyedItemStack, Float> cachedMTExperience = new HashMap();
	private static boolean isMTRunning;
	private static final Collection<FurnaceRecipe> MTAddedRecipes = new ArrayList();
	private static boolean isDeterminingMT;

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

	public static void prepareForMinetweakerChanges() {
		DragonAPICore.log("Restoring furnace recipes to pre-Minetweaker state. Contains "+unpermutedSmeltingList.size()+" recipes (from "+smeltingList.size()+").");
		smeltingList.clear();
		smeltingList.putAll(unpermutedSmeltingList);

		ReikaJavaLibrary.pConsole("=============CURRENT PRE RECIPES==============");
		ArrayList<FurnaceRecipe> li = new ArrayList(smeltingList.values());
		Collections.sort(li);
		int i = 1;
		for (FurnaceRecipe r : li) {
			ReikaJavaLibrary.pConsole("#"+i+": "+r.toString());
			i++;
		}
		ReikaJavaLibrary.pConsole("==========================================");

		isMTRunning = true;
		DragonAPICore.log("Preparing to read Minetweaker changes to smelting recipes.");
	}

	public static void applyMinetweakerChanges() {
		handleDirectMapChanges();
		isMTRunning = false;
		DragonAPICore.log("Successfully applied "+MTChanges.size()+" Minetweaker changes to smelting recipes.");
		MTChanges.clear();
		cachedMTExperience.clear();

		ReikaJavaLibrary.pConsole("=============CURRENT POST RECIPES==============");
		ArrayList<FurnaceRecipe> li = new ArrayList(smeltingList.values());
		Collections.sort(li);
		int i = 1;
		for (FurnaceRecipe r : li) {
			ReikaJavaLibrary.pConsole("#"+i+": "+r.toString());
			i++;
		}
		ReikaJavaLibrary.pConsole("==========================================");
	}

	private static void handleDirectMapChanges() {
		if (!MTChanges.isEmpty()) {
			for (MapChange<ItemStack, ItemStack> chg : MTChanges) {
				log("Handling Minetweaker recipe change: "+chg);
				switch(chg.operation) {
					case ADD:
						Float xp = cachedMTExperience.get(chg.key);
						registerRecipe(chg.key, chg.value, xp != null ? xp.floatValue() : 0);
						break;
					case REMOVE:
						removeRecipe(chg.key);
						break;
				}
			}
		}
	}

	public static void build() {
		if (isCompiled)
			return;

		Map<ItemStack, ItemStack> map = getInstance().getSmeltingList();
		for (Entry<ItemStack, ItemStack> e : map.entrySet()) {
			ItemStack in = e.getKey();
			ItemStack out = e.getValue();
			AddSmeltingEvent.isVanillaPass = vanillaRecipes.contains(new FurnaceRecipe(in, out, 0));
			registerRecipe(in, out, getInstance().func_151398_b(out));
		}
		AddSmeltingEvent.isVanillaPass = false;

		unpermutedSmeltingList.clear();
		unpermutedSmeltingList.putAll(smeltingList);

		determineMinetweakerRecipes();

		DragonAPICore.log("Compiled replacement smelting system.");

		ReikaJavaLibrary.pConsole("=============CURRENT RECIPES==============");
		ArrayList<FurnaceRecipe> li = new ArrayList(smeltingList.values());
		Collections.sort(li);
		int i = 1;
		for (FurnaceRecipe r : li) {
			ReikaJavaLibrary.pConsole("#"+i+": "+r.toString());
			i++;
		}
		ReikaJavaLibrary.pConsole("==========================================");

		ReikaJavaLibrary.pConsole("=============ORIGINAL RECIPES==============");
		li = new ArrayList(unpermutedSmeltingList.values());
		Collections.sort(li);
		i = 1;
		for (FurnaceRecipe r : li) {
			ReikaJavaLibrary.pConsole("#"+i+": "+r.toString());
			i++;
		}
		ReikaJavaLibrary.pConsole("==========================================");

		isCompiled = true;

		//if (MTInteractionManager.isMTLoaded()) {
		//	applyMinetweakerChanges();
		//}
	}

	private static void determineMinetweakerRecipes() {
		log("Determining which smelting recipes are added by Minetweaker");
		MTAddedRecipes.clear();
		isDeterminingMT = true;
		MTInteractionManager.instance.reloadMT();
		isDeterminingMT = false;
		log("Found "+MTAddedRecipes.size()+" recipes: "+MTAddedRecipes.toString());
		for (FurnaceRecipe f : MTAddedRecipes) {
			unpermutedSmeltingList.remove(createKey(f.input));
		}
	}

	public static boolean checkRecipe(ItemStack in, ItemStack out, float xp) {
		AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
		//ReikaJavaLibrary.pConsole("Checking recipe add for "+in+" to "+out+"; MT is active: "+isMTRunning);
		if (MinecraftForge.EVENT_BUS.post(evt)) {
			if (evt.isValid())
				log("Recipe for "+in+" to "+out+" was cancelled by another mod; it will not be added");
			else
				log("Recipe was marked invalid, it will not be added!");
			return false;
		}
		if (isMTRunning) {
			if (isDeterminingMT) {
				MTAddedRecipes.add(new FurnaceRecipe(in, out, xp));
			}
			else {
				MTChanges.add(new MapChange(Operations.ADD, in, out));
				cachedMTExperience.put(createKey(in), xp);
			}
		}
		return true;
	}

	private static void registerRecipe(ItemStack in, ItemStack out, float xp) {
		FurnaceRecipe f = new FurnaceRecipe(in, out, xp);
		KeyedItemStack key = createKey(in);
		smeltingList.put(key, f);
		if (xp > 0)
			outputToExperienceMap.put(createKey(out), Math.max(getSmeltingXPByOutput(out), xp));
	}

	public static void fireEventsForVanillaRecipes() {
		AddSmeltingEvent.isVanillaPass = true;

		Map<ItemStack, ItemStack> map = getInstance().getSmeltingList();
		Iterator<Entry<ItemStack, ItemStack>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ItemStack, ItemStack> e = it.next();
			ItemStack in = e.getKey();
			ItemStack out = e.getValue();
			if (vanillaRecipes.contains(new FurnaceRecipe(in, out, 0))) {
				float xp = FurnaceRecipes.smelting().func_151398_b(out);
				AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
				MinecraftForge.EVENT_BUS.post(evt);
				if (evt.isCanceled()) {
					it.remove();
				}
				else if (xp != evt.experienceValue) {
					FurnaceRecipes.smelting().experienceList.put(out, evt.experienceValue);
				}
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
		float base = output.getItem().getSmeltingExperience(output);
		if (base != -1)
			return base;
		Float rec = /*smeltingList.get(*/outputToExperienceMap.get(createKey(output))/*)*/;
		return rec != null ? rec.floatValue() : 0;
	}

	public static Map getList() {
		if (isMTRunning) {
			BackWritableHashMap map = new BackWritableHashMap();
			for (FurnaceRecipe r : smeltingList.values()) {
				map.put(r.getInput(), r.getOutput());
			}
			map.tracking = true;
			return map;
		}
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

	private static void log(String s) {
		DragonAPICore.log("Replacement Smelting System: "+s);
	}

	public static class FurnaceRecipe implements Comparable<FurnaceRecipe> {

		private final ItemStack input;
		private final ItemStack output;

		private float experience;

		private FurnaceRecipe(ItemStack in, ItemStack out) {
			this(in, out, 0);
		}

		private FurnaceRecipe(ItemStack in, ItemStack out, float xp) {
			if (in == null)
				throw new IllegalArgumentException("You cannot smelt null to anything!");
			if (out == null)
				throw new IllegalArgumentException("You cannot smelt anything to null!");
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

		@Override
		public int compareTo(FurnaceRecipe f) {
			return ReikaItemHelper.comparator.compare(input, f.input);
		}

	}

	private static class BackWritableHashMap<K, V> extends HashMap<K, V> {

		private boolean tracking = false;

		@Override
		public V put(K key, V val) {
			if (tracking) {
				MTChanges.add(new MapChange<ItemStack, ItemStack>(Operations.ADD, (ItemStack)key, (ItemStack)val));
			}
			return super.put(key, val);
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> map) {
			if (tracking) {
				for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
					MTChanges.add(new MapChange<ItemStack, ItemStack>(Operations.ADD, (ItemStack)e.getKey(), (ItemStack)e.getValue()));
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
				MTChanges.add(new MapChange<ItemStack, ItemStack>(Operations.REMOVE, (ItemStack)key, null));
			}
			return super.remove(key);
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

		@Override
		public String toString() {
			return operation.toString()+" "+key+" > "+value;
		}

	}

	private static enum Operations {
		ADD(),
		REMOVE();

		@Override
		public String toString() {
			return ReikaStringParser.capFirstChar(this.name());
		}
	}
}
