/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class ChancedOutputList {

	private final HashMap<List<Integer>, Integer> data = new HashMap();

	public ChancedOutputList() {

	}

	public ChancedOutputList(Map<ItemStack, Integer> output) {
		for (ItemStack is : output.keySet()) {
			data.put(Arrays.asList(is.itemID, is.getItemDamage()), output.get(is));
		}
	}

	public ChancedOutputList(ItemStack[] items, int... chances) {
		for (int i = 0; i < items.length; i++) {
			data.put(Arrays.asList(items[i].itemID, items[i].getItemDamage()), chances[i]);
		}
	}

	public ChancedOutputList addItem(ItemStack is, int chance) {
		data.put(Arrays.asList(is.itemID, is.getItemDamage()), chance);
		return this;
	}

	public ChancedOutputList addItems(ArrayList<ItemStack> is, int chance) {
		for (int i = 0; i < is.size(); i++)
			data.put(Arrays.asList(is.get(i).itemID, is.get(i).getItemDamage()), chance);
		return this;
	}

	public HashMap<Integer, ItemStack> getData() {
		HashMap map = new HashMap();
		map.putAll(data);
		return map;
	}

	public int getItemChance(ItemStack is) {
		List<Integer> key = Arrays.asList(is.itemID, is.getItemDamage());
		return data.containsKey(key) ? data.get(key) : 0;
	}

	public ArrayList<ItemStack> getAllWithChance(int chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (List<Integer> key : data.keySet()) {
			int c = data.get(key);
			if (c == chance)
				li.add(new ItemStack(key.get(0), 1, key.get(1)));
		}
		return li;
	}

	public ArrayList<ItemStack> getAllWithAtLeastChance(int chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (List<Integer> key : data.keySet()) {
			int c = data.get(key);
			if (c >= chance)
				li.add(new ItemStack(key.get(0), 1, key.get(1)));
		}
		return li;
	}

	public ArrayList<ItemStack> calculate() {
		ArrayList<ItemStack> li = new ArrayList();
		for (List<Integer> key : data.keySet()) {
			int c = data.get(key);
			if (ReikaRandomHelper.doWithChance(c))
				li.add(new ItemStack(key.get(0), 1, key.get(1)));
		}
		return li;
	}

	public ArrayList<ItemStack> getAllItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (List<Integer> key : data.keySet()) {
			li.add(new ItemStack(key.get(0), 1, key.get(1)));
		}
		return li;
	}

}
