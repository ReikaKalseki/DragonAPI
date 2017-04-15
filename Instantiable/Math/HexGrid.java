package Reika.DragonAPI.Instantiable.Math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;

public class HexGrid {

	private static final List<Hex> directions = Collections.unmodifiableList(ReikaJavaLibrary.makeListFrom(new Hex(1, 1, -1, 0), new Hex(1, 0, -1, 1), new Hex(1, -1, 0, 1), new Hex(1, -1, 1, 0), new Hex(1, 0, 1, -1), new Hex(1, 1, 0, -1)));
	private static final List<Hex> diagonals = Collections.unmodifiableList(ReikaJavaLibrary.makeListFrom(new Hex(1, 2, -1, -1), new Hex(1, 1, -2, 1), new Hex(1, -1, -1, 2), new Hex(1, -2, 1, 1), new Hex(1, -1, 2, -1), new Hex(1, 1, 1, -2)));

	private static final Orientation angled = new Orientation(Math.sqrt(3D), Math.sqrt(3D) / 2D, 0D, 3D / 2D, Math.sqrt(3D) / 3D, -1D / 3D, 0D, 2D / 3D, 0.5);
	private static final Orientation flat = new Orientation(3D / 2D, 0D, Math.sqrt(3D) / 2D, Math.sqrt(3D), 2D / 3D, 0D, -1D / 3D, Math.sqrt(3D) / 3D, 0D);

	private final HashMap<Coordinate, Hex> hexes = new HashMap();

	public final int size;
	public final double hexSize;

	private final Orientation style;
	private final MapShape shape;

	private GridProperties properties;

	/** Size is a diameter! */
	public HexGrid(int s, double s2, boolean flatTop, MapShape shape) {
		style = flatTop ? flat : angled;
		size = s;
		this.shape = shape;
		hexSize = s2;
	}

	public GridProperties getGridProperties() {
		if (properties == null) {
			properties = new GridProperties(this);
		}
		return properties;
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
		properties = null;
		return this;
	}

