/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import Reika.DragonAPI.DragonAPICore;

public class ReikaRandomHelper extends DragonAPICore {

	/** Like rand.getInt(), but will not crash for n <= 0 */
	public static int getSafeRandomInt(int val) {
		if (val <= 1)
			return 0;
		return rand.nextInt(val);
	}

	/** Gets a random double value within base +/- range. */
	public static double getRandomPlusMinus(double base, double range) {
		double add = -range+rand.nextDouble()*(range*2+1);
		return (base+add);
	}

	/** Gets a random integer value within base +/- range. */
	public static int getRandomPlusMinus(int base, int range) {
		int add = -range+rand.nextInt(range*2+1);
		return base+add;
	}

	/** Returns true with a percentage probability. Args: chance (out of 1 or a %) */
	public static boolean doWithChance(double num) {
		if (num > 1)
			num /= 100D;
		if (num >= 1)
			return true;
		if (num <= 0)
			return false;

		return rand.nextDouble() < num;
	}

}
