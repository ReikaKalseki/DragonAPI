/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class RandomVariance {

	public final double variance;
	public final double velocity;
	public final double tolerance;

	private double value;
	private double target;

	public RandomVariance(double var, double vel, double tol) {
		variance = var;
		velocity = vel;
		tolerance = tol;
	}

	public void update() {
		if (ReikaMathLibrary.approxr(value, target, tolerance)) {
			target = ReikaRandomHelper.getRandomPlusMinus(0, variance);
		}
		value += velocity*(target-value)/16D;
	}

	public double getValue() {
		return value;
	}

}
