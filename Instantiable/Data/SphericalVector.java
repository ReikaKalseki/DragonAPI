/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

public class SphericalVector {

	public double magnitude;
	public double inclination;
	public double rotation;

	public SphericalVector(double m, double theta, double phi) {
		magnitude = m;
		inclination = theta;
		rotation = phi;
	}

	public static SphericalVector fromCartesian(double dx, double dy, double dz) {
		double[] dat = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
		return new SphericalVector(dat[0], dat[1], dat[2]);
	}

	public double[] getCartesian() {
		return ReikaPhysicsHelper.polarToCartesian(magnitude, inclination, rotation);
	}

	public double getXProjection() {
		return this.getCartesian()[0];
	}

	public double getYProjection() {
		return this.getCartesian()[1];
	}

	public double getZProjection() {
		return this.getCartesian()[2];
	}

	public void aimFrom(double x1, double y1, double z1, double x2, double y2, double z2) {
		double[] dat = ReikaPhysicsHelper.cartesianToPolar(x2-x1, y2-y1, z2-z1);
		magnitude = dat[0];
		inclination = -(dat[1]-90);
		rotation = -dat[2]-90;
	}

}
