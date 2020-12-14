/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventBase;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class WaterColorEvent extends PositionEventBase {

	public final int originalColor;
	public int color;

	public WaterColorEvent(IBlockAccess iba, int x, int y, int z, int c) {
		super(iba, x, y, z);
		color = originalColor = c;
	}

	public static int fire(IBlockAccess iba, int x, int y, int z) {
		WaterColorEvent evt = new WaterColorEvent(iba, x, y, z, calcDefault(iba, x, y, z));
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.color;
	}

	private static int calcDefault(IBlockAccess iba, int x, int y, int z) {
		int sumR = 0;
		int sumG = 0;
		int sumB = 0;

		int r = 1;
		for (int k = -r; k <= r; ++k) {
			for (int i = -r; i <= r; ++i) {
				int c = iba.getBiomeGenForCoords(x+i, z+k).getWaterColorMultiplier();
				sumR += ReikaColorAPI.getRed(c);
				sumG += ReikaColorAPI.getGreen(c);
				sumB += ReikaColorAPI.getBlue(c);
			}
		}

		return (sumR / 9 & 255) << 16 | (sumG / 9 & 255) << 8 | (sumB / 9 & 255);
	}

}
