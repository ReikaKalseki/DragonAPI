/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import java.util.List;
import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class LightningBolt {

	public final int nsteps;

	private double varianceX = 1;
	private double varianceY = 1;
	private double varianceZ = 1;
	private double velocityX = 1;
	private double velocityY = 1;
	private double velocityZ = 1;

	public final DecimalPosition start;
	public final DecimalPosition end;

	private final DecimalPosition[] middle;

	private final double[][] offsets;
	private final double[][] offsetTargets;

	private Random rand = new Random();

	public LightningBolt(double x1, double y1, double z1, double x2, double y2, double z2, int steps) {
		this(new DecimalPosition(x1, y1, z1), new DecimalPosition(x2, y2, z2), steps);
	}

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

	public LightningBolt maximize() {
		for (int i = 1; i < nsteps; i++) {
			double tx = ReikaRandomHelper.getRandomPlusMinus(0, varianceX, rand);
			double ty = ReikaRandomHelper.getRandomPlusMinus(0, varianceY, rand);
			double tz = ReikaRandomHelper.getRandomPlusMinus(0, varianceZ, rand);
			offsets[i][0] = tx;
			offsets[i][1] = ty;
			offsets[i][2] = tz;
		}
		return this;
	}

	public void update() {
		for (int i = 1; i < nsteps; i++) {
			double dx = offsets[i][0];
			double dy = offsets[i][1];
			double dz = offsets[i][2];

			double tx = offsetTargets[i][0];
			double ty = offsetTargets[i][1];
			double tz = offsetTargets[i][2];

			if (ReikaMathLibrary.approxr(dx, tx, varianceX/8D)) {
				tx = ReikaRandomHelper.getRandomPlusMinus(0, varianceX, rand);
			}
			if (ReikaMathLibrary.approxr(dy, ty, varianceY/8D)) {
				ty = ReikaRandomHelper.getRandomPlusMinus(0, varianceY, rand);
			}
			if (ReikaMathLibrary.approxr(dz, tz, varianceZ/8D)) {
				tz = ReikaRandomHelper.getRandomPlusMinus(0, varianceZ, rand);
			}

			if (tx > dx) {
				dx += velocityX;
			}
			else if (tx < dx) {
				dx -= velocityX;
			}

			if (ty > dy) {
				dy += velocityY;
			}
			else if (ty < dy) {
				dy -= velocityY;
			}

			if (tz > dz) {
				dz += velocityZ;
			}
			else if (tz < dz) {
				dz -= velocityZ;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bolt [");
		for (int i = 0; i <= nsteps; i++) {
			sb.append(this.getPosition(i));
			if (i < nsteps) {
				sb.append(" > ");
			}
		}
		sb.append("];");
		return sb.toString();
	}

	public List<DecimalPosition> spline(SplineType type, int points) {
		Spline path = new Spline(type);
		for (int i = 0; i <= nsteps; i++) {
			path.addPoint(new BasicSplinePoint(this.getPosition(i)));
		}
		return path.get(points, false);
	}

	public LightningBolt setRandom(Random r) {
		rand = r;
		return this;
	}

	public LightningBolt setVariance(double v) {
		return this.setVariance(v, v, v);
	}

	public LightningBolt setVariance(double vx, double vy, double vz) {
		varianceX = vx;
		varianceY = vy;
		varianceZ = vz;
		return this;
	}

	public LightningBolt setVelocity(double v) {
		return this.setVelocity(v, v, v);
	}

	public LightningBolt setVelocity(double vx, double vy, double vz) {
		velocityX = vx;
		velocityY = vy;
		velocityZ = vz;
		return this;
	}

	public LightningBolt scaleVariance(double v) {
		return this.scaleVariance(v, v, v);
	}

	public LightningBolt scaleVariance(double vx, double vy, double vz) {
		varianceX *= vx;
		varianceY *= vy;
		varianceZ *= vz;
		return this;
	}

	public LightningBolt scaleVelocity(double v) {
		return this.scaleVelocity(v, v, v);
	}

	public LightningBolt scaleVelocity(double vx, double vy, double vz) {
		velocityX *= vx;
		velocityY *= vy;
		velocityZ *= vz;
		return this;
	}

	public LightningBolt setVelocity(LightningBolt b) {
		return this.setVelocity(b.velocityX, b.velocityY, b.velocityZ);
	}

	public LightningBolt setVariance(LightningBolt b) {
		return this.setVariance(b.varianceX, b.varianceY, b.varianceZ);
	}
}
