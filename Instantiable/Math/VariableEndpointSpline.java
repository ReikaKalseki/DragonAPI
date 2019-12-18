package Reika.DragonAPI.Instantiable.Math;

import java.util.ArrayList;
import java.util.List;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicVariablePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;

public class VariableEndpointSpline {

	public final DecimalPosition start;
	private final Spline curve;
	private final int pointCount;

	private DecimalPosition endpoint;
	private final ArrayList<DecimalPosition> roots = new ArrayList();
	private final ArrayList<BasicVariablePoint> points = new ArrayList();

	public VariableEndpointSpline(double x, double y, double z, double x2, double y2, double z2, SplineType t, int points, double variance, double velocity) {
		this(new DecimalPosition(x, y, z), new DecimalPosition(x2, y2, z2), t, points, variance, velocity);
	}

	public VariableEndpointSpline(DecimalPosition p, DecimalPosition p2, SplineType t, int points, double variance, double velocity) {
		curve = new Spline(t);
		pointCount = points;
		start = p;
		for (int i = 0; i <= pointCount; i++) {
			BasicVariablePoint a = new BasicVariablePoint(new DecimalPosition(0, 0, 0), variance, velocity);
			a.tolerance = 0.0625;
			this.points.add(a);
			curve.addPoint(a);
		}
		this.setEndpoint(p2);
	}

	public void setEndpoint(double x, double y, double z) {
		this.setEndpoint(new DecimalPosition(x, y, z));
	}

	public void setEndpoint(DecimalPosition p) {
		if (endpoint != null && endpoint.equals(p))
			return;
		endpoint = new DecimalPosition(p);
		this.calculateLine();
	}

	private void calculateLine() {
		roots.clear();
		for (int i = 0; i <= pointCount; i++) {
			float f = i/(float)pointCount;
			DecimalPosition r = DecimalPosition.interpolate(start, endpoint, f);
			roots.add(r);
			BasicVariablePoint b = points.get(i);
			b.setRelativeTo(r);
		}
	}

	public void tick() {
		for (int i = 0; i <= pointCount; i++) {
			BasicVariablePoint p = points.get(i);
			if (i == 0) {
				p.posX = p.posY = p.posZ = 0;
			}/*
			else if (i == pointCount) {
				p.posX = endpoint.xCoord-start.xCoord;
				p.posY = endpoint.yCoord-start.yCoord;
				p.posZ = endpoint.zCoord-start.zCoord;
			}*/
			else
				p.update();
		}
	}

	public List<DecimalPosition> getPoints(int fineness) {
		return curve.get(fineness, false);
	}

}
