/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * Unless given explicit written permission - electronic writing is acceptable - no user may
 * copy, edit, or redistribute this source code nor any derivative works.
 * Failure to comply with these restrictions is a violation of
 * copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI;

import java.awt.Color;

import net.minecraft.client.renderer.Tessellator;
import Reika.DragonAPI.Libraries.ReikaRenderHelper;

public class PixelRenderer {

	private boolean xy;
	private double x,y,z;

	private double pw;
	private double ph;

	private boolean flip;
	private boolean mirror;

	private int[] rgba = new int[4];

	/** Args: Position x,y,z, XY plane (false for ZY), Image width in pixels, Image height in pixels, mapped width, height, flip yes/no */
	public PixelRenderer(double px, double py, double pz, boolean plane, int wx, int wy, double w, double h, boolean f) {
		x = px;
		y = py;
		z = pz;
		xy = plane;

        pw = w/wx;
        ph = -h/wy;

        flip = f;
	}

	public void setPosition(double x0, double y0, double z0) {
		x = x0;
		y = y0;
		z = z0;
	}

	public void setFlip(boolean f) {
		flip = f;
	}

	public void setMirror(boolean m) {
		mirror = m;
	}

	public void setPlane(boolean plane) {
		xy = plane;
	}

	public void draw(int x1, int y1, int x2, int y2) {
		if (mirror) {

		}
		ReikaRenderHelper.prepareGeoDraw(rgba[3] < 255);
		Tessellator v5 = new Tessellator();
		if (flip) {
			if (xy) {
				v5.startDrawingQuads();
				v5.setColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
				v5.addVertex(x+x1*pw, y+ph+y2*ph, z);
				v5.addVertex(x+pw+x2*pw, y+ph+y2*ph, z);
				v5.addVertex(x+pw+x2*pw, y+y1*ph, z);
				v5.addVertex(x+x1*pw, y+y1*ph, z);
				v5.draw();
			}
			else {
				v5.startDrawingQuads();
				v5.setColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
				v5.addVertex(x, y+ph+y2*ph, z+x1*pw);
				v5.addVertex(x, y+ph+y2*ph, z+pw+x2*pw);
				v5.addVertex(x, y+y1*ph, z+pw+x2*pw);
				v5.addVertex(x, y+y1*ph, z+x1*pw);
				v5.draw();
			}
		}
		else {
			if (xy) {
				v5.startDrawingQuads();
				v5.setColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
				v5.addVertex(x+x1*pw, y+y1*ph, z);
				v5.addVertex(x+pw+x2*pw, y+y1*ph, z);
				v5.addVertex(x+pw+x2*pw, y+ph+y2*ph, z);
				v5.addVertex(x+x1*pw, y+ph+y2*ph, z);
				v5.draw();
			}
			else {
				v5.startDrawingQuads();
				v5.setColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
				v5.addVertex(x, y+y1*ph, z+x1*pw);
				v5.addVertex(x, y+y1*ph, z+pw+x2*pw);
				v5.addVertex(x, y+ph+y2*ph, z+pw+x2*pw);
				v5.addVertex(x, y+ph+y2*ph, z+x1*pw);
				v5.draw();
			}
		}
		ReikaRenderHelper.exitGeoDraw();
	}

	public void setColor(int r, int g, int b, int a) {
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}

	public void setColor(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		int a = c.getAlpha();
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}

}
