/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraft.util.Vec3;

import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;


public class Ray {

	public final DecimalPosition origin;
	public final Vec3 directionStep;

	public Ray(DecimalPosition c, double dx, double dy, double dz) {
		this(c, Vec3.createVectorHelper(dx, dy, dz));
	}

	public static Ray fromPolar(DecimalPosition c, double theta, double phi) {
		double[] xyz = ReikaPhysicsHelper.polarToCartesian(1, theta, phi);
		return new Ray(c, xyz[0], xyz[1], xyz[2]);
	}

	public Ray(DecimalPosition c, Vec3 vec) {
		origin = c;
		directionStep = vec;
	}

	public DecimalPosition getScaledPosition(double d) {
		return origin.offset(directionStep.xCoord*d, directionStep.yCoord*d, directionStep.zCoord*d);
	}

	@Override
	public String toString() {
		return origin+" > "+directionStep.toString();
	}

}
