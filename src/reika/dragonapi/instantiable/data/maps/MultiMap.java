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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import reika.dragonapi.libraries.java.ReikaJavaLibrary;

public final class MultiMap<K, V> {

	private final HashMap<K, Collection<V>> data = new HashMap();
	//private final HashSet<ImmutablePair<K, V>> pairSet = new HashSet();

	private boolean modifiable = true;
	private boolean nullEmpty = false;
	private Comparator<V> ordering = null;

	private final CollectionFactory factory;

	public MultiMap() {
		this(null);
	}

	public MultiMap(CollectionFactory cf) {
		factory = cf != null ? cf : new ListFactory();
	}

	public Collection<V> put(K key, Collection<V> value) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		if (ordering != null && value instanceof List)
			Collections.sort((List)value, ordering);
		Collection<V> old = data.put(key, factory.createCollection(value));
		if (old != null)
			this.removeContainKeys(key, old);
		this.addContainKeys(key, value);
		return old;
	}

	private void removeContainKey(K key, V value) {
		//pairSet.remove(new ImmutablePair(key, value));
	}

	private void removeContainKeys(K key, Collection<V> set) {
		/*
		for (V val : set) {
			this.removeContainKey(key, val);
		}
		 */
	}

	private void addContainKey(K key, V value) {
		//pairSet.add(new ImmutablePair(key, value));
	}

	private void addContainKeys(K key, Collection<V> set) {
		/*
		for (V val : set) {
			this.addContainKey(key, val);
		}
		 */
	}

	public Collection<V> putValue(K key, V value) {
		Collection<V> ret = this.remove(key);
		if (ret != null)
			this.removeContainKeys(key, ret);
		this.addValue(key, value, false, false);
		return ret;
	}

	public boolean addValue(K key, V value) {
		return this.addValue(key, value, false);
	}

	public boolean addValue(K key, V value, boolean allowCopies) {
		return this.addValue(key, value, true, allowCopies);
	}

	private boolean addValue(K key, V value, boolean load, boolean copy) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		Collection<V> li = null;
		if (load)
			li = data.get(key);
		if (!load || li == null) {
			li = this.createCollection();
			data.put(key, li);
		}
		if (copy || !factory.allowsDuplicates() || /*!li.contains(value)*/!this.containsValueForKey(key, value)) {
			li.add(value);
			if (ordering != null && li instanceof List)
				Collections.sort((List)li, ordering);
			this.addContainKey(key, value);
			return true;
		}
		return false;
	}

	private Collection<V> createCollection() {
		return this.factory.createCollection();
	}

	public Collection<V> remove(K key) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		Collection<V> ret = data.remove(key);
		if (ret != null)
			this.removeContainKeys(key, ret);
		return ret != null ? ret : new ArrayList();
	}

	public Collection<V> get(K key) {
		Collection<V> c = data.get(key);
		if (c == null && this.nullEmpty)
			return null;
		return c != null ? (this.modifiable ? c : Collections.unmodifiableCollection(c)) : this.factory.createCollection(); //Internal NPE protection
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
		//pairSet.clear();
	}

	public Collection<K> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public Collection<Collection<V>> values() {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		return Collections.unmodifiableCollection(data.values());
	}

	public Collection<V> allValues(boolean duplicates) {
		Collection<V> li = duplicates ? new ArrayList() : new HashSet();
		for (Collection<V> c : data.values()) {
			li.addAll(c);
		}
		return li;
	}

	public void shuffleValues() {
		for (Collection c : data.values()) {
			if (c instanceof List)
				Collections.shuffle((List)c);
		}
	}

	public int totalSize() {
		return this.allValues(true).size();
	}

	public boolean containsValue(V value) {
		return ReikaJavaLibrary.collectionMapContainsValue(data, value);
	}

	public boolean containsValueForKey(K key, V value) {

		Collection<V> c = data.get(key);
		return c != null && c.contains(value);

		//return pairSet.contains(new ImmutablePair(key, value));
	}

	public boolean remove(K key, V value) {
		if (!modifiable)
			throw new UnsupportedOperationException("Map "+this+" is locked!");
		Collection<V> c = data.get(key);
		boolean flag = c != null && c.remove(value);
		this.removeContainKey(key, value);
		if (flag && c.isEmpty()) {
			this.data.remove(key);
		}
		return flag;
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

	public MultiMap<K, V> setOrdered(Comparator<V> order) {
		this.ordering = order;
		return this;
	}

	public void putAll(Map<K, V> map) {
		for (K k : map.keySet()) {
			this.addValue(k, map.get(k));
		}
	}

	public void putAll(MultiMap<K, V> map) {
		for (K k : map.keySet()) {
			for (V v : map.get(k)) {
				this.addValue(k, v);
			}
		}
	}

	public static interface CollectionFactory<C> {

		public Collection<? extends C> createCollection();
		public Collection<? extends C> createCollection(Collection c);
		public boolean allowsDuplicates();

	}

	public static final class ListFactory implements CollectionFactory<ArrayList> {

		@Override
		public ArrayList createCollection() {
			return new ArrayList();
		}

		@Override
		public ArrayList createCollection(Collection c) {
			return new ArrayList(c);
		}

		@Override
		public boolean allowsDuplicates() {
			return true;
		}

	}

	public static final class HashSetFactory implements CollectionFactory<HashSet> {

		@Override
		public HashSet createCollection() {
			return new HashSet();
		}

		@Override
		public HashSet createCollection(Collection c) {
			return new HashSet(c);
		}

		@Override
		public boolean allowsDuplicates() {
			return false;
		}

	}

}
