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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Like HashMap but can take int arrays as keys and still function.
 * Do not use the traditional put/get on this map! */
public class ArrayMap<V> extends HashMap {

	public final int keySize;

	public ArrayMap(int size) {
		keySize = size;
	}

	public V putV(V value, int... key) {
		return this.putA(key, value);
	}

	public V getV(int... key) {
		return this.getA(key);
	}

	public boolean containsKeyV(int... key) {
		return this.containsKeyA(key);
	}

	public V putA(int[] key, V value) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List li = this.toList(key);
		V ret = (V) this.get(li);
		this.put(li, value);
		return ret;
	}

	public V getA(int[] key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List<Integer> li = this.toList(key);
		V ret = (V) this.get(li);
		return ret;
	}

	public boolean containsKeyA(int[] key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List li = this.toList(key);
		return this.containsKey(li);
	}

	private List<Integer> toList(int[] key) {
		List<Integer> li = new ArrayList();
		for (int i = 0; i < this.keySize; i++) {
			li.add(key[i]);
		}
		return li;
	}

}
