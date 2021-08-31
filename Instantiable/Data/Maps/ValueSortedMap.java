/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class ValueSortedMap<K, V> {

	private final HashMap<K, V> raw;
	private final TreeMap<K, V> data;

	private Comparator<V> comparator;

	public ValueSortedMap() {
		raw = new HashMap();
		data = new TreeMap(new ValueComparator());
	}

	public ValueSortedMap(Map<K, V> data) {
		this();
		this.putAll(data);
	}

	public ValueSortedMap setComparator(Comparator<V> c) {
		comparator = c;
		return this;
	}

	public int size() {
		return raw.size();
	}

	public boolean isEmpty() {
		return raw.isEmpty();
	}

	public boolean containsKey(K key) {
		return raw.containsKey(key);
	}

	public boolean containsValue(V value) {
		return raw.containsValue(value);
	}

	public V get(K key) {
		return raw.get(key);
	}

	public V put(K key, V value) {
		V ret = raw.put(key, value);
		this.rebuildData();
		return ret;
	}

	public V remove(K key) {
		V ret = raw.remove(key);
		this.rebuildData();
		return ret;
	}

	public void putAll(Map<K, V> m) {
		raw.putAll(m);
		this.rebuildData();
	}

	private void rebuildData() {
		data.clear();
		data.putAll(raw);
	}

	public void clear() {
		raw.clear();
		data.clear();
	}

	public Set<K> keySet() {
		return Collections.unmodifiableSet(data.keySet());
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(data.values());
	}

	public Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(data.entrySet());
	}

	public K getFirstKey() {
		return this.isEmpty() ? null : data.firstEntry().getKey();
	}

	public V getFirst() {
		return this.isEmpty() ? null : data.firstEntry().getValue();
	}

	@Override
	public String toString() {
		return raw.toString();
	}

	private class ValueComparator implements Comparator<V> {

		private ValueComparator() {

		}

		@Override
		public int compare(V o1, V o2) {
			return comparator != null ? comparator.compare(o1, o2) : ((Comparable<V>)raw.get(o1)).compareTo(raw.get(o2));
		}

	}

}
