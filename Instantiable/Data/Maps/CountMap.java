/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTIO;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;


public class CountMap<V> {

	private final HashMap<V, Integer> data = new HashMap();
	private int total;

	public CountMap() {

	}

	public void increment(V key) {
		this.increment(key, 1);
	}

	public void increment(V key, int num) {
		Integer get = data.get(key);
		int has = get != null ? get.intValue() : 0;
		int next = has+num;
		this.set(key, next);
		total += num;
	}

	public void increment(CountMap<V> map) {
		for (V val : map.data.keySet()) {
			this.increment(val, map.data.get(val));
		}
	}

	public void subtract(V key, int num) {
		int has = this.get(key);
		if (num >= has)
			this.remove(key);
		else
			this.increment(key, -num);
	}

	public void set(V key, int num) {
		if (num != 0)
			data.put(key, num);
		else
			data.remove(key);
	}

	public int remove(V key) {
		Integer amt = data.remove(key);
		if (amt == null)
			amt = 0;
		this.total -= amt;
		return amt;
	}

	public int get(V key) {
		Integer get = data.get(key);
		return get != null ? get.intValue() : 0;
	}

	public int size() {
		return data.size();
	}

	public int getTotalCount() {
		return total;
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof CountMap && ((CountMap)o).data.equals(data);
	}

	@Override
	public String toString() {
		return this.data.toString();
	}

	public Set<V> keySet() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public boolean containsKey(V key) {
		return data.containsKey(key);
	}

	public void clear() {
		data.clear();
	}

	public WeightedRandom<V> asWeightedRandom() {
		WeightedRandom<V> w = new WeightedRandom();
		for (V key : data.keySet()) {
			w.addEntry(key, this.get(key));
		}
		return w;
	}

	public double getFraction(V k) {
		if (this.total == 0)
			return 0;
		return this.get(k)/(double)this.getTotalCount();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Map<V, Integer> view() {
		return Collections.unmodifiableMap(data);
	}

	public void readFromNBT(NBTTagCompound tag, NBTIO<V> converter) {
		total = tag.getInteger("total");

		data.clear();
		NBTTagList li = tag.getTagList("data", NBTTypes.COMPOUND.ID);
		for (Object o : li.tagList) {
			NBTTagCompound dat = (NBTTagCompound)o;
			V key = (V)ReikaNBTHelper.getValue(dat.getTag("key"), converter);
			int amt = dat.getInteger("value");
			data.put(key, amt);
		}
	}

	public void writeToNBT(NBTTagCompound tag, NBTIO<V> converter) {
		tag.setInteger("total", total);
		NBTTagList li = new NBTTagList();
		for (V k : data.keySet()) {
			NBTTagCompound dat = new NBTTagCompound();
			int amt = this.get(k);
			dat.setTag("key", ReikaNBTHelper.getTagForObject(k, converter));
			dat.setInteger("value", amt);
			li.appendTag(dat);
		}
		tag.setTag("data", li);
	}

}
