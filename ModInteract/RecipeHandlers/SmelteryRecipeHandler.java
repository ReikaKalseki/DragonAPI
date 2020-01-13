/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.RecipeHandlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler.Pulses;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

public class SmelteryRecipeHandler {

	public static final int INGOT_AMOUNT = 144;

	private static boolean isLoaded;

	private static Method addMelting;
	private static Method addCasting;
	private static Method addBlockCasting;
	private static Method getAllRecipes;
	private static Method addAlloying;
	private static Object castingInstance;
	private static Object castingBasinInstance;
	private static Object smelteryInstance;
	private static Class castingRecipe;
	private static Field recipeOutput;
	private static Field recipeFluid;
	private static Field recipeCast;

	public static void addIngotMelting(ItemStack ingot, ItemStack render, int temp, String fluid) {
		addMelting(ingot, render, temp, INGOT_AMOUNT, fluid);
	}

	public static void addIngotMelting(ItemStack ingot, ItemStack render, int temp, Fluid fluid) {
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

	public static void addBlockMelting(ItemStack is, int temp, Fluid fluid) {
		if (fluid == null)
			throw new MisuseException("You cannot melt items into a null fluid!");
		addMelting(is, is, temp, new FluidStack(fluid, INGOT_AMOUNT*9));
	}

	public static void addMelting(ItemStack is, ItemStack render, int temp, FluidStack fluid) {
		Block b = Block.getBlockFromItem(render.getItem());
		if (!(render.getItem() instanceof ItemBlock) || b == null)
			throw new MisuseException("The render block must be a non-null block!");
		if (!isLoaded)
			return;
		try {
			addMelting.invoke(smelteryInstance, is, b, render.getItemDamage(), temp, fluid);
			DragonAPICore.log("Adding smeltery melting of "+is+" into "+fluidToString(fluid)+".");
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not add Smeltery Recipe for "+is+" to "+fluidToString(fluid)+" @ "+temp+"!");
			e.printStackTrace();
		}
	}

	public static void addIngotCasting(ItemStack out, String fluid, int delay) {
		addCasting(TinkerToolHandler.getInstance().getIngotCast(), out, FluidRegistry.getFluid(fluid), INGOT_AMOUNT, delay);
	}

	public static void addIngotCasting(ItemStack out, Fluid fluid, int delay) {
		addCasting(TinkerToolHandler.getInstance().getIngotCast(), out, fluid, INGOT_AMOUNT, delay);
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
		if (!isLoaded)
			return;
		try {
			addCasting.invoke(castingInstance, out, in, cast, delay);
			if (addCastRecipe)
				addCasting.invoke(castingInstance, cast, getCastFluid(), out, delay/2);
			DragonAPICore.log("Adding casting of "+fluidToString(in)+" to "+out+" with "+cast+".");
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not add Casting Recipe for "+fluidToString(in)+" to "+out+" with "+cast+"!");
			e.printStackTrace();
		}
	}

	public static void addReversibleCasting(ItemStack cast, ItemStack out, ItemStack render, int temp, FluidStack fluid, int delay) {
		addCasting(cast, out, fluid, delay);
		addMelting(out, render, temp, fluid);
	}

	public static void addReversibleCasting(ItemStack cast, ItemStack out, ItemStack render, int temp, Fluid fluid, int fluidAmount, int delay) {
		addCasting(cast, out, fluid, fluidAmount, delay);
		addMelting(out, render, temp, fluidAmount, fluid);
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

	public static void addBlockCasting(ItemStack block, Fluid fluid, int delay) {
		if (fluid == null)
			throw new MisuseException("You cannot cast blocks from a null fluid!");
		addBlockCasting(block, new FluidStack(fluid, INGOT_AMOUNT*9), delay);
	}

	public static void addBlockCasting(ItemStack block, FluidStack fluid, int delay) {
		if (!(block.getItem() instanceof ItemBlock))
			throw new MisuseException("You cannot cast a non-block as a block!");
		if (!isLoaded)
			return;
		try {
			addBlockCasting.invoke(castingBasinInstance, block, fluid, delay);
			DragonAPICore.log("Adding block casting of "+fluidToString(fluid)+" to "+block+".");
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not add Block Casting Recipe for "+fluidToString(fluid)+" to "+block+"!");
			e.printStackTrace();
		}
	}

	public static void addAlloying(FluidStack out, FluidStack... in) {
		if (!isLoaded)
			return;
		try {
			addAlloying.invoke(smelteryInstance, out, in);
			DragonAPICore.log("Adding alloying of "+Arrays.toString(in)+" to "+fluidToString(out)+".");
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not add alloying of "+Arrays.toString(in)+" to "+fluidToString(out)+".");
			e.printStackTrace();
		}
	}

	public static List getCastingRecipes() {
		if (!isLoaded)
			return null;
		try {
			List li = (List)getAllRecipes.invoke(castingInstance);
			return li;
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not fetch TiC Casting Recipes!");
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack getRecipeOutput(Object recipe) {
		if (!isLoaded)
			return null;
		if (recipe == null || recipe.getClass() != castingRecipe)
			throw new MisuseException("You cannot get the output for a null or non-recipe!");
		try {
			return (ItemStack)recipeOutput.get(recipe);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not fetch TiC Casting Recipe output!");
			e.printStackTrace();
			return null;
		}
	}

	public static FluidStack getRecipeFluid(Object recipe) {
		if (!isLoaded)
			return null;
		if (recipe == null || recipe.getClass() != castingRecipe)
			throw new MisuseException("You cannot get the fluid for a null or non-recipe!");
		try {
			return (FluidStack)recipeFluid.get(recipe);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not fetch TiC Casting Recipe fluid!");
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack getRecipeCast(Object recipe) {
		if (!isLoaded)
			return null;
		if (recipe == null || recipe.getClass() != castingRecipe)
			throw new MisuseException("You cannot get the cast for a null or non-recipe!");
		try {
			return (ItemStack)recipeCast.get(recipe);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not fetch TiC Casting Recipe cast!");
			e.printStackTrace();
			return null;
		}
	}

	private static String fluidToString(FluidStack fs) {
		return fs.amount+" mB of "+fs.getLocalizedName();
	}

	private static FluidStack getCastFluid() {
		Fluid f = FluidRegistry.getFluid("aluminiumbrass.molten");
		if (f == null)
			f = FluidRegistry.getFluid("aluminumbrass.molten");
		if (f == null)
			f = FluidRegistry.getFluid("liquid_alubrass");
		if (f == null)
			f = FluidRegistry.getFluid("fluid.molten.alubrass");
		return new FluidStack(f, INGOT_AMOUNT);
	}

	static {
		if (ModList.TINKERER.isLoaded() && Pulses.SMELTERY.isLoaded()) {
			try {
				Class reg = Class.forName("tconstruct.library.TConstructRegistry");
				Method getTableCasting = reg.getMethod("getTableCasting");
				Method getBasinCasting = reg.getMethod("getBasinCasting");

				castingInstance = getTableCasting.invoke(null);
				castingBasinInstance = getBasinCasting.invoke(null);

				Class smeltery = Class.forName("tconstruct.library.crafting.Smeltery");
				Field inst = smeltery.getField("instance");
				smelteryInstance = inst.get(null);

				addMelting = smeltery.getMethod("addMelting", ItemStack.class, Block.class, int.class, int.class, FluidStack.class);
				addAlloying = smeltery.getMethod("addAlloyMixing", FluidStack.class, FluidStack[].class);

				Class casting = Class.forName("tconstruct.library.crafting.LiquidCasting");
				addCasting = casting.getMethod("addCastingRecipe", ItemStack.class, FluidStack.class, ItemStack.class, int.class);
				addBlockCasting = casting.getMethod("addCastingRecipe", ItemStack.class, FluidStack.class, int.class);
				getAllRecipes = casting.getMethod("getCastingRecipes");

				castingRecipe = Class.forName("tconstruct.library.crafting.CastingRecipe");
				recipeOutput = castingRecipe.getField("output");
				recipeFluid = castingRecipe.getField("castingMetal");
				recipeCast = castingRecipe.getField("cast");
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not load Smeltery Recipe Handler!");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.TINKERER, e);
			}
			isLoaded = true;
		}
		else {
			isLoaded = false;
		}
	}
}
