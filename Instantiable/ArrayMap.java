package Reika.DragonAPI.Instantiable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/** Like HashMap but can take int arrays as keys and still function.
 * Do not use the traditional put/get on this map! */
public class ArrayMap extends HashMap {

	public final int keySize;

	public ArrayMap(int size) {
		keySize = size;
	}

	public Object putV(Object value, int... key) {
		return this.put(key, value);
	}

	public Object getV(int... key) {
		return this.get(key);
	}

	public boolean containsKeyV(int... key) {
		return this.containsKey(key);
	}

	public Object put(int[] key, Object value) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List li = Arrays.asList(key);
		Object ret = this.get(li);
		this.put(li, value);
		return ret;
	}

	public Object get(int[] key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List li = Arrays.asList(key);
		Object ret = this.get(li);
		return ret;
	}

	public boolean containsKey(int[] key) {
		if (key.length != keySize)
			throw new IllegalArgumentException("Invalid key length!");
		List li = Arrays.asList(key);
		return this.containsKey(li);
	}

}
