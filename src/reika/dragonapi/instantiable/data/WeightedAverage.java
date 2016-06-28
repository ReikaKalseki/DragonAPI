/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data;

import java.util.ArrayList;
import java.util.Collection;


public class WeightedAverage {

	private final Collection<Average> data = new ArrayList();

	private double totalWeight = 0;

	public WeightedAverage() {

	}

	public void addValue(double val, double wt) {
		data.add(new Average(val, wt));
		totalWeight += wt;
	}

	public double getAverageValue() {
		double val = 0;
		for (Average a : data) {
			val += a.value*a.weight/totalWeight;
		}
		return val;
	}

	private static class Average {

		private final double value;
		private final double weight;

		private Average(double v, double w) {
			value = v;
			weight = w;
		}

	}
}
