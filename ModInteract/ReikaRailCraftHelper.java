/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.ModList;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;

public class ReikaRailCraftHelper extends DragonAPICore {

	private static final LiquidStack steam = LiquidDictionary.getLiquid("Steam", LiquidContainerRegistry.BUCKET_VOLUME);

	public static boolean doesRailCraftExist() {
		return ModList.RAILCRAFT.isLoaded();
	}

	/** Get energy of one steam bucket/block in joules. */
	public static double getSteamBucketEnergy(double Tinit) {
		double dT = 100-Tinit;
		if (dT < 0) //If initial T is hotter
			dT = 1; //require 4.18 MJ (one degree)
		return ReikaThermoHelper.WATER_BLOCK_HEAT*dT; //4.18MJ/block/degree * delta-T
	}

	/** Get the energy liberated by the conversion of one block of steam to one bucket of water. */
	public static double getSteamBucketEnergy() {
		return ReikaThermoHelper.WATER_BOIL_ENTHALPY*LiquidContainerRegistry.BUCKET_VOLUME*1000; //2260 kJ/kg * 1000 kg * 1000 J/kJ
	}

}
