/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;


public class ThresholdMapping<V> {

	private final TreeMap<Double, V> data;

	public ThresholdMapping() {
		data = new TreeMap();
	}

	public ThresholdMapping(Comparator c) {
		data = new TreeMap(c);
	}

	public V addMapping(double thresh, V value) {
		return this.data.put(thresh, value);
	}

	public V getForValue(double v, boolean ceil) {
		Double d = ceil ? data.ceilingKey(v) : data.floorKey(v);
		return d != null ? this.data.get(d) : null;
	}

	public V remove(double val) {
		return data.remove(val);
	}

	public Collection<Double> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public double maxValue() {
		return data.isEmpty() ? 0 : data.lastKey();
	}

}
