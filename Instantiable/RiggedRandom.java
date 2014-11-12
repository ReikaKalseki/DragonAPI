package Reika.DragonAPI.Instantiable;

import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class RiggedRandom extends Random {

	private final WeightedRandom<Integer> rand = new WeightedRandom();
	private double bioff = 0;
	private double birange = 0;
	private double scale = 1;

	public static final int DEFAULT_WEIGHT = 50;

	public RiggedRandom() {

	}

	public RiggedRandom setIntBias(int val, int weight) {
		rand.addEntry(val, weight);
		return this;
	}

	public RiggedRandom setLinearBias(double offset, double range) {
		bioff = offset;
		birange = range;
		return this;
	}

	public RiggedRandom setScaling(double scale) {
		this.scale = scale;
		return this;
	}


	@Override
	protected int next(int bits) {
		int base = rand.getRandomEntry(super.next(bits), DEFAULT_WEIGHT);
		if (birange > 0) {
			base += ReikaRandomHelper.getRandomPlusMinus(bioff, birange);
		}
		base *= scale;
		return base;
	}

}
