/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import java.util.Random;

import reika.dragonapi.instantiable.data.WeightedRandom;
import reika.dragonapi.libraries.java.ReikaRandomHelper;

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
