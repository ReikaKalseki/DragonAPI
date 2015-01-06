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

public abstract class MathExpression {

	public abstract double evaluate(double arg);

	public abstract double getBaseValue();

	@Override
	public abstract String toString();

}
