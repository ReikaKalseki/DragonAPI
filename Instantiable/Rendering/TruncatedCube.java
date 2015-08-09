/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;


public class TruncatedCube {

	public double mainSize;
	public double cutSize;

	private List<DecimalPosition>[] faces;
	private List<ArrayList<DecimalPosition>> corners;

	public TruncatedCube(double s, double s2) {
		mainSize = s2;
		cutSize = s;
	}

	public TruncatedCube cache(boolean startCenter, double x0, double y0, double z0) {
		faces = new List[6];
		for (int i = 0; i < 6; i++) {
			faces[i] = Collections.unmodifiableList(this.getFaceVertices(ForgeDirection.VALID_DIRECTIONS[i], startCenter, x0, y0, z0));
		}
		corners = Collections.unmodifiableList(this.getCornerVertices(x0, y0, z0));
		return this;
	}

	public List<DecimalPosition> getFaceVertices(ForgeDirection face, boolean startCenter, double x0, double y0, double z0) {
		if (faces != null)
			return faces[face.ordinal()];

		ArrayList<DecimalPosition> li = new ArrayList();
		switch(face) {
			case DOWN:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0-mainSize, z0));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				break;
			case UP:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0+mainSize, z0));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				break;
			case WEST:
				if (startCenter)
					li.add(new DecimalPosition(x0-mainSize, y0, z0));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				break;
			case EAST:
				if (startCenter)
					li.add(new DecimalPosition(x0+mainSize, y0, z0));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				break;
			case SOUTH:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
				break;
			case NORTH:
				if (startCenter)
					li.add(new DecimalPosition(x0, y0, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
				li.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
				li.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
				break;
			default:
				break;
		}
		return li;
	}

	public List<ArrayList<DecimalPosition>> getCornerVertices(double x0, double y0, double z0) {
		if (corners != null)
			return corners;

		ArrayList<ArrayList<DecimalPosition>> li = new ArrayList();

		//top corners
		ArrayList<DecimalPosition> li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0-cutSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0-mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0-cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+cutSize, y0+mainSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0+mainSize, z0+cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-mainSize, y0+mainSize, z0+cutSize));
		li2.add(new DecimalPosition(x0-mainSize, y0+cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-cutSize, y0+mainSize, z0+mainSize));
		li.add(li2);

		//bottom corners
		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0-cutSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0-cutSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0-mainSize));
		li2.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0-mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0+mainSize, y0-mainSize, z0+cutSize));
		li2.add(new DecimalPosition(x0+mainSize, y0-cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0+cutSize, y0-mainSize, z0+mainSize));
		li.add(li2);

		li2 = new ArrayList();
		li2.add(new DecimalPosition(x0-cutSize, y0-mainSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-cutSize, z0+mainSize));
		li2.add(new DecimalPosition(x0-mainSize, y0-mainSize, z0+cutSize));
		li.add(li2);

		return li;
	}

}
