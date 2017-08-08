/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Instantiable.Data.Maps.ThresholdMapping;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class Interpolation {

	private final ThresholdMapping<Double> data = new ThresholdMapping();

	private final boolean isColor;

	public boolean cosInterpolate = false;

	/** "Is this for color mixing or simple interpolation" */
	public Interpolation(boolean iscolor) {
		isColor = iscolor;
	}

	public Interpolation addPoint(double c, double val) {
		data.addMapping(c, val);
		return this;
	}

	public double getValue(double key) {
		Double x1 = data.getKeyForValue(key, false);
		Double x2 = data.getKeyForValue(key, true);
		Double d1 = data.getForValue(key, false);
		Double d2 = data.getForValue(key, true);
		if (x1 == x2) {
			return d1;
		}
		else if (x1 == null) {
			return d2;
		}
		else if (x2 == null) {
			return d1;
		}
		float f = (float)((key-x1)/(x2-x1));
		return isColor ? ReikaColorAPI.mixColors(d2.intValue(), d1.intValue(), f) : (cosInterpolate ? ReikaMathLibrary.cosInterpolation(key, x1, x2, d1, d2) : ReikaMathLibrary.linterpolate(key, x1, x2, d1, d2));
	}

}
