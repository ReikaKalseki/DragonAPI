/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

public class RectangleMap<V> {

	private final HashMap<V, Rectangle> data = new HashMap();

	public void addItem(V obj, int x, int y, int w, int h) {
		data.put(obj, new Rectangle(x, y, w, h));
	}

	public V getItemAt(int x, int y) {
		for (V p : data.keySet()) {
			Rectangle r = data.get(p);
			if (r.contains(x, y))
				return p;
		}
		return null;
	}

	public Point getPosition(V obj) {
		Rectangle r = data.get(obj);
		return r != null ? new Point(r.getX(), r.getY()) : null;
	}

	public void clear() {
		this.data.clear();
	}

	public Map<V, Rectangle> view() {
		return Collections.unmodifiableMap(data);
	}

	public Collection<V> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

}
