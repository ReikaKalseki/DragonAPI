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

public abstract class MathExpression {

	public abstract double evaluate(double arg) throws ArithmeticException;

	public abstract double getBaseValue();

	@Override
	public abstract String toString();

	public static final MathExpression self = new MathExpression() {
		@Override
		public double evaluate(double arg) throws ArithmeticException {
			return arg;
		}
		@Override
		public double getBaseValue() {
			return 0;
		}
		@Override
		public String toString() {
			return "<self>";
		}
	};

}
