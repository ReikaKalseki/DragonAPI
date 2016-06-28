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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import reika.dragonapi.DragonAPICore;
import buildcraft.api.fuels.BuildcraftFuelRegistry;

public class ReikaBuildCraftHelper extends DragonAPICore {

	public static final int rhogas = 720;
	public static final int rhooil = 850; //varies between 700 and 1000

	private static final Fluid fuel = FluidRegistry.getFluid("fuel");
	private static double gasEnergyPerKg = 46.9; //MegaJoules

	public static int getFuelRFPerTick() {
		return BuildcraftFuelRegistry.fuel.getFuel(fuel).getPowerPerCycle();
	}

	/** In ticks */
	public static int getFuelBucketDuration() {
		return BuildcraftFuelRegistry.fuel.getFuel(fuel).getTotalBurningTime();
	}

	/** Minecraft joules per second */
	@Deprecated
	public static float getFuelMinecraftWatts() {
		return 20*getFuelRFPerTick()/(getFuelBucketDuration()/20F);
	}

	/** J/s for fuel */
	@Deprecated
	public static double getFuelRealPower() {
		double energy = getFuelBucketEnergy();
		double time = getFuelBucketDuration();
		return energy/time;
	}

	@Deprecated
	public static double getWattsPerMJ() {
		//if (!doesBuildCraftExist())
		return 56280; //default  *4?
		//double power = getFuelRealPower();
		//double mj = getFuelMJPerTick();
		//return power/mj; //as of 1.5.2, is 56.28kW per MJ/t
	}

	/** Get mass of gasoline in kilograms from the number of forge millibuckets. */
	private static double getGasolineMass(int millis) {
		double volume = millis/FluidContainerRegistry.BUCKET_VOLUME;
		return rhogas*volume;
	}

	private static double getOilMass(int millis) {
		double volume = millis/FluidContainerRegistry.BUCKET_VOLUME;
		return rhooil*volume;
	}

	/** Get energy of gasoline in joules from the number of forge millibuckets. */
	public static double getGasolineEnergy(int millis) {
		double mass = getGasolineMass(millis);
		return gasEnergyPerKg*1000000*mass;
	}

	/** Get energy of one fuel bucket in joules. */
	public static double getFuelBucketEnergy() {
		return getGasolineEnergy(FluidContainerRegistry.BUCKET_VOLUME);
	}

}
