/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public final class ChancedOutputList {

	private final ItemHashMap<Float> data = new ItemHashMap();

	private boolean modifiable = true;

	public ChancedOutputList() {

	}

	public ChancedOutputList(ItemHashMap<Float> output) {
		for (ItemStack is : output.keySet()) {
			data.put(is.copy(), output.get(is));
		}
	}

	public ChancedOutputList(ItemStack[] items, float... chances) {
		for (int i = 0; i < items.length; i++) {
			data.put(items[i].copy(), chances[i]);
		}
	}

	/** Chances are in percentages, so 60% is not 0.6, but 60.0 */
	public ChancedOutputList addItem(ItemStack is, float chance) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		data.put(is, chance);
		return this;
	}

	public ChancedOutputList addItems(ArrayList<ItemStack> li, float chance) {
		for (ItemStack is : li)
			this.addItem(is, chance);
		return this;
	}

	public Collection<ItemStack> keySet() {
		Collection<ItemStack> c = new ArrayList();
		for (ItemStack is : data.keySet())
			c.add(is.copy());
		return c;
	}

	public float getItemChance(ItemStack is) {
		Float get = data.get(is);
		return get != null ? get.floatValue() : 0;
	}

	public ArrayList<ItemStack> getAllWithChance(float chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			if (c == chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> getAllWithAtLeastChance(float chance) {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			if (c >= chance)
				li.add(key.copy());
		}
		return li;
	}

	public ArrayList<ItemStack> calculate() {
		ArrayList<ItemStack> li = new ArrayList();
		for (ItemStack key : data.keySet()) {
			float c = data.get(key);
			if (ReikaRandomHelper.doWithChance(c/100D)) // /100 to force all into 0-1 range
				li.add(key.copy());
		}
		return li;
	}

	public ChancedOutputList copy() {
		return new ChancedOutputList(data);
	}

	public ChancedOutputList lock() {
		modifiable = false;
		return this;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void multiplyChances(float f) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		for (ItemStack is : data.keySet()) {
			float f2 = MathHelper.clamp_float(f*data.get(is), 0, 100);
			data.put(is, f2);
		}
	}

	/** Raises chances by the given power. */
	public void powerChances(double p) {
		if (!modifiable)
			throw new UnsupportedOperationException("This ChancedOutputList is locked!");
		for (ItemStack is : data.keySet()) {
			float cur = data.get(is)/100F;
			float pow = 100F*(float)Math.pow(cur, 1D/p);
			float next = MathHelper.clamp_float(pow, 0, 100);
			data.put(is, next);
		}
	}

}
