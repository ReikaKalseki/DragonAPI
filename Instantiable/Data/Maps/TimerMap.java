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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class TimerMap<V> {

	private final Map<V, Integer> timer;

	public TimerMap() {
		this(new HashMap());
	}

	public TimerMap(Map<V, Integer> map) {
		timer = map;
	}

	public void put(V val, int time) {
		this.timer.put(val, time);
	}

	public void clear() {
		timer.clear();
	}

	public void tick() {
		this.tick(1);
	}

	public void tick(int amt) {
		if (!timer.isEmpty()) {
			Iterator<Entry<V, Integer>> it = timer.entrySet().iterator();
			while (it.hasNext()) {
				Entry<V, Integer> e = it.next();
				if (e.getValue() > amt) {
					e.setValue(e.getValue()-amt);
				}
				else {
					if (e.getKey() instanceof TimerCallback) {
						((TimerCallback)e.getKey()).call();
					}
					it.remove();
				}
			}
		}
	}

	public boolean containsKey(V val) {
		return timer.containsKey(val);
	}

	public boolean isEmpty() {
		return timer.isEmpty();
	}

	public Collection<V> keySet() {
		return Collections.unmodifiableCollection(timer.keySet());
	}

	public int get(V val) {
		return timer.get(val);
	}

	@Override
	public final String toString() {
		return timer.toString();
	}

	public final int size() {
		return timer.size();
	}

	public static interface TimerCallback {

		void call();

	}

}
