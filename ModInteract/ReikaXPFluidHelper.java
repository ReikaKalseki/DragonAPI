/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.DragonAPICore;

public class ReikaXPFluidHelper {

	/** Size is mB per units XP */
	private static FluidStack loaded;
	private static Fluid type;
	private static int ratio;

	static {
		addFluid("openblocks.OpenBlocks$Fluids", "xpJuice", "openblocks.Config", "xpToLiquidRatio");
		addFluid("openblocks.OpenBlocks$Fluids", "xpJuice", "openmods.utils.EnchantmentUtils", "RATIO");
		addFluid("crazypants.enderio.EnderIO", "fluidXpJuice", "crazypants.enderio.xp.XpUtil", "RATIO");
		addFluid("mods.immibis.lxp.LiquidXPMod", "defaultFluid", "mods.immibis.lxp.LiquidXPMod", "mbPerXp");
	}

	/** Reflective fluid-based version */
	private static void addFluid(String cf, String sf, String c, String f) {
		try {
			Class cl = Class.forName(c);
			Field fd = cl.getDeclaredField(f);
			fd.setAccessible(true);

			Class clf = Class.forName(cf);
			Field fdf = clf.getDeclaredField(sf);
			fdf.setAccessible(true);

			addFluid((Fluid)fdf.get(null), fd.getInt(null));
		}
		catch (Exception e) {
			DragonAPICore.logError("Error loading xp fluid type as loaded from "+cf+"#"+sf+": "+e);
			//e.printStackTrace();
		}
	}

	/** Reflective version */
	private static void addFluid(String s, String c, String f) {
		if (loaded != null)
			return;
		try {
			Class cl = Class.forName(c);
			Field fd = cl.getDeclaredField(f);
			fd.setAccessible(true);
			addFluid(s, fd.getInt(null));
		}
		catch (Exception e) {
			DragonAPICore.logError("Error loading xp fluid type "+s+": "+e);
			//e.printStackTrace();
		}
	}

	private static void addFluid(Fluid f, int amt) {
		if (loaded != null)
			return;
		if (f != null) {
			register(f, amt);
		}
	}

	private static void addFluid(String s, int amt) {
		Fluid f = FluidRegistry.getFluid(s);
		if (f != null) {
			register(f, amt);
		}
	}

	private static void register(Fluid f, int amt) {
		if (loaded == null) {
			loaded = new FluidStack(f, amt);
			type = f;
			ratio = amt;
			DragonAPICore.log("Loaded XP fluid "+f.getName()+" with ratio of "+amt+" mB/xp.");
		}
		else {
			DragonAPICore.log("Rejected XP fluid "+f.getName()+" with ratio of "+amt+" mB/xp; a fluid is already loaded.");
		}
	}

	public static Fluid getFluidType() {
		return type;
	}

	public static FluidStack getFluid() {
		return loaded != null ? loaded.copy() : null;
	}

	public static FluidStack getFluid(int xp) {
		FluidStack fs = getFluid();
		if (fs != null) {
			fs.amount *= xp;
		}
		return fs;
	}

	public static int getXPForAmount(int fluid) {
		return ratio > 0 ? fluid/ratio : 0;
	}

	public static boolean fluidsExist() {
		return loaded != null;
	}

}
