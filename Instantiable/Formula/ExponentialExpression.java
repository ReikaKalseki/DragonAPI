/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula;


public class ExponentialExpression extends MathExpression {

	private final double baseVal;
	private final double scale;
	private final double base;

	public ExponentialExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		this.base = base;
	}

	@Override
	public double evaluate(double arg) {
		return baseVal + scale*Math.pow(base, arg);
	}

	@Override
	public double getBaseValue() {
		return baseVal;
	}

	@Override
	public String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*"+base+"^x";
	}
}