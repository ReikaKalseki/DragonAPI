/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula;


public class PowerExpression extends MathExpression {

	private final double baseVal;
	private final double scale;
	private final double power;

	public PowerExpression(double init, double scale, double power) {
		baseVal = init;
		this.scale = scale;
		this.power = power;
	}

	@Override
	public double evaluate(double arg) {
		return baseVal + scale*Math.pow(arg, power);
	}

	@Override
	public double getBaseValue() {
		return baseVal;
	}

	@Override
	public String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*base^"+power;
	}
}
