package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.ArrayList;
import java.util.Collection;
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

	public void addBlock(int x, int y, int z) {
		this.addBlock(new Coordinate(x, y, z));
	}

	public void addBlock(Coordinate loc) {
		this.addColumn(loc, loc.yCoord, loc.yCoord);
	}

	public Collection<Column> getColumns(int x, int z) {
		return this.getColumns(new Coordinate(x, 0, z));
	}

	public Collection<Column> getColumns(Coordinate c) {
		ColumnSet set = data.get(c.to2D());
		return set != null ? set.getColumns() : new ArrayList();
	}

	private static class ColumnSet {

		//private final ArrayList<Column> columns = new ArrayList();

		private boolean[] data = new boolean[256];

		private int minY = Integer.MAX_VALUE;
		private int maxY = Integer.MIN_VALUE;

		private void addColumn(int y1, int y2) {
			//columns.add(new Column(y1, y2));
			for (int y = y1; y <= y2; y++) {
				data[y] = true;
			}
			minY = Math.min(minY, y1);
			maxY = Math.max(maxY, y2);
		}

		public Collection<Column> getColumns() {
			Collection<Column> li = new ArrayList();
			boolean active = true;
			int startY = minY;
			for (int y = minY; y <= maxY; y++) {
				if (active && !data[y]) {
					li.add(new Column(startY, y-1));
					active = false;
				}
				else if (data[y]) {
					if (!active)
						startY = y;
					active = true;
				}
			}
			li.add(new Column(startY, maxY));
			return li;
		}

	}
}
