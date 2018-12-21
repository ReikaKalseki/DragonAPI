/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;


public class TinkerSmelteryHandler {

	private static Class tileClass;
	private static Field fuel;
	private static Field temperatures;
	private static Field internal;
	private static Field use;
	private static Field use2;
	private static Field tank;
	private static Field tanks;
	private static Class coord;
	private static Field coordX;
	private static Field coordY;
	private static Field coordZ;

	static {
		if (ModList.TINKERER.isLoaded()) {
			try {
				tileClass = Class.forName("tconstruct.smeltery.logic.SmelteryLogic");
				fuel = tileClass.getDeclaredField("fuelAmount"); //the "gague" (XD) field is for GUI display
				fuel.setAccessible(true);
				temperatures = tileClass.getDeclaredField("activeTemps");
				temperatures.setAccessible(true);
				internal = tileClass.getDeclaredField("internalTemp");
				internal.setAccessible(true);
				use = tileClass.getDeclaredField("useTime");
				use.setAccessible(true);
				use2 = tileClass.getDeclaredField("inUse");
				use2.setAccessible(true);
				tank = tileClass.getDeclaredField("activeLavaTank");
				tank.setAccessible(true);
				tanks = tileClass.getDeclaredField("lavaTanks");
				tanks.setAccessible(true);
				coord = Class.forName("mantle.world.CoordTuple");
				coordX = coord.getDeclaredField("x");
				coordX.setAccessible(true);
				coordY = coord.getDeclaredField("y");
				coordY.setAccessible(true);
				coordZ = coord.getDeclaredField("z");
				coordZ.setAccessible(true);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error loading Smeltery Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
		}
	}

	public static boolean isSmelteryController(TileEntity te) {
		return te != null && te.getClass() == tileClass;
	}

	public static class SmelteryWrapper {

		public int[] currentTemperatures;

		/** This is a temperature. Higher with pyrotheum (1300 vs 5000 for lava/pyro). */
		public int meltPower;
		public int fuelLevel;

		private final long tileID;

		public SmelteryWrapper(TileEntity te) {
			if (!isSmelteryController(te))
				throw new MisuseException("Tile is not a smeltery!");
			tileID = System.identityHashCode(te);
			this.load(te);
		}

		/** Rerun this to reload the data from the tile. */
		public void load(TileEntity te) {
			if (System.identityHashCode(te) != tileID)
				throw new MisuseException("You cannot reuse a SmelteryWrapper instance for different TileEntities!");
			try {
				currentTemperatures = (int[])temperatures.get(te);
				meltPower = internal.getInt(te);
				fuelLevel = fuel.getInt(te);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error running Smeltery Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
		}

		/** Call this to write the data to the TileEntity. */
		public void write(TileEntity te) {
			if (System.identityHashCode(te) != tileID)
				throw new MisuseException("You cannot reuse a SmelteryWrapper instance for different TileEntities!");
			try {
				temperatures.set(te, currentTemperatures);
				internal.setInt(te, meltPower);
				fuel.setInt(te, fuelLevel);
				if (fuelLevel > 0) {
					use.setInt(te, Math.max(8, use.getInt(te)));
					use2.setBoolean(te, true);
				}
				te.markDirty();
				Method m = tileClass.getDeclaredMethod("heatItems");
				m.setAccessible(true);
				m.invoke(te);
			}
			catch (Exception e) {
				DragonAPICore.logError("Error running Smeltery Handling!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
		}

	}

	public static IFluidHandler getActiveTank(TileEntity te) {
		try {
			Object coord = tank.get(te);
			if (coord == null) {
				for (Object o : (ArrayList)tanks.get(te)) {
					if (o != null && o.getClass() == coord) {
						coord = o;
						break;
					}
				}
			}
			if (coord == null)
				return null;
			int x = coordX.getInt(coord);
			int y = coordY.getInt(coord);
			int z = coordZ.getInt(coord);
			return (IFluidHandler)te.worldObj.getTileEntity(x, y, z);
		}
		catch (Exception e) {
			DragonAPICore.logError("Error running Smeltery Handling!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
		}
		return null;
	}

	public static void tick(TileEntity te, int temp) { //they drain 15mB per cycle
		IFluidHandler ifl = getActiveTank(te);
		if (ifl != null) {
			ifl.fill(ForgeDirection.UP, new FluidStack(FluidRegistry.LAVA, 15), true);
		}
		te.markDirty();
		te.updateEntity();
	}
}