	/** Expands in rings; ideal for creating hexagonal shapes. */
	public HexGrid flower() {
		this.addHex(0, 0, 0);
		for (int i = 1; i < size; i += 2) {
			Collection<Hex> cp = new HashSet(this.getAllHexes());
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

	public Collection<Hex> getAllHexes() {
		return Collections.unmodifiableCollection(hexes.values());
	}

	public Hex getHex(int q, int r, int s) {
		return hexes.get(new Coordinate(q, r, s));//this.getHex(shape.getI(q, r, s), shape.getK(q, r, s));
	}

	public Hex getRandomEdgeCell(Random rand) { //alternative: keep picking random cells until find one without six neighbors
		int d = rand.nextInt(6);
		Hex h = new Hex(hexSize, 0, 0, 0);
		while (this.containsHex(h.getNeighbor(d))) {
			h = h.getNeighbor(d);
		}
		return h;
	}

	public boolean isHexAtEdge(Hex h) {
		return Math.abs(h.q)+Math.abs(h.r)+Math.abs(h.s) == size-1;
	}

	public static enum MapShape {
		RECTANGLE(),
		TRIANGLE(),
		HEXAGON(),
		RHOMBUS();

		public boolean isInGrid(Hex h, int diameter) {
			return this.isInGrid(h.q, h.r, h.s, diameter);
		}

		public boolean isInGrid(int q, int r, int s, int diameter) {
			switch(this) {
				case HEXAGON:
					return Math.abs(q)+Math.abs(r)+Math.abs(s) <= diameter-1;
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

	public Point getHexLocation(Hex h) {
		double x = (style.f0 * h.q + style.f1 * h.r) * hexSize/2D;
		double y = (style.f2 * h.q + style.f3 * h.r) * hexSize/2D;
		return new Point(x, y);
	}

	public Hex getHexAtLocation(int x, int y) {
		FractionalHex f = this.getFHexAtLocation(x, y);
		Hex h = f.hexRound();
		return this.containsHex(h) ? h : null;
	}

	public boolean containsHex(Hex h) {
		return hexes.containsKey(new Coordinate(h.q, h.r, h.s));
	}

	public boolean containsHex(int q, int r, int s) {
		return hexes.containsKey(new Coordinate(q, r, s));
	}

	private FractionalHex getFHexAtLocation(int x, int y) {
		double dx = x*2D/hexSize;
		double dy = y*2D/hexSize;
		double q = style.b0 * dx + style.b1 * dy;
		double r = style.b2 * dx + style.b3 * dy;
		return new FractionalHex(hexSize, q, r, -q - r);
	}

	public int getNeighborDirection(double angle) {
		angle = (angle+360D)%360D;
		double a = angle;//-Math.toDegrees(style.start_angle);
		int val = (int)Math.floor(a/60D);
		return val;
	}

	public void drawHexEdges(Tessellator v5, Hex h, int color) {
		float f = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(3);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		v5.startDrawing(GL11.GL_LINE_LOOP);
		v5.setColorRGBA_I(color & 0xFFFFFF, ReikaColorAPI.getAlpha(color));

		double r = hexSize/2;
		for (double a = Math.toDegrees(style.start_angle); a < 360; a += 60) {
			double dx = r+0.1+r*Math.cos(Math.toRadians(a));
			double dy = r-1+r*Math.sin(Math.toRadians(a));
			v5.addVertex(dx, dy, 0);
		}

		v5.draw();
		GL11.glLineWidth(f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void drawFilledHex(Tessellator v5, Hex h, int color) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setColorRGBA_I(color & 0xFFFFFF, ReikaColorAPI.getAlpha(color));

		double r = hexSize/2;
		v5.addVertex(r+0.1, r-1, 0);

		for (double a = Math.toDegrees(style.start_angle); a <= 360+style.start_angle; a += 60) {
			double dx = r+0.1+r*Math.cos(Math.toRadians(a));
			double dy = r-1+r*Math.sin(Math.toRadians(a));
			v5.addVertex(dx, dy, 0);
		}

		v5.draw();
		GL11.glPopAttrib();
	}

	public void drawTexturedGrid(Tessellator v5) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);

		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (Hex h : hexes.values()) {
			Point p = this.getHexLocation(h);
			minX = Math.min(minX, p.x);
			minY = Math.min(minY, p.y);
			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}
		minX -= hexSize/2;
		minY -= hexSize/2;
		maxX += hexSize/2;
		maxY += hexSize/2;

		double sizeX = maxX-minX;
		double sizeY = maxY-minY;
		for (Hex h : hexes.values()) {
			Point p = this.getHexLocation(h);
			double fx = (p.x-hexSize/2)/sizeX;
			double fy = (p.y-hexSize/2)/sizeY;
			GL11.glPushMatrix();
			GL11.glTranslated(p.x, p.y, 0);
			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			v5.setColorRGBA_I(0xffffff, 255);

			double cu = 0.5+fx;
			double cv = 0.5+fy;
			//this.drawTexturedHex(v5, h, 0.5+fx, 0.5+fy, 1D/size*0.625);

			double r = hexSize/2;
			double x = r+0.1;
			double y = r-1;
			double u = cu+x/sizeX;
			double v = cv+y/sizeY;
			v5.addVertexWithUV(x, y, 0, u, v);

			for (double a = Math.toDegrees(style.start_angle); a <= 360+style.start_angle; a += 60) {
				double ang = Math.toRadians(a);
				double dx = r+0.1+r*Math.cos(ang);
				double dy = r-1+r*Math.sin(ang);

				u = cu+dx/sizeX;
				v = cv+dy/sizeY;
				v5.addVertexWithUV(dx, dy, 0, u, v);
			}

			v5.draw();
			GL11.glPopMatrix();
		}

		GL11.glPopAttrib();
	}

	public void drawTexturedHex(Tessellator v5, Hex h, double cu, double cv, double tr) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_CULL_FACE);
		v5.startDrawing(GL11.GL_TRIANGLE_FAN);
		v5.setColorRGBA_I(0xffffff, 255);

		double r = hexSize/2;
		v5.addVertexWithUV(r+0.1, r-1, 0, cu, cv);

		for (double a = Math.toDegrees(style.start_angle); a <= 360+style.start_angle; a += 60) {
			double ang = Math.toRadians(a);
			double dx = r+0.1+r*Math.cos(ang);
			double dy = r-1+r*Math.sin(ang);
			double u = cu+tr*Math.cos(ang);
			double v = cv+tr*Math.sin(ang);
			v5.addVertexWithUV(dx, dy, 0, u, v);
		}

		v5.draw();
		GL11.glPopAttrib();
	}

	public ArrayList<Integer> getValidMovementDirections(Hex location) {
		ArrayList<Integer> ret = new ArrayList();
		for (int i = 0; i < 6; i++) {
			Hex h = location.getNeighbor(i);
			if (this.containsHex(h))
				ret.add(i);
		}
		return ret;
	}

	public int getOppositeDirection(int dir) {
		return (dir+3)%6;
	}

	public Collection<Hex> getRegion(Hex start, Collection<Hex> exclusions) {
		return this.getRegion(start, new HashSet(exclusions));
	}

	private Collection<Hex> getRegion(Hex start, HashSet<Hex> exclusions) {
		exclusions.add(start);
		Collection<Hex> ret = new HashSet();
		ret.add(start);
		for (Hex h : start.getNeighbors()) {
			if (this.containsHex(h) && !exclusions.contains(h)) {
				ret.addAll(this.getRegion(h, exclusions));
			}
		}
		return ret;
	}

	public boolean dividesGrid(Hex start, Collection<Hex> exclusions) {
		return this.dividesGrid(start, new HashSet(exclusions));
	}

	private boolean dividesGrid(Hex start, HashSet<Hex> exclusions) {
		Collection<Hex> region = null;
		for (Hex h : start.getNeighbors()) {
			if (this.containsHex(h) && !exclusions.contains(h)) {
				Collection<Hex> region2 = this.getRegion(start, exclusions);
				//ReikaJavaLibrary.pConsole(start+" > "+region2.size());
				if (region == null)
					region = region2;
				if (!region.equals(region2))
					return true;
			}
		}
		return false;
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

		public Hex getNeighbor(int direction) {
			return add(size, this, directions.get(direction));
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

		public Hex offset(Hex h) {
			return add(size, this, h);
		}

		public Hex offset(int q, int r, int s) {
			return add(size, this, new Hex(size, q, r, s));
		}

		public Hex subtract(Hex h) {
			return subtract(size, this, h);
		}

		public static Hex add(double size, Hex a, Hex b) {
			return new Hex(size, a.q + b.q, a.r + b.r, a.s + b.s);
		}

		public static Hex subtract(double size, Hex a, Hex b) {
			return new Hex(size, a.q - b.q, a.r - b.r, a.s - b.s);
		}

		@Override
		public int hashCode() {
			return (-q*17 ^ r*77) * s*37;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Hex) {
				Hex h = (Hex)o;
				return h.q == q && h.r == r && h.s == s;
			}
			return false;
		}

		@Override
		public String toString() {
			return q+","+r+","+s;
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

		@Override
		public String toString() {
			return q+", "+r+", "+s;
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

	public static final class Point {

		public final double x;
		public final double y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public Point translate(double dx, double dy) {
			return new Point(x+dx, y+dy);
		}

		public Point scale(double d) {
			return this.scale(d, d);
		}

		public Point scale(double sx, double sy) {
			return new Point(x*sx, y*sy);
		}

		public Point rotate(double r, int ox, int oz) {
			Vec3 ret = ReikaVectorHelper.rotateVector(Vec3.createVectorHelper(x-ox, 0, y-oz), 0, r, 0);
			double x2 = ox+ret.xCoord;
			double y2 = oz+ret.zCoord;
			return new Point(x2, y2);
		}

	}

	public static final class GridProperties {

		public final double minX;
		public final double maxX;
		public final double minY;
		public final double maxY;
		public final double sizeX;
		public final double sizeY;

		private GridProperties(HexGrid g) {
			double nx = Double.POSITIVE_INFINITY;
			double px = Double.NEGATIVE_INFINITY;
			double ny = Double.POSITIVE_INFINITY;
			double py = Double.NEGATIVE_INFINITY;

			for (Hex h : g.hexes.values()) {
				Point p = g.getHexLocation(h);
				nx = Math.min(nx, p.x);
				ny = Math.min(ny, p.y);
				px = Math.max(px, p.x);
				py = Math.max(py, p.y);
			}
			nx -= g.hexSize/2D;
			ny -= g.hexSize/2D;
			px += g.hexSize/2D;
			py += g.hexSize/2D;

			minX = nx;
			maxX = px;
			minY = ny;
			maxY = py;

			sizeX = maxX-minX;
			sizeY = maxY-minY;
		}

	}

}

