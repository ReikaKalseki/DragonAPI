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

	public static int getSafeRandomInt(int val) {
		if (val <= 1)
			return 0;
		return rand.nextInt(val);
	}

	public static double getRandomPlusMinus(double base, double range) {
		double add = -range+rand.nextDouble()*(range*2+1);
		return (base+add);
	}

}
