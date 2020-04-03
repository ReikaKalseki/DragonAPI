package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.ArrayList;
import java.util.HashMap;

import Reika.DragonAPI.Instantiable.Data.Immutable.Column;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class ColumnMap {

	private final HashMap<Coordinate, ColumnSet> data = new HashMap();

	public void addColumn(int x, int z, int y1, int y2) {
		this.addColumn(new Coordinate(x, 0, z), y1, y2);
	}

	public void addColumn(Coordinate loc, int y1, int y2) {
		Coordinate key = loc.to2D();
		ColumnSet c = data.get(key);
		if (c == null) {
			c = new ColumnSet();
			data.put(key, c);
		}
		c.addColumn(y1, y2);
	}

	private static class ColumnSet {

		private final ArrayList<Column> columns = new ArrayList();

		private int minY = Integer.MAX_VALUE;
		private int maxY = Integer.MIN_VALUE;

		private void addColumn(int y1, int y2) {
			columns.add(new Column(y1, y2));
			minY = Math.min(minY, y1);
			maxY = Math.max(maxY, y2);
		}

		private void mergeColumns() {

		}

	}
}
