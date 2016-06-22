/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;


public class DynamicAverage {

	private final Collection<Double> values = new ArrayList();
	private double sum;

	public double getAverage() {
		return sum/values.size();
	}

	public void add(double val) {
		sum += val;
		values.add(val);
	}

	public void clear() {
		values.clear();
		sum = 0;
	}

}
