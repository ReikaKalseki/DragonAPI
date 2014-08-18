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

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class ChancedOutputList {

	private final HashMap<ItemStack, Integer> data = new HashMap();

	public ChancedOutputList() {

	}

	public ChancedOutputList(Map<ItemStack, Integer> output) {
		for (ItemStack is : output.keySet()) {
			data.put(is.copy(), output.get(is));
		}
	}

	public ChancedOutputList(ItemStack[] items, int... chances) {
		for (int i = 0; i < items.length; i++) {
			data.put(items[i].copy(), chances[i]);
		}
	}

	public ChancedOutputList addItem(ItemStack is, int chance) {
		data.put(is.copy(), chance);
		return this;
	}

	public ChancedOutputList addItems(ArrayList<ItemStack> is, int chance) {
		for (int i = 0; i < is.size(); i++)
			data.put(is.get(i).copy(), chance);
		return this;
	}

	public HashMap<Integer, ItemStack> getData() {
		HashMap map = new HashMap();
		map.putAll(data);
		return map;
	}

	public int getItemChance(ItemStack is) {
		return data.containsKey(is) ? data.get(is) : 0;
	}

	public ArrayList<ItemStack> getAllWithChance(int chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			int c = data.get(key);
			if (c == chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> getAllWithAtLeastChance(int chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			int c = data.get(key);
			if (c >= chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> calculate() {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			int c = data.get(key);
			if (ReikaRandomHelper.doWithChance(c))
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> getAllItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			li.add(key.copy());
		}
		return li;
	}

}
