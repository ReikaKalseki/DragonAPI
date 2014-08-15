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


public class InverseExpression extends MathExpression {

	private final double baseVal;
	private final double scale;

	public InverseExpression(double init, double scale) {
		baseVal = init;
		this.scale = scale;
	}

	@Override
	public double evaluate(double arg) {
		return baseVal + scale/arg;
	}

	@Override
	public double getBaseValue() {
		return baseVal;
	}

	@Override
	public String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"/x";
	}
}