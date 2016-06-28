/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.formula;

import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;

public class LogarithmExpression extends MathExpression {

	public final double baseVal;
	public final double scale;
	public final double logBase;

	public LogarithmExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		logBase = base;
	}

	@Override
	public final double evaluate(double arg) throws ArithmeticException {
		return baseVal + scale*ReikaMathLibrary.logbase(arg+1, logBase);
	}

	@Override
	public final double getBaseValue() {
		return baseVal;
	}

	@Override
	public final String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*log_"+logBase+"(x+1)";
	}
}
