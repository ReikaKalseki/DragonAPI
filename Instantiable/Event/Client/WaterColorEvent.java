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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.common.eventhandler.Event;


public class WaterColorEvent extends Event {

	public final int originalColor;
	public int color;

	public final IBlockAccess world;
	public final int x;
	public final int y;
	public final int z;

	public WaterColorEvent(IBlockAccess iba, int x, int y, int z, int c) {
		color = originalColor = c;
		world = iba;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BiomeGenBase getBiome() {
		return world.getBiomeGenForCoords(x, z);
	}

	public int getLightLevel() {
		return world.getLightBrightnessForSkyBlocks(x, y, z, 0);
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
