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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class NestedMap<K, M, V> {

	private final HashMap<K, HashMap<M, V>> data = new HashMap();
	private final MultiMap<M, K> innerSet = new MultiMap(new MultiMap.HashSetFactory());
	private final HashSet<V> valueSet = new HashSet();

	public NestedMap() {

	}

	public V put(K key, M inner, V value) {
		HashMap<M, V> map = data.get(key);
		if (map == null) {
			map = new HashMap();
			data.put(key, map);
		}
		innerSet.addValue(inner, key);
		valueSet.add(value);
		return map.put(inner, value);
	}

	public V get(K key, M inner) {
		HashMap<M, V> map = data.get(key);
		return map != null ? map.get(inner) : null;
	}

	public V remove(K key, M inner) {
		HashMap<M, V> map = data.get(key);
		if (map != null) {
			if (map.containsKey(inner)) {
				innerSet.remove(inner, key);
				this.rebuildValues();
			}
			return map.remove(inner);
		}
		return null;
	}

	private void rebuildValues() {
		valueSet.clear();
		for (HashMap<M, V> map : data.values()) {
			valueSet.addAll(map.values());
		}
	}

	public void removeAll(M inner) {
		Collection<K> keys = this.innerSet.get(inner);
		for (K key : keys) {
			this.remove(key, inner);
		}
	}

	public int size() {
		return this.valueSet.size();
	}

	public void putAll(NestedMap map) {
		data.putAll(map.data);
		innerSet.putAll(map.innerSet);
		valueSet.addAll(map.valueSet);
	}

	public void clear() {
		data.clear();
		innerSet.clear();
		valueSet.clear();
	}

	public Set<K> keySet() {
		return data.keySet();
	}

	public Collection<M> innerSet() {
		return this.innerSet.keySet();
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(valueSet);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public boolean containsValue(V value) {
		return this.valueSet.contains(value);
	}

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	public boolean containsInnerKey(M inner) {
		return innerSet.containsKey(inner);
	}

	public Collection<M> getAllKeysIn(K key) {
		HashMap<M, V> map = this.data.get(key);
		return map != null ? map.keySet() : null;
	}

	public Map<M, V> getMap(K key) {
		HashMap<M, V> map = this.data.get(key);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

}
