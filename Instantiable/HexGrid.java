package Reika.DragonAPI.Instantiable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class HexGrid {

	private static ArrayList<Hex> directions = ReikaJavaLibrary.makeListFrom(new Hex(1, 1, 0, -1), new Hex(1, 1, -1, 0), new Hex(1, 0, -1, 1), new Hex(1, -1, 0, 1), new Hex(1, -1, 1, 0), new Hex(1, 0, 1, -1));
	private static ArrayList<Hex> diagonals = ReikaJavaLibrary.makeListFrom(new Hex(1, 2, -1, -1), new Hex(1, 1, -2, 1), new Hex(1, -1, -1, 2), new Hex(1, -2, 1, 1), new Hex(1, -1, 2, -1), new Hex(1, 1, 1, -2));

	private static final Orientation angled = new Orientation(Math.sqrt(3D), Math.sqrt(3D) / 2D, 0D, 3D / 2D, Math.sqrt(3D) / 3D, -1D / 3D, 0D, 2D / 3D, 0.5);
	private static final Orientation flat = new Orientation(3D / 2D, 0D, Math.sqrt(3D) / 2D, Math.sqrt(3D), 2D / 3D, 0D, -1D / 3D, Math.sqrt(3D) / 3D, 0D);

	private final HashMap<Coordinate, Hex> hexes = new HashMap();

	public final int size;
	public final double hexSize;

	private final Orientation style;
	private final MapShape shape;

	public HexGrid(int s, double s2, boolean flatTop, MapShape shape) {
		style = flatTop ? flat : angled;
		size = s;
		this.shape = shape;
		hexSize = s2;
	}

	public HexGrid addHex(Hex h) {
		return this.addHex(h.q, h.r, h.s);
	}

	public HexGrid addHex(int q, int r, int s) {
		if (q+r+s != 0)
			throw new IllegalArgumentException("Q, R, and S must sum to zero!");
		if (!shape.isInGrid(q, r, s, size))
			throw new IllegalArgumentException("Position outside grid!");
		if (!hexes.containsKey(new Coordinate(q, r, s)))
			hexes.put(new Coordinate(q, r, s), new Hex(hexSize, q, r, s));
		return this;
	}

	/** Expands in rings; ideal for creating hexagonal shapes. */
	public HexGrid flower() {
		this.addHex(0, 0, 0);
		for (int i = 1; i <= size; i += 2) {
			Collection<Hex> cp = new HashSet(hexes.values());
			for (Hex h : cp) {
				Collection<Hex> c = h.getNeighbors();
				for (Hex h2 : c) {
					if (shape.isInGrid(h2, size)) {
						this.addHex(h2);
					}
				}
			}
		}
		return this;
	}

	public int cellCount() {
		return hexes.size();
	}

	public Hex getHex(int q, int r, int s) {
		return hexes.get(new Coordinate(q, r, s));//this.getHex(shape.getI(q, r, s), shape.getK(q, r, s));
	}

	public static enum MapShape {
		RECTANGLE(),
		TRIANGLE(),
		HEXAGON(),
		RHOMBUS();

		public boolean isInGrid(Hex h, int radius) {
			return this.isInGrid(h.q, h.r, h.s, radius);
		}

		public boolean isInGrid(int q, int r, int s, int radius) {
			switch(this) {
				case HEXAGON:
					return Math.abs(q+r+s) <= radius;
				case RECTANGLE:
					break; //TODO
				case RHOMBUS:
					break; //TODO
				case TRIANGLE:
					break; //TODO
			}
			return false;
		}

		/*
		public int getI(int q, int r, int s) {
			switch(this) {
				case RECTANGLE:
					return r;
				case TRIANGLE:
					return r;
				case HEXAGON:
					return r+N;
				case RHOMBUS:
					return r;
				default:
					return -1;
			}
		}

		public int getK(int q, int r, int s) {
			switch(this) {
				case RECTANGLE:
					return q+r/2;
				case TRIANGLE:
					return q;
				case HEXAGON:
					return q+N+Math.min(r, 0);
				case RHOMBUS:
					return q;
				default:
					return -1;
			}
		}

		public int getQ(int i, int k) {
			switch(this) {
				case RECTANGLE:
				case TRIANGLE:
				case HEXAGON:
				case RHOMBUS:
				default:
			}
		}

		public int getR(int i, int k) {
			switch(this) {
				case RECTANGLE:
				case TRIANGLE:
				case HEXAGON:
				case RHOMBUS:
				default:
			}
		}

		public int getS(int i, int k) {
			switch(this) {
				case RECTANGLE:
				case TRIANGLE:
				case HEXAGON:
				case RHOMBUS:
				default:
			}
		}*/
	}

	public Point hexToPixel(Hex h) {
		double x = (style.f0 * h.q + style.f1 * h.r) * hexSize;
		double y = (style.f2 * h.q + style.f3 * h.r) * hexSize;
		return new Point((int)Math.round(x), (int)Math.round(y));
	}

	public FractionalHex pixelToHex(Point p) {
		double dx = p.x/hexSize;
		double dy = p.y/hexSize;
		double q = style.b0 * dx + style.b1 * dy;
		double r = style.b2 * dx + style.b3 * dy;
		return new FractionalHex(hexSize, q, r, -q - r);
	}
	/*
public Point hexCornerOffset(int corner) {
	Point size = layout.size;
	double angle = 2.0 * Math.PI * (M.start_angle - corner) / 6;
	return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
}

public ArrayList<Point> polygonCorners(Hex h) {
	ArrayList<Point> corners = new ArrayList();
	Point center = hexToPixel(layout, h);
	for (int i = 0; i < 6; i++) {
		Point offset = hexCornerOffset(layout, i);
		corners.add(new Point(center.x + offset.x, center.y + offset.y));
	}
	return corners;
}
	 */

	public static final class Hex {

		public final double size;

		public final int q;
		public final int r;
		public final int s;

		/*
		private Hex(int i, int k, MapShape shape) { //from cartesian array coords
			this(shape.getQ(i, k), shape.getR(i, k), shape.getS(i, k));
		}*/

		public Hex(double sz, int q, int r, int s) {
			size = sz;
			this.q = q;
			this.r = r;
			this.s = s;
		}

		public Hex scale(int k) {
			return new Hex(size, q * k, r * k, s * k);
		}

		public Hex neighbor(int direction) {
			return add(size, this, relativeDirection(direction));
		}

		public Hex diagonalNeighbor(int direction) {
			return add(size, this, diagonals.get(direction));
		}

		public int length() {
			return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
		}

		public Collection<Hex> getNeighbors() {
			Collection<Hex> c = new HashSet();
			for (Hex h : directions) {
				c.add(add(size, this, h));
			}
			return c;
		}

		public static int distance(double size, Hex a, Hex b) {
			return Hex.subtract(size, a, b).length();
		}

		public static Hex add(double size, Hex a, Hex b) {
			return new Hex(size, a.q + b.q, a.r + b.r, a.s + b.s);
		}

		public static Hex subtract(double size, Hex a, Hex b) {
			return new Hex(size, a.q - b.q, a.r - b.r, a.s - b.s);
		}

		private static Hex relativeDirection(int direction) {
			return directions.get(direction);
		}
	}

	private static final class FractionalHex {

		public final double size;

		public final double q;
		public final double r;
		public final double s;

		public FractionalHex(double sz, double q, double r, double s) {
			size = sz;

			this.q = q;
			this.r = r;
			this.s = s;
		}

		public Hex hexRound() {
			int q = (int)Math.round(this.q);
			int r = (int)Math.round(this.r);
			int s = (int)Math.round(this.s);
			double q_diff = Math.abs(q - this.q);
			double r_diff = Math.abs(r - this.r);
			double s_diff = Math.abs(s - this.s);
			if (q_diff > r_diff && q_diff > s_diff) {
				q = -r - s;
			}
			else {
				if (r_diff > s_diff) {
					r = -q - s;
				}
				else {
					s = -q - r;
				}
			}
			return new Hex(size, q, r, s);
		}

		public static FractionalHex hexLerp(double size, FractionalHex a, FractionalHex b, double t) {
			return new FractionalHex(size, a.q * (1 - t) + b.q * t, a.r * (1 - t) + b.r * t, a.s * (1 - t) + b.s * t);
		}

		public static ArrayList<Hex> hexLinedraw(double size, Hex a, Hex b) {
			int N = Hex.distance(size, a, b);
			FractionalHex a_nudge = new FractionalHex(a.size, a.q + 0.000001, a.r + 0.000001, a.s - 0.000002);
			FractionalHex b_nudge = new FractionalHex(a.size, b.q + 0.000001, b.r + 0.000001, b.s - 0.000002);
			ArrayList<Hex> results = new ArrayList();
			double step = 1.0 / Math.max(N, 1);
			for (int i = 0; i <= N; i++) {
				results.add(FractionalHex.hexLerp(size, a_nudge, b_nudge, step * i).hexRound());
			}
			return results;
		}

	}

	private static final class OffsetCoord {

		public final int col;
		public final int row;
		public static final int EVEN = 1;
		public static final int ODD = -1;

		public OffsetCoord(int col, int row) {
			this.col = col;
			this.row = row;
		}

		public Hex roffsetToCube(double size, int offset) {
			int q = col - (row + offset * (row & 1)) / 2;
			int r = row;
			int s = -q - r;
			return new Hex(size, q, r, s);
		}

		public Hex qoffsetToCube(double size, int offset) {
			int q = col;
			int r = row - (col + offset * (col & 1)) / 2;
			int s = -q - r;
			return new Hex(size, q, r, s);
		}

		public static OffsetCoord roffsetFromCube(int offset, Hex h) {
			int col = h.q + (h.r + offset * (h.r & 1)) / 2;
			int row = h.r;
			return new OffsetCoord(col, row);
		}

		public static OffsetCoord qoffsetFromCube(int offset, Hex h) {
			int col = h.q;
			int row = h.r + (h.q + offset * (h.q & 1)) / 2;
			return new OffsetCoord(col, row);
		}

	}

	private static final class Orientation {

		public final double f0;
		public final double f1;
		public final double f2;
		public final double f3;
		public final double b0;
		public final double b1;
		public final double b2;
		public final double b3;
		public final double start_angle;

		public Orientation(double f0, double f1, double f2, double f3, double b0, double b1, double b2, double b3, double start_angle) {
			this.f0 = f0;
			this.f1 = f1;
			this.f2 = f2;
			this.f3 = f3;
			this.b0 = b0;
			this.b1 = b1;
			this.b2 = b2;
			this.b3 = b3;
			this.start_angle = start_angle;
		}
	}

}

