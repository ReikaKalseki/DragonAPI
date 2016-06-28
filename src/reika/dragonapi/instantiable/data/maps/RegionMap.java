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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.Rectangle;

/** Uses the rectangle as a key. Do not confuse with {@link RectangleMap}. */
public class RegionMap<V> {

	private final HashMap<Rectangle, V> data = new HashMap();

	public RegionMap() {

	}

	public void clear() {
		data.clear();
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
		return o instanceof RegionMap && this.data.equals(((RegionMap)o).data);
	}

	public V addRegionByWH(int x, int y, int w, int h, V obj) {
		return data.put(new Rectangle(x, y, w, h), obj);
	}

	public V addRegionByDXDY(int minx, int miny, int maxx, int maxy, V obj) {
		return this.addRegionByWH(minx, miny, maxx-minx, maxy-miny, obj);
	}

	public V getRegion(int x, int y) {
		for (Rectangle r : data.keySet()) {
			if (r.contains(x, y))
				return data.get(r);
		}
		return null;
	}

	public V remove(int x, int y) {
		for (Rectangle r : data.keySet()) {
			if (r.getX() == x && r.getY() == y)
				return data.remove(r);
		}
		return null;
	}

	public boolean containsOrigin(int x, int y) {
		for (Rectangle r : data.keySet()) {
			if (r.getX() == x && r.getY() == y)
				return true;
		}
		return false;
	}

	public Map<Rectangle, V> view() {
		return Collections.unmodifiableMap(data);
	}

	public Collection<Rectangle> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

}
