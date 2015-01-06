/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class LogarithmExpression extends MathExpression {

	private final double baseVal;
	private final double scale;
	private final double logBase;

	public LogarithmExpression(double init, double scale, double base) {
		baseVal = init;
		this.scale = scale;
		logBase = base;
	}

	@Override
	public double evaluate(double arg) {
		return baseVal + scale*ReikaMathLibrary.logbase(arg+1, logBase);
	}

	@Override
	public double getBaseValue() {
		return baseVal;
	}

	@Override
	public String toString() {
		return baseVal+(scale > 0 ? "+" : "-")+Math.abs(scale)+"*log_"+logBase+"(x+1)";
	}
}
