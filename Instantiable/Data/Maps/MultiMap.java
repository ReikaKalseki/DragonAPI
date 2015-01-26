/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public final class MultiMap<K, V> {

	private final HashMap<K, Collection<V>> data = new HashMap();

	private boolean modifiable = true;
	private boolean nullEmpty = false;

	public Collection<V> put(K key, Collection<V> value) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
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
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
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
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		Collection<V> ret = data.get(key);
		data.remove(key);
		return ret != null ? ret : new ArrayList();
	}

	public Collection<V> get(K key) {
		Collection<V> c = data.get(key);
		if (c == null && this.nullEmpty)
			return null;
		return c != null ? (this.modifiable ? c : Collections.unmodifiableCollection(c)) : new ArrayList(); //Internal NPE protection
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
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		data.clear();
	}

	public Collection<K> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public Collection<Collection<V>> values() {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		return Collections.unmodifiableCollection(data.values());
	}

	public Collection<V> allValues() {
		Collection<V> li = new ArrayList();
		for (Collection<V> c : data.values()) {
			li.addAll(c);
		}
		return li;
	}

	public int totalSize() {
		return this.allValues().size();
	}

	public boolean containsValue(V value) {
		return ReikaJavaLibrary.collectionMapContainsValue(data, value);
	}

	public boolean containsValueForKey(K key, V value) {
		Collection<V> c = data.get(key);
		return c != null && c.contains(value);
	}

	public boolean remove(K key, V value) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		Collection<V> c = data.get(key);
		return c != null && c.remove(value);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof MultiMap && this.data.equals(((MultiMap)o).data);
	}

	public MultiMap<K, V> lock() {
		modifiable = false;
		return this;
	}

	public MultiMap<K, V> setNullEmpty() {
		nullEmpty = true;
		return this;
	}

}
