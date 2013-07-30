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
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import buildcraft.api.fuels.IronEngineFuel;

public class ReikaBuildCraftHelper extends DragonAPICore {

	public static final int rhogas = 720;
	public static final int rhooil = 850; //varies between 700 and 1000

	private static final LiquidStack fuel = LiquidDictionary.getLiquid("Fuel", LiquidContainerRegistry.BUCKET_VOLUME);
	private static double gasEnergyPerKg = 46.9;

	public static boolean doesBuildCraftExist() {
		return ReikaJavaLibrary.doesClassExist("BuildCraftEnergy");
	}

	public static float getFuelMJPerTick() {
		return IronEngineFuel.getFuelForLiquid(fuel).powerPerCycle;
	}

	/** In ticks */
	public static float getFuelBucketDuration() {
		return IronEngineFuel.getFuelForLiquid(fuel).totalBurningTime;
	}

	/** Minecraft joules per second */
	public static float getFuelMinecraftWatts() {
		return 20*getFuelMJPerTick()/(getFuelBucketDuration()/20F);
	}

	/** J/s for fuel */
	public static double getFuelRealPower() {
		double energy = getFuelBucketEnergy();
		double time = getFuelBucketDuration();
		return energy/time;
	}

	public static double getWattsPerMJ() {
		if (!doesBuildCraftExist())
			return 56280; //default
		double power = getFuelRealPower();
		double mj = getFuelMJPerTick();
		return power/mj; //as of 1.5.2, is 56.28kW per MJ
	}

	/** Get mass of gasoline in kilograms from the number of forge millibuckets. */
	private static double getGasolineMass(int millis) {
		double volume = millis/LiquidContainerRegistry.BUCKET_VOLUME;
		return rhogas*volume;
	}

	private static double getOilMass(int millis) {
		double volume = millis/LiquidContainerRegistry.BUCKET_VOLUME;
		return rhooil*volume;
	}

	/** Get energy of gasoline in joules from the number of forge millibuckets. */
	public static double getGasolineEnergy(int millis) {
		double mass = getGasolineMass(millis);
		return gasEnergyPerKg*1000000*mass;
	}

	/** Get energy of one fuel bucket in joules. */
	public static double getFuelBucketEnergy() {
		return getGasolineEnergy(LiquidContainerRegistry.BUCKET_VOLUME);
	}

}
