/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collections;

import Reika.DragonAPI.Instantiable.Data.Maps.CountMap;


public class Statistics {

	private double mean;
	private double mode;
	private double median;
	private double sum;

	private final CountMap<Double> counts = new CountMap();
	private final ArrayList<Double> values = new ArrayList();

	public Statistics() {

	}

	public Statistics addValue(double val) {
		values.add(val);
		counts.increment(val);

		Collections.sort(values);

		sum += val;
		mean = sum/values.size();

		if (val != mode) {
			if (counts.get(val) > counts.get(mode)) {
				mode = val;
			}
		}

		median = values.size()%2 == 0 ? (values.get(values.size()/2)+values.get(values.size()/2-1))/2D : values.get(values.size()/2);

		return this;
	}

	public double getMean() {
		return mean;
	}

	public double getMedian() {
		return median;
	}

	public double getMode() {
		return mode;
	}

	public double getSum() {
		return sum;
	}

}
