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

public abstract class MathExpression {

	public abstract double evaluate(double arg) throws ArithmeticException;

	public abstract double getBaseValue();

	@Override
	public abstract String toString();

}
