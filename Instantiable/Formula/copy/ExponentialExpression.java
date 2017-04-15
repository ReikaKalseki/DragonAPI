/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula.copy;


public class ExponentialExpression extends MathExpression {

	public final double baseVal;
	public final double scale;
	public final double base;

	public ExponentialExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		this.base = base;
	}

	@Override
	public final double evaluate(double arg) throws ArithmeticException {
		return baseVal + scale*Math.pow(base, arg);
	}

	@Override
	public final double getBaseValue() {
		return baseVal;
	}

	@Override
	public final String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*"+base+"^x";
	}
}
