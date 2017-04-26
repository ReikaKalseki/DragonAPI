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
