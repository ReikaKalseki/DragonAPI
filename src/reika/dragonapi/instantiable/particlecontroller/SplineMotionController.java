/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.particlecontroller;

import java.util.List;

import net.minecraft.entity.Entity;
import reika.dragonapi.instantiable.Spline;
import reika.dragonapi.instantiable.Spline.BasicVariablePoint;
import reika.dragonapi.instantiable.Spline.SplineType;
import reika.dragonapi.instantiable.data.immutable.DecimalPosition;
import reika.dragonapi.interfaces.PositionController;

//incomplete
public class SplineMotionController implements PositionController {

	public final DecimalPosition pos1;
	public final DecimalPosition pos2;

	public final int entityLife;

	private int tick = 0;
	private boolean updateSpline = false;
	private final Spline spline;
	private List<DecimalPosition> points;

	public SplineMotionController(double x1, double y1, double z1, double x2, double y2, double z2, int l, Spline s) {
		pos1 = new DecimalPosition(x1, y1, z1);
		pos2 = new DecimalPosition(x2, y2, z2);

		entityLife = l;

		spline = s;
		points = spline.get(32, false);
	}

	public SplineMotionController(double x1, double y1, double z1, double x2, double y2, double z2, int l, double var, double vel) {
		pos1 = new DecimalPosition(x1, y1, z1);
		pos2 = new DecimalPosition(x2, y2, z2);

		entityLife = l;

		spline = new Spline(SplineType.CHORDAL);
		for (double d = 0; d <= 1; d += 0.125) {
			BasicVariablePoint p = new BasicVariablePoint(DecimalPosition.interpolate(pos1, pos2, d), var, vel);
			p.tolerance *= 0.03125;
			spline.addPoint(p);
		}
		updateSpline = true;
		points = spline.get(32, false);
	}

	@Override
	public void update(Entity e) {
		if (updateSpline)
			spline.update();
		points = spline.get(32, false);
		tick++;
	}

	private int getIndex(Entity e) {
		int t = tick*points.size()/entityLife;
		return Math.min(t, points.size()-1);
	}

	@Override
	public double getPositionX(Entity e) {
		return points.get(this.getIndex(e)).xCoord;
	}

	@Override
	public double getPositionY(Entity e) {
		return points.get(this.getIndex(e)).yCoord;
	}

	@Override
	public double getPositionZ(Entity e) {
		return points.get(this.getIndex(e)).zCoord;
	}

}
