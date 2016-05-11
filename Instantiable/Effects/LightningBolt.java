/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class LightningBolt {

	public final int nsteps;

	public double variance = 1;
	public double velocity = 1;

	public final DecimalPosition start;
	public final DecimalPosition end;

	private final DecimalPosition[] middle;

	private final double[][] offsets;
	private final double[][] offsetTargets;

	public LightningBolt(DecimalPosition p1, DecimalPosition p2, int steps) {
		nsteps = steps;
		start = p1;
		end = p2;

		offsets = new double[nsteps+1][3];
		offsetTargets = new double[nsteps+1][3];

		middle = new DecimalPosition[nsteps+1];
		for (int i = 0; i < middle.length; i++) {
			double dx = p1.xCoord+(p2.xCoord-p1.xCoord)*i/nsteps;
			double dy = p1.yCoord+(p2.yCoord-p1.yCoord)*i/nsteps;
			double dz = p1.zCoord+(p2.zCoord-p1.zCoord)*i/nsteps;
			middle[i] = new DecimalPosition(dx, dy, dz);
		}
	}

	public void update() {
		for (int i = 1; i < nsteps; i++) {
			double dx = offsets[i][0];
			double dy = offsets[i][1];
			double dz = offsets[i][2];

			double tx = offsetTargets[i][0];
			double ty = offsetTargets[i][1];
			double tz = offsetTargets[i][2];

			if (ReikaMathLibrary.approxr(dx, tx, variance/8D)) {
				tx = ReikaRandomHelper.getRandomPlusMinus(0, variance);
			}
			if (ReikaMathLibrary.approxr(dy, ty, variance/8D)) {
				ty = ReikaRandomHelper.getRandomPlusMinus(0, variance);
			}
			if (ReikaMathLibrary.approxr(dz, tz, variance/8D)) {
				tz = ReikaRandomHelper.getRandomPlusMinus(0, variance);
			}

			if (tx > dx) {
				dx += velocity;
			}
			else if (tx < dx) {
				dx -= velocity;
			}

			if (ty > dy) {
				dy += velocity;
			}
			else if (ty < dy) {
				dy -= velocity;
			}

			if (tz > dz) {
				dz += velocity;
			}
			else if (tz < dz) {
				dz -= velocity;
			}

			offsets[i][0] = dx;
			offsets[i][1] = dy;
			offsets[i][2] = dz;

			offsetTargets[i][0] = tx;
			offsetTargets[i][1] = ty;
			offsetTargets[i][2] = tz;
		}
	}

	public DecimalPosition getPosition(int n) {
		return new DecimalPosition(middle[n].xCoord+offsets[n][0], middle[n].yCoord+offsets[n][1], middle[n].zCoord+offsets[n][2]);
	}
}
