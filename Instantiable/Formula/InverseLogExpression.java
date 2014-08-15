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

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class InverseLogExpression extends MathExpression {

	private final double baseVal;
	private final double scale;
	private final double base;

	public InverseLogExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		this.base = base;
	}

	@Override
	public double evaluate(double arg) {
		return baseVal/(1+scale*(ReikaMathLibrary.logbase(arg+1, base)));
	}

	@Override
	public double getBaseValue() {
		return baseVal;
	}

	@Override
	public String toString() {
		return baseVal+"/(1+"+scale+"*(log_"+base+"(x+1)))";
	}

}