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

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class InverseLogExpression extends MathExpression {

	public final double baseVal;
	public final double scale;
	public final double base;

	public InverseLogExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		this.base = base;
	}

	@Override
	public final double evaluate(double arg) throws ArithmeticException {
		return baseVal/(1+scale*(ReikaMathLibrary.logbase(arg+1, base)));
	}

	@Override
	public final double getBaseValue() {
		return baseVal;
	}

	@Override
	public final String toString() {
		return baseVal+"/(1+"+scale+"*(log_"+base+"(x+1)))";
	}

}
