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
import java.util.function.Function;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class LightningBolt {

	public final int nsteps;

	private final double[] varianceX;
	private final double[] varianceY;
	private final double[] varianceZ;
	private final double[] velocityX;
	private final double[] velocityY;
	private final double[] velocityZ;

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

		varianceX = new double[nsteps+1];
		varianceY = new double[nsteps+1];
		varianceZ = new double[nsteps+1];
		velocityX = new double[nsteps+1];
		velocityY = new double[nsteps+1];
		velocityZ = new double[nsteps+1];

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
			double tx = ReikaRandomHelper.getRandomPlusMinus(0, varianceX[i], rand);
			double ty = ReikaRandomHelper.getRandomPlusMinus(0, varianceY[i], rand);
			double tz = ReikaRandomHelper.getRandomPlusMinus(0, varianceZ[i], rand);
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

			if (ReikaMathLibrary.approxr(dx, tx, varianceX[i]/8D)) {
				tx = ReikaRandomHelper.getRandomPlusMinus(0, varianceX[i], rand);
			}
			if (ReikaMathLibrary.approxr(dy, ty, varianceY[i]/8D)) {
				ty = ReikaRandomHelper.getRandomPlusMinus(0, varianceY[i], rand);
			}
			if (ReikaMathLibrary.approxr(dz, tz, varianceZ[i]/8D)) {
				tz = ReikaRandomHelper.getRandomPlusMinus(0, varianceZ[i], rand);
			}

			if (tx > dx) {
				dx += velocityX[i];
			}
			else if (tx < dx) {
				dx -= velocityX[i];
			}

			if (ty > dy) {
				dy += velocityY[i];
			}
			else if (ty < dy) {
				dy -= velocityY[i];
			}

			if (tz > dz) {
				dz += velocityZ[i];
			}
			else if (tz < dz) {
				dz -= velocityZ[i];
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
		return this.setVariance(vx, vy, vz, null, null, null);
	}

	public LightningBolt setVariance(double vx, double vy, double vz, Function<Integer, Double> funcX, Function<Integer, Double> funcY, Function<Integer, Double> funcZ) {
		for (int i = 0; i < varianceX.length; i++) {
			varianceX[i] = vx;
			varianceY[i] = vy;
			varianceZ[i] = vz;
			if (funcX != null)
				varianceX[i] *= funcX.apply(i);
			if (funcY != null)
				varianceY[i] *= funcY.apply(i);
			if (funcZ != null)
				varianceZ[i] *= funcZ.apply(i);
		}
		return this;
	}

	public LightningBolt setVariance(double[] vx, double[] vy, double[] vz) {
		for (int i = 0; i < varianceX.length; i++) {
			varianceX[i] = vx[i];
			varianceY[i] = vy[i];
			varianceZ[i] = vz[i];
		}
		return this;
	}

	public LightningBolt setVelocity(double v) {
		return this.setVelocity(v, v, v);
	}

	public LightningBolt setVelocity(double vx, double vy, double vz) {
		return this.setVelocity(vx, vy, vz, null, null, null);
	}

	public LightningBolt setVelocity(double vx, double vy, double vz, Function<Integer, Double> funcX, Function<Integer, Double> funcY, Function<Integer, Double> funcZ) {
		for (int i = 0; i < varianceX.length; i++) {
			velocityX[i] = vx;
			velocityY[i] = vy;
			velocityZ[i] = vz;
			if (funcX != null)
				velocityX[i] *= funcX.apply(i);
			if (funcY != null)
				velocityY[i] *= funcY.apply(i);
			if (funcZ != null)
				velocityZ[i] *= funcZ.apply(i);
		}
		return this;
	}

	public LightningBolt setVelocity(double[] vx, double[] vy, double[] vz) {
		for (int i = 0; i < velocityX.length; i++) {
			velocityX[i] = vx[i];
			velocityY[i] = vy[i];
			velocityZ[i] = vz[i];
		}
		return this;
	}

	public LightningBolt scaleVariance(double v) {
		return this.scaleVariance(v, v, v);
	}

	public LightningBolt scaleVariance(double vx, double vy, double vz) {
		for (int i = 0; i < varianceX.length; i++) {
			varianceX[i] *= vx;
			varianceY[i] *= vy;
			varianceZ[i] *= vz;
		}
		return this;
	}

	public LightningBolt scaleVelocity(double v) {
		return this.scaleVelocity(v, v, v);
	}

	public LightningBolt scaleVelocity(double vx, double vy, double vz) {
		for (int i = 0; i < varianceX.length; i++) {
			velocityX[i] *= vx;
			velocityY[i] *= vy;
			velocityZ[i] *= vz;
		}
		return this;
	}

	public LightningBolt setVelocity(LightningBolt b) {
		return this.setVelocity(b.velocityX, b.velocityY, b.velocityZ);
	}

	public LightningBolt setVariance(LightningBolt b) {
		return this.setVariance(b.varianceX, b.varianceY, b.varianceZ);
	}
}
