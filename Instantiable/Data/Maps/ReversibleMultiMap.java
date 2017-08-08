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

import java.util.Collection;
import java.util.Collections;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionFactory;

public final class ReversibleMultiMap<K, V> {

	private final MultiMap<K, V> forward;
	private final MultiMap<V, K> backward;

	public ReversibleMultiMap() {
		this(null);
	}

	public ReversibleMultiMap(CollectionFactory cf) {
		forward = new MultiMap(cf);
		backward = new MultiMap(cf);
	}

	public void put(K key, Collection<V> values) {
		this.forward.put(key, values);
		for (V v : values) {
			this.backward.addValue(v, key);
		}
	}

	public void addValue(K key, V value) {
		this.forward.addValue(key, value);
		this.backward.addValue(value, key);
	}

	public Collection<V> getForward(K key) {
		return this.forward.get(key);
	}

	public Collection<K> getBackward(V value) {
		return this.backward.get(value);
	}

	public void clear() {
		this.forward.clear();
		this.backward.clear();
	}

	public void remove(K key, V value) {
		this.forward.remove(key, value);
		this.backward.remove(value, key);
	}

	public void removeKey(K key) {
		Collection<V> c = forward.remove(key);
		for (V v : c) {
			this.backward.remove(v, key);
		}
	}

	public void removeValue(V value) {
		Collection<K> c = backward.remove(value);
		for (K k : c) {
			this.forward.remove(k, value);
		}
	}

	@Override
	public String toString() {
		return "REVERSIBLE: "+this.forward.toString();
	}

	public Collection<K> keySet() {
		return Collections.unmodifiableCollection(forward.keySet());
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(backward.keySet());
	}

}
