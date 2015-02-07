/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.input.Mouse;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton.TextAlign;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class GuiPainter {

	private final PaintElement[][] data;

	public final int posX;
	public final int posY;

	private final int pixelSize;
	public PaintElement activeElement;
	private MultiMap<PaintElement, Point> locations = new MultiMap();
	public Brush brush = Brush.PIXEL;

	private static final Random rand = new Random();

	public GuiPainter(int x, int y, int w, int h, int s) {
		data = new PaintElement[w][h];
		pixelSize = s;
		posX = x;
		posY = y;
	}

	public void onRenderTick(int mx, int my) {
		if (activeElement != null && Mouse.isButtonDown(0)) {
			if (mx >= posX && my >= posY && mx < posX+data.length*pixelSize && my < posY+data[0].length*pixelSize) {
				int x = mx-posX;
				int y = my-posY;
				Collection<Point> c = brush.getFill();
				for (Point p : c) {
					int dx = x+p.x;
					int dy = y+p.y;
					if (dx >= 0 && dy >= 0 && dx < data.length && dy < data[dx].length) {
						this.put(dx, dy, activeElement);
						activeElement.onPaintedTo(dx, dy);
					}
				}
			}
		}
	}

	public void draw() {
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < data[i].length; k++) {
				PaintElement p = data[i][k];
				if (p != null) {
					int dx = posX+i*pixelSize;
					int dy = posY+k*pixelSize;
					p.draw(dx, dy, pixelSize);
				}
			}
		}
		ReikaGuiAPI.instance.drawRectFrame(posX, posY, data.length*pixelSize, data[0].length*pixelSize, 0xffffff);
	}

	public void put(int dx, int dy, PaintElement p) {
		PaintElement prev = data[dx][dy];
		if (this.canReplace(prev, p)) {
			Collection c1 = locations.get(prev);
			c1.remove(new Point(dx, dy));
			if (c1.isEmpty())
				locations.remove(prev);
			else
				locations.put(prev, c1);
			data[dx][dy] = p;
			Collection c2 = locations.get(p);
			c2.add(new Point(dx, dy));
			locations.put(p, c2);
		}
	}

	private boolean canReplace(PaintElement prev, PaintElement p) {
		return p == null || prev == null || p.isPaintable(prev);
	}

	public void drawLegend(int x, int y) {
		this.drawLegend(Minecraft.getMinecraft().fontRenderer, x, y);
	}

	public void drawLegend(FontRenderer f, int x, int y) {
		int dy = y;
		for (PaintElement p : locations.keySet()) {
			int s = 7;
			p.draw(x, dy, s);
			ReikaGuiAPI.instance.drawRectFrame(x, dy, s, s, 0xffffff);
			f.drawString(p.getName(), x+s+2, dy, 0xffffff);
			dy += f.FONT_HEIGHT+4;
		}
	}

	public static interface PaintElement {

		public abstract void draw(int x, int y, int s);

		public abstract String getName();

		public abstract boolean isPaintable(PaintElement original);

		public abstract void onPaintedTo(int x, int y);

	}

	public static enum Brush {
		PIXEL("1x1 Pixel"),
		X2("2x2 Square"),
		X3("3x3 Square"),
		DOT("4x4 Dot"),
		CROSS("3x3 '+'"),
		SQUARE("5x5 Square"),
		CIRCLE("5x5 Circle"),
		SPRAY("Random Spray");

		public final String name;

		public static final Brush[] brushList = values();

		private Brush(String n) {
			name = n;
		}

		public GuiButton getButton(int id, int x, int y) {
			ImagedGuiButton b = new ImagedGuiButton(id, x, y, 16, 16, (this.ordinal()%4)*16, (this.ordinal()/4)*16, name, 0xffffff, false, "Resources/brushes.png", DragonAPICore.class);
			b.alignment = TextAlign.LEFT;
			b.textOffset = 18;
			b.textureSize = 64;
			return b;
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
				c.add(new Point(-1, 0));
				c.add(new Point(-1, 1));
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
