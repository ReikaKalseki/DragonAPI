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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class MultiMap<K, V> {

	private final HashMap<K, Collection<V>> data = new HashMap();

	public Collection<V> put(K key, Collection<V> value) {
		return data.put(key, value);
	}

	public Collection<V> putValue(K key, V value) {
		Collection<V> ret = this.remove(key);
		this.addValue(key, value, false, false);
		return ret;
	}

	public void addValue(K key, V value) {
		this.addValue(key, value, false);
	}

	public void addValue(K key, V value, boolean allowCopies) {
		this.addValue(key, value, true, allowCopies);
	}

	private void addValue(K key, V value, boolean load, boolean copy) {
		Collection<V> li = null;
		if (load)
			li = data.get(key);
		if (!load || li == null) {
			li = new ArrayList();
			data.put(key, li);
		}
		if (copy || !li.contains(value))
			li.add(value);
	}

	public Collection<V> remove(K key) {
		Collection<V> ret = data.get(key);
		data.remove(key);
		return ret;
	}

	public Collection<V> get(K key) {
		Collection<V> c = data.get(key);
		return c != null ? c : new ArrayList(); //Internal NPE protection
	}

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	public int getSize() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void clear() {
		data.clear();
	}

	public Collection<K> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public Collection<Collection<V>> values() {
		return Collections.unmodifiableCollection(data.values());
	}

	public boolean containsValue(V value) {
		return ReikaJavaLibrary.collectionMapContainsValue(data, value);
	}

	public boolean remove(K key, V value) {
		Collection<V> c = data.get(key);
		return c != null && c.remove(value);
	}

}
