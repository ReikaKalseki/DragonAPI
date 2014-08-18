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
import java.util.HashMap;
import java.util.List;

/** A multi-key HashMap. */
public class PluralMap<V> extends HashMap {

	public final int keySize;

	public PluralMap(int size) {
		keySize = size;
	}

	public V put(V value, Object... key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		return (V)super.put(this.toList(key), value);
	}

	public V get(Object... key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		return (V)super.get(this.toList(key));
	}

	public boolean containsKeyV(Object... key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		return super.containsKey(this.toList(key));
	}

	private List<Object> toList(Object[] key) {
		List<Object> li = new ArrayList();
		for (int i = 0; i < this.keySize; i++) {
			li.add(key[i]);
		}
		return li;
	}

}
