/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Math;

import java.awt.Point;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class LineClipper {

	private static final int INSIDE = 0; // 0000
	private static final int LEFT = 1;   // 0001
	private static final int RIGHT = 2;  // 0010
	private static final int BOTTOM = 4; // 0100
	private static final int TOP = 8;    // 1000

	// Compute the bit code for a point (x, y) using the clip rectangle
	// bounded diagonally by (minX, minY), and (maxX, maxY)

	// ASSUME THAT maxX, minX, maxY and minY are global constants.

	public final int minX;
	public final int minY;
	public final int maxX;
	public final int maxY;

	public LineClipper(int x0, int y0, int x1, int y1) {
		minX = x0;
		minY = y0;
		maxX = x1;
		maxY = y1;
	}

	private int ComputeOutCode(int x, int y) {
		int code = INSIDE;

		if (x < minX)           // to the left of clip window
			code |= LEFT;
		else if (x > maxX)      // to the right of clip window
			code |= RIGHT;

		if (y < minY)           // below the clip window
			code |= BOTTOM;
		else if (y > maxY)      // above the clip window
			code |= TOP;

		return code;
	}

	// Cohen–Sutherland clipping algorithm clips a line from
	// P0 = (x0, y0) to P1 = (x1, y1) against a rectangle with
	// diagonal from (minX, minY) to (maxX, maxY).
	public ImmutablePair<Point, Point> clip(int x0, int y0, int x1, int y1) {
		// compute outcodes for P0, P1, and whatever point lies outside the clip rectangle
		int outcode0 = this.ComputeOutCode(x0, y0);
		int outcode1 = this.ComputeOutCode(x1, y1);

		while (true) {
			if ((outcode0 | outcode1) == 0) { // Bitwise OR is 0. Trivially accept and get out of loop
				return new ImmutablePair(new Point(x0, y0), new Point(x1, y1));
			}
			else if ((outcode0 & outcode1) != 0) { // Bitwise AND is not 0. Trivially reject and get out of loop
				return null;
			}
			else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				int x = x0;
				int y = y0;

				// At least one endpoint is outside the clip rectangle; pick it.
				int outcodeOut = outcode0 != 0 ? outcode0 : outcode1;

				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope) * (y - y0)
				if ((outcodeOut & TOP) != 0) {           // point is above the clip rectangle
					x = x0 + (x1 - x0) * (maxY - y0) / (y1 - y0);
					y = maxY;
				}
				else if ((outcodeOut & BOTTOM) != 0) { // point is below the clip rectangle
					x = x0 + (x1 - x0) * (minY - y0) / (y1 - y0);
					y = minY;
				}
				else if ((outcodeOut & RIGHT) != 0) {  // point is to the right of clip rectangle
					y = y0 + (y1 - y0) * (maxX - x0) / (x1 - x0);
					x = maxX;
				}
				else if ((outcodeOut & LEFT) != 0) {   // point is to the left of clip rectangle
					y = y0 + (y1 - y0) * (minX - x0) / (x1 - x0);
					x = minX;
				}

				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0) {
					x0 = x;
					y0 = y;
					outcode0 = this.ComputeOutCode(x0, y0);
				}
				else {
					x1 = x;
					y1 = y;
					outcode1 = this.ComputeOutCode(x1, y1);
				}
			}
		}
	}
}
