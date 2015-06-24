/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class SmelteryRecipeHandler {

	public static final int INGOT_AMOUNT = 144;

	private static Method addMelting;
	private static Method addCasting;
	private static Method addBlockCasting;
	private static Object castingInstance;
	private static Object castingBasinInstance;
	private static Object smelteryInstance;
	private static Item castItem;

	public static void addIngotMelting(ItemStack ingot, ItemStack render, int temp, String fluid) {
		addMelting(ingot, render, temp, INGOT_AMOUNT, fluid);
	}

	public static void addMelting(ItemStack is, ItemStack render, int temp, int fluidAmount, String fluid) {
		addMelting(is, render, temp, fluidAmount, FluidRegistry.getFluid(fluid));
	}

	public static void addMelting(ItemStack is, ItemStack render, int temp, int fluidAmount, Fluid fluid) {
		if (fluid == null)
			throw new MisuseException("You cannot melt items into a null fluid!");
		addMelting(is, render, temp, new FluidStack(fluid, fluidAmount));
	}

	public static void addMelting(ItemStack is, ItemStack render, int temp, FluidStack fluid) {
		Block b = Block.getBlockFromItem(render.getItem());
		if (!(render.getItem() instanceof ItemBlock) || b == null)
			throw new MisuseException("The render block must be a non-null block!");
		try {
			addMelting.invoke(smelteryInstance, is, b, render.getItemDamage(), temp, fluid);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Adding smeltery melting of "+is+" into "+fluidToString(fluid)+".");
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not add Smeltery Recipe for "+is+" to "+fluidToString(fluid)+" @ "+temp+"!");
			e.printStackTrace();
		}
	}

	public static void addIngotCasting(ItemStack out, String fluid, int delay) {
		addCasting(getIngotCast(), out, FluidRegistry.getFluid(fluid), INGOT_AMOUNT, delay);
	}

	public static void addCasting(ItemStack cast, ItemStack out, String fluid, int fluidAmount, int delay) {
		addCasting(cast, out, FluidRegistry.getFluid(fluid), fluidAmount, delay);
	}

	public static void addCasting(ItemStack cast, ItemStack out, Fluid fluid, int fluidAmount, int delay) {
		if (fluid == null)
			throw new MisuseException("You cannot cast items from a null fluid!");
		addCasting(cast, out, new FluidStack(fluid, fluidAmount), delay);
	}

	public static void addCasting(ItemStack cast, ItemStack out, FluidStack in, int delay) {
		addCasting(cast, out, in, delay, true);
	}

	public static void addCasting(ItemStack cast, ItemStack out, FluidStack in, int delay, boolean addCastRecipe) {
		try {
			addCasting.invoke(castingInstance, out, in, cast, delay);
			if (addCastRecipe)
				addCasting.invoke(castingInstance, cast, getCastingFluid(), out, delay/2);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Adding casting of "+fluidToString(in)+" to "+out+" with "+cast+".");
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not add Casting Recipe for "+fluidToString(in)+" to "+out+" with "+cast+"!");
			e.printStackTrace();
		}
	}

	public static void addReversibleCasting(ItemStack cast, ItemStack out, ItemStack render, int temp, FluidStack fluid, int delay) {
		addCasting(cast, out, fluid, delay);
		addMelting(out, render, temp, fluid);
	}

	public static void addReversibleCasting(ItemStack cast, ItemStack out, ItemStack render, int temp, String fluid, int fluidAmount, int delay) {
		addCasting(cast, out, fluid, fluidAmount, delay);
		addMelting(out, render, temp, fluidAmount, fluid);
	}

	public static void addBlockCasting(ItemStack block, int fluidAmount, String fluid, int delay) {
		addBlockCasting(block, fluidAmount, FluidRegistry.getFluid(fluid), delay);
	}

	public static void addBlockCasting(ItemStack block, int fluidAmount, Fluid fluid, int delay) {
		if (fluid == null)
			throw new MisuseException("You cannot cast blocks from a null fluid!");
		addBlockCasting(block, new FluidStack(fluid, fluidAmount), delay);
	}

	public static void addBlockCasting(ItemStack block, FluidStack fluid, int delay) {
		if (!(block.getItem() instanceof ItemBlock))
			throw new MisuseException("You cannot cast a non-block as a block!");
		try {
			addBlockCasting.invoke(castingBasinInstance, block, fluid, delay);
			ReikaJavaLibrary.pConsole("DRAGONAPI: Adding block casting of "+fluidToString(fluid)+" to "+block+".");
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not add Block Casting Recipe for "+fluidToString(fluid)+" to "+block+"!");
			e.printStackTrace();
		}
	}

	private static String fluidToString(FluidStack fs) {
		return fs.amount+" mB of "+fs.getLocalizedName();
	}

	private static ItemStack getIngotCast() {
		return new ItemStack(castItem, 1, 0);
	}

	private static FluidStack getCastingFluid() {
		Fluid f = FluidRegistry.getFluid("aluminiumbrass.molten");
		if (f == null)
			f = FluidRegistry.getFluid("aluminumbrass.molten");
		if (f == null)
			f = FluidRegistry.getFluid("liquid_alubrass");
		if (f == null)
			f = FluidRegistry.getFluid("fluid.molten.alubrass");
		return new FluidStack(f, 144);
	}

	static {
		if (ModList.TINKERER.isLoaded()) {
			try {
				Class reg = Class.forName("tconstruct.library.TConstructRegistry");
				Method getTableCasting = reg.getMethod("getTableCasting");
				Method getBasinCasting = reg.getMethod("getBasinCasting");

				castingInstance = getTableCasting.invoke(null);
				castingBasinInstance = getBasinCasting.invoke(null);

				Class c = Class.forName("tconstruct.smeltery.TinkerSmeltery");
				castItem = (Item)c.getField("metalPattern").get(null);

				Class smeltery = Class.forName("tconstruct.library.crafting.Smeltery");
				Field inst = smeltery.getField("instance");
				smelteryInstance = inst.get(null);

				addMelting = smeltery.getMethod("addMelting", ItemStack.class, Block.class, int.class, int.class, FluidStack.class);

				Class casting = Class.forName("tconstruct.library.crafting.LiquidCasting");
				addCasting = casting.getMethod("addCastingRecipe", ItemStack.class, FluidStack.class, ItemStack.class, int.class);
				addBlockCasting = casting.getMethod("addCastingRecipe", ItemStack.class, FluidStack.class, int.class);
			}
			catch (Exception e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load Smeltery Recipe Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
		}
	}
}
