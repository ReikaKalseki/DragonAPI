package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Instantiable.Data.Maps.ThresholdMapping;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class Interpolation {

	private final ThresholdMapping<Double> data = new ThresholdMapping();

	public Interpolation() {

	}

	public void add(double c, double val) {
		data.addMapping(c, val);
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
		return ReikaColorAPI.mixColors(d2.intValue(), d1.intValue(), (float)((key-x1)/(x2-x1)));//ReikaMathLibrary.linterpolate(key, x1, x2, d1, d2);
	}

}
