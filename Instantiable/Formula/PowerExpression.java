/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula;


public class PowerExpression extends MathExpression {

	public final double baseVal;
	public final double scale;
	public final double power;

	public PowerExpression(double init, double scale, double power) {
		baseVal = init;
		this.scale = scale;
		this.power = power;
	}

	@Override
	public final double evaluate(double arg) throws ArithmeticException {
		return baseVal + scale*Math.pow(arg, power);
	}

	@Override
	public final double getBaseValue() {
		return baseVal;
	}

	@Override
	public final String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*base^"+power;
	}
}
