/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.HashMap;


public class ExtremaFinder<V> {

	private final HashMap<V, Double> values = new HashMap();

	private V lowest;
	private V highest;
	private double lowestVal;
	private double highestVal;

	public ExtremaFinder() {

	}

	public void addValue(V obj, double value) {
		values.put(obj, value);
		if (value > this.highestVal || highest == null) {
			this.highest = obj;
			this.highestVal = value;
		}
		if (value < this.lowestVal || this.lowest == null) {
			this.lowest = obj;
			this.lowestVal = value;
		}
	}

	public V getHighest() {
		return this.highest;
	}

	public V getLowest() {
		return this.lowest;
	}

	public double getValue(V obj) {
		return this.values.get(obj);
	}

}
