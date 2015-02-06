package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.lwjgl.input.Mouse;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class GuiPainter {

	private final PaintElement[][] data;

	private final int pixelSize;
	private PaintElement active;
	private Brush brush = Brush.PIXEL;

	private static final Random rand = new Random();

	public GuiPainter(int w, int h, int s) {
		data = new PaintElement[w][h];
		pixelSize = s;
	}

	public void onRenderTick(int mx, int my) {
		if (active != null && Mouse.isButtonDown(0)) {
			int x = 0;
			int y = 0;
			Collection<Point> c = brush.getFill();
			for (Point p : c) {
				int dx = x+p.x;
				int dy = y+p.y;
				this.put(dx, dy, active);
			}
		}
	}

	public void draw(int x, int y) {
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < data[i].length; k++) {
				PaintElement p = data[i][k];
				if (p != null) {
					int dx = x+i*pixelSize;
					int dy = y+k*pixelSize;
					p.draw(dx, dy, pixelSize);
				}
			}
		}
	}

	public void put(int dx, int dy, PaintElement p) {
		data[dx][dy] = p;
	}

	public static abstract class PaintElement {

		protected abstract void draw(int x, int y, int s);

	}

	public static enum Brush {
		PIXEL(),
		X2(),
		X3(),
		DOT(),
		CROSS(),
		SQUARE(),
		CIRCLE(),
		SPRAY();

		private Brush() {

		}

		public Collection<Point> getFill() {
			Collection<Point> c = new ArrayList();
			int r = 2;
			switch(this) {
			case CIRCLE:
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if (ReikaMathLibrary.py3d(i, 0, k) <= r+0.5)
							c.add(new Point(i, k));
					}
				}
				break;
			case DOT:
				c.add(new Point(0, -1));
				c.add(new Point(1, -1));
				c.add(new Point(0, 0));
				c.add(new Point(1, 0));
				c.add(new Point(0, 1));
				c.add(new Point(1, 1));
				c.add(new Point(2, 0));
				c.add(new Point(2, 1));
				c.add(new Point(-2, 0));
				c.add(new Point(-2, 1));
				c.add(new Point(0, 2));
				c.add(new Point(1, 2));
				break;
			case PIXEL:
				c.add(new Point(0, 0));
				break;
			case SPRAY:
				int n = 5+rand.nextInt(4);
				for (int i = 0; i < n; i++) {
					c.add(new Point(ReikaRandomHelper.getRandomPlusMinus(0, 4), ReikaRandomHelper.getRandomPlusMinus(0, 4)));
				}
				break;
			case SQUARE:
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						c.add(new Point(i, k));
					}
				}
				break;
			case CROSS:
				c.add(new Point(0, 0));
				c.add(new Point(1, 0));
				c.add(new Point(-1, 0));
				c.add(new Point(0, 1));
				c.add(new Point(0, -1));
				break;
			case X2:
				c.add(new Point(0, 0));
				c.add(new Point(1, 0));
				c.add(new Point(0, 1));
				c.add(new Point(1, 1));
				break;
			case X3:
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						c.add(new Point(i, k));
					}
				}
				break;
			}
			return c;
		}
	}

}
