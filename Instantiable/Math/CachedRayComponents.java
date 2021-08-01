package Reika.DragonAPI.Instantiable.Math;


public class CachedRayComponents {

	private double angle;

	private double dx;
	private double dy;
	private double dxL;
	private double dyL;

	public CachedRayComponents(double angle) {
		this.angle = angle-1;
		this.setAngle(angle);
	}

	public void setAngle(double angle) {
		if (angle == this.angle)
			return;
		this.angle = angle;
		dx = Math.cos(Math.toRadians(angle));
		dy = Math.sin(Math.toRadians(angle));
		dxL = Math.cos(Math.toRadians(angle+90));
		dyL = Math.sin(Math.toRadians(angle+90));
	}

	public double getAngle() {
		return angle;
	}

	public double getDX() {
		return dx;
	}

	public double getDZ() {
		return dy;
	}

	public double getPerpendicularDX() {
		return dxL;
	}

	public double getPerpendicularDZ() {
		return dyL;
	}

}
