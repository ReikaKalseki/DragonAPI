/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collections;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;


public class RenderPolygon {

	public final DecimalPosition pos1;
	public final DecimalPosition pos2;
	public final DecimalPosition pos3;

	public RenderPolygon(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
		this(new DecimalPosition(x1, y1, z1), new DecimalPosition(x2, y2, z2), new DecimalPosition(x3, y3, z3));
	}

	public RenderPolygon(DecimalPosition p1, DecimalPosition p2, DecimalPosition p3) {
		pos1 = p1;
		pos2 = p2;
		pos3 = p3;
	}

	@Override
	public String toString() {
		return pos1.toString()+" , "+pos2.toString()+", "+pos3.toString();
	}

	@Override
	public boolean equals(Object o) { //Sort here and not above, because that would wreck rendering
		if (o instanceof RenderPolygon) {
			RenderPolygon rp = (RenderPolygon)o;
			ArrayList<DecimalPosition> li = new ArrayList();
			li.add(pos1);
			li.add(pos2);
			li.add(pos3);
			Collections.sort(li);
			DecimalPosition p1 = li.get(0);
			DecimalPosition p2 = li.get(1);
			DecimalPosition p3 = li.get(2);

			li = new ArrayList();
			li.add(rp.pos1);
			li.add(rp.pos2);
			li.add(rp.pos3);
			Collections.sort(li);
			DecimalPosition p1b = li.get(0);
			DecimalPosition p2b = li.get(1);
			DecimalPosition p3b = li.get(2);

			return p1b.equals(p1) && p2b.equals(p2) && p3b.equals(p3);
		}
		return false;
	}

	public int locationHash() {
		return DecimalPosition.average(pos1, pos2, pos3).hashCode();
	}

	@Override
	public int hashCode() {
		return pos1.hashCode() + pos2.hashCode() + pos3.hashCode();
	}

}
