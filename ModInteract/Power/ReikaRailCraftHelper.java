/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Power;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.MathSci.ReikaThermoHelper;

public class ReikaRailCraftHelper extends DragonAPICore {

	private static Class sharedClass;
	private static Class solidClass;
	private static Class fluidClass;
	private static Field boiler;
	//private static Field lit;
	private static Field boilerBurnTime;
	private static Field boilerHeat;
	private static Field boilerBurning;

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

	public static boolean isFirebox(TileEntity te) {
		return te != null && sharedClass.isAssignableFrom(te.getClass());
	}

	public static boolean isSolidFirebox(TileEntity te) {
		return te != null && solidClass == te.getClass();
	}

	public static boolean isFluidFirebox(TileEntity te) {
		return te != null && fluidClass == te.getClass();
	}

	static {
		if (ModList.RAILCRAFT.isLoaded()) {
			try {
				sharedClass = Class.forName("mods.railcraft.common.blocks.machine.beta.TileBoilerFirebox");
				boiler = sharedClass.getDeclaredField("boiler");
				boiler.setAccessible(true);
				//lit = tileClass.getDeclaredField("wasLit");

				solidClass = Class.forName("mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxSolid");
				fluidClass = Class.forName("mods.railcraft.common.blocks.machine.beta.TileBoilerFireboxFluid");

				Class c2 = Class.forName("mods.railcraft.common.util.steam.SteamBoiler");
				boilerBurnTime = c2.getDeclaredField("burnTime");
				boilerBurnTime.setAccessible(true);

				boilerHeat = c2.getDeclaredField("heat");
				boilerHeat.setAccessible(true);

				boilerBurning = c2.getDeclaredField("isBurning");
				boilerBurning.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error loading Firebox Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.RAILCRAFT, e);
			}
		}
	}

	public static class FireboxWrapper {

		private final long tileID;

		public double temperature;

		public FireboxWrapper(TileEntity te) {
			if (!isFirebox(te))
				throw new MisuseException("Tile is not a firebox!");
			tileID = System.identityHashCode(te);
			this.load(te);
		}

		/** Rerun this to reload the data from the tile. */
		public void load(TileEntity te) {
			if (System.identityHashCode(te) != tileID)
				throw new MisuseException("You cannot reuse a FireboxWrapper instance for different TileEntities!");
			try {
				Object obj = boiler.get(te);
				temperature = boilerHeat.getDouble(obj);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error running Firebox Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.RAILCRAFT, e);
			}
		}

		/** Call this to write the data to the TileEntity. */
		public void write(TileEntity te) {
			if (System.identityHashCode(te) != tileID)
				throw new MisuseException("You cannot reuse a FireboxWrapper instance for different TileEntities!");
			try {
				Object obj = boiler.get(te);
				boilerHeat.setDouble(obj, temperature);
				boilerBurning.setBoolean(obj, temperature > 20);
				if (temperature > 20) {
					boilerBurnTime.setDouble(obj, 20);
				}
			}
			catch (Exception e) {
				DragonAPICore.logError("Error running Firebox Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.RAILCRAFT, e);
			}
		}
	}

}
