/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.maps;

import java.util.HashMap;
import java.util.Set;

import reika.dragonapi.instantiable.data.WeightedRandom;


public class CountMap<V> {

	private final HashMap<V, Integer> data = new HashMap();

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
	}

	public void set(V key, int num) {
		if (num != 0)
			data.put(key, num);
		else
			data.remove(key);
	}

	public int get(V key) {
		Integer get = data.get(key);
		return get != null ? get.intValue() : 0;
	}

	public int size() {
		return data.size();
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
		return data.keySet();
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

	public boolean isEmpty() {
		return data.isEmpty();
	}

}
