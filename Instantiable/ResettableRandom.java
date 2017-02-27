package Reika.DragonAPI.Instantiable;

import java.util.Random;


public class ResettableRandom extends Random {

	private long lastSeed;

	@Override
	synchronized public void setSeed(long seed) {
		super.setSeed(seed);

		lastSeed = seed;
	}

	public void resetSeed() {
		this.setSeed(lastSeed);
	}

}
