package Reika.DragonAPI.Instantiable.Data.Immutable;

public class Column {

	public final int minY;
	public final int maxY;

	public Column(int y1, int y2) {
		minY = y1;
		maxY = y2;
	}

	public Column topSlice(int y) {
		if (y > maxY)
			return null;
		return new Column(y, maxY);
	}

	public Column bottomSlice(int y) {
		if (y < minY)
			return null;
		return new Column(minY, y);
	}

	public Column extendTo(int y) {
		int y1 = Math.min(y, minY);
		int y2 = Math.max(y, maxY);
		return new Column(y1, y2);
	}

	public Column merge(Column c) {
		int y1 = Math.min(c.minY, minY);
		int y2 = Math.max(c.maxY, maxY);
		return new Column(y1, y2);
	}

	public boolean contains(int y) {
		return y >= minY && y <= maxY;
	}

}
