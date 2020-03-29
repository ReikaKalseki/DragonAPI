package Reika.DragonAPI.Instantiable.Data.Maps;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class PathMap<V> {

	private final NestedMap<Coordinate, Coordinate, V> data = new NestedMap();

	public V get(Coordinate from, Coordinate to) {
		return data.get(from, to);
	}

	public void put(V path, Coordinate from, Coordinate to) {
		data.put(from, to, path);
	}

}
