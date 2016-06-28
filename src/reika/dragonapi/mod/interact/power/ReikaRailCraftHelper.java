/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.power;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.ModList;
import reika.dragonapi.libraries.mathsci.ReikaThermoHelper;

public class ReikaRailCraftHelper extends DragonAPICore {

	private static final Fluid STEAM = FluidRegistry.getFluid("steam");

	public static boolean doesRailCraftExist() {
		return ModList.RAILCRAFT.isLoaded();
	}

	/** Get energy of steam in joules. */
	public static double getSteamEnergy(int Tinit, int mB) {
		return getSteamBoilingEnergy(mB)+getSteamBucketEnergyToHeat(Tinit, mB);
	}

	/** Get the energy liberated by the conversion of one block of steam to one bucket of water. */
	public static double getSteamBoilingEnergy(int mB) {
		return ReikaThermoHelper.WATER_BOIL_ENTHALPY*mB*1000; //2260 kJ/kg * 1000 kg * 1000 J/kJ
	}

	/** Get the energy required to heat one water bucket to 100 degrees */
	public static double getSteamBucketEnergyToHeat(int Tinit, int mB) {
		double dT = 100-Tinit;
		if (dT < 0)
			dT = 0;
		return ReikaThermoHelper.WATER_HEAT*mB*1000*dT; //4.18 kJ/kgK * 1000 kg * 1000 J/kJ * dT K
	}

	public static int getAmountConvertibleSteam(int Tinit, long energy) {
		double per = getSteamEnergy(Tinit, 1);
		return (int)(energy/per);
	}

}
