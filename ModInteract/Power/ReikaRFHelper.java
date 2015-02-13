/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Power;

import java.lang.reflect.Field;

import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;

public class ReikaRFHelper {

	private static final int JoulePerRF_legacy = 5628;

	private static final int crucibleStoneMeltRF_Default = 200000; //configurable, defaults to 200k
	private static int crucibleStoneMelt = -1; //configurable, defaults to 200k

	//Default value yields 1RF/t=520W, with config ranges from 1RF/t=260W to 1RF/t=1040W
	public static int getWattsPerRF() {
		return (int)(20*ReikaThermoHelper.ROCK_MELT_ENERGY/getRFPerStoneBlock()); //*20 for /t vs /s
	}

	public static long getRFPerStoneBlock() {
		return crucibleStoneMelt > 0 ? crucibleStoneMelt : crucibleStoneMeltRF_Default;
	}

	static {
		try {
			Class c = Class.forName("thermalexpansion.core.TEProps");
			Field f = c.getDeclaredField("lavaRF");
			crucibleStoneMelt = f.getInt(null);
			crucibleStoneMelt = MathHelper.clamp_int(crucibleStoneMelt, 100000, 400000); //clamp read int to 1/2 and 2x normal
		}
		catch (Exception e) {

		}
	}

}
