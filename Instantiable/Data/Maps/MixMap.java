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
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

public final class MixMap<V, K> {

	private final HashMap<V, HashMap<V, K>> data = new HashMap();
	private final HashMap<K, ArrayList<ImmutablePair<V, V>>> reverse = new HashMap();

	public MixMap() {

	}

	public void addSelfMix(V key, K mix) {
		this.addOrSetKey(key, key, mix);
	}

	public void addMix(V v1, V v2, K mix) {
		this.addOrSetKey(v1, v2, mix);
	}

	private void addOrSetKey(V v1, V v2, K mix) { //Bidirectional
		HashMap<V, K> map = data.get(v1);
		if (map == null) {
			map = new HashMap();
			data.put(v1, map);
		}
		map.put(v2, mix);

		map = data.get(v2);
		if (map == null) {
			map = new HashMap();
			data.put(v2, map);
		}
		map.put(v1, mix);

		this.addReverseMapping(v1, v2, mix);
	}

	private void addReverseMapping(V v1, V v2, K mix) {
		ArrayList<ImmutablePair<V, V>> li = reverse.get(mix);
		if (li == null) {
			li = new ArrayList();
			reverse.put(mix, li);
		}
		li.add(new ImmutablePair(v1, v2));
	}

	public K getMix(V v1, V v2) {
		HashMap<V, K> map = data.get(v1);
		return map != null ? map.get(v2) : null;
	}

	public Collection<V> getMixablesWith(V v1) {
		Map<V, K> map = data.get(v1);
		return map != null ? Collections.unmodifiableCollection(map.keySet()) : null;
	}

	public Collection<K> getChildrenOf(V v1) {
		Map<V, K> map = data.get(v1);
		return map != null ? Collections.unmodifiableCollection(map.values()) : null;
	}

	public Collection<V> getMixParents(K k) {
		Collection<V> c = new HashSet();
		Collection<ImmutablePair<V, V>> pairs = this.getMixesMaking(k);
		if (pairs != null) {
			for (ImmutablePair<V, V> p : pairs) {
				c.add(p.left);
				c.add(p.right);
			}
		}
		return c;
	}

	public boolean containsKey(V obj) {
		return data.containsKey(obj);
	}

	public Collection<ImmutablePair<V, V>> getMixesMaking(K mix) {/*
		ArrayList<ImmutablePair<V, V>> li = new ArrayList();
		for (V k1 : data.keySet()) {
			HashMap<V, K> map = data.get(k1);
			for (V k2 : map.keySet()) {
				K out = map.get(k2);
				if (mix.equals(out)) {
					li.add(new ImmutablePair(k1, k2));
				}
			}
		}
		return Collections.unmodifiableCollection(li);*/
		ArrayList li = reverse.get(mix);
		if (li == null)
			li = new ArrayList();
		return Collections.unmodifiableCollection(li);
	}

	public V getOtherEntry(K mix, V entry) {
		ArrayList<ImmutablePair<V, V>> li = reverse.get(mix);
		if (li == null)
			return null;
		for (ImmutablePair<V, V> key : li) {
			if (key.left.equals(entry))
				return key.right;
			if (key.right.equals(entry))
				return key.left;
		}
		return null;
	}

}
