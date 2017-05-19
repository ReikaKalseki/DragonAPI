/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Math;

import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;


public class MovingAverage {

	private final double[] data;

	public MovingAverage(int dataPoints) {
		data = new double[dataPoints];
	}

	public MovingAverage addValue(double val) {
		ReikaArrayHelper.cycleArray(data, val);
		return this;
	}

	public double getAverage() {
		double avg = 0;
		for (int i = 0; i < data.length; i++) {
			avg += data[i];
		}
		return avg/data.length;
	}

}
