/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import Reika.DragonAPI.Instantiable.Event.ThermalRecipeEvent;
import Reika.DragonAPI.Instantiable.Event.ThermalRecipeEvent.ThermalMachine;
import cpw.mods.fml.common.event.FMLInterModComms;

public class ThermalRecipeHelper {

	public static void addMagmaticFuel(Fluid f, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setString("fluidName", f.getName());
		toSend.setInteger("energy", energy);
		FMLInterModComms.sendMessage("ThermalExpansion", "MagmaticFuel", toSend);
	}

	public static void addCompressionFuel(Fluid f, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setString("fluidName", f.getName());
		toSend.setInteger("energy", energy);
		FMLInterModComms.sendMessage("ThermalExpansion", "CompressionFuel", toSend);
	}

	public static void addCoolant(Fluid f, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setString("fluidName", f.getName());
		toSend.setInteger("energy", energy);
		FMLInterModComms.sendMessage("ThermalExpansion", "Coolant", toSend);
	}

	public static void addFluidTransposerFill(ItemStack in, ItemStack out, int energy, FluidStack f) {
		addFluidTransposerFill(in, out, energy, f, false);
	}

	public static void addFluidTransposerFill(ItemStack in, ItemStack out, int energy, FluidStack f, boolean reversible) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		out.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", reversible);
		f.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage("ThermalExpansion", "TransposerFillRecipe", toSend);
		fireEvent(ThermalRecipeEvent.ThermalMachine.TRANSPOSER, in, null, out, null, 0, energy);
	}

	private static void fireEvent(ThermalMachine type, ItemStack in, FluidStack out, int rf) {
		MinecraftForge.EVENT_BUS.post(new ThermalRecipeEvent(type, in, out, rf));
	}

	private static void fireEvent(ThermalMachine type, ItemStack in1, ItemStack in2, ItemStack out1, ItemStack out2, int out2chance, int rf) {
		MinecraftForge.EVENT_BUS.post(new ThermalRecipeEvent(type, in1, in2, out1, out2, out2chance, rf));
	}

	public static void addFluidTransposerDrain(ItemStack in, ItemStack out, int energy, FluidStack f) {
		addFluidTransposerDrain(in, out, energy, f, 100);
	}

	public static void addFluidTransposerDrain(ItemStack in, ItemStack out, int energy, FluidStack f, boolean reversible) {
		addFluidTransposerDrain(in, out, energy, f, 100, reversible);
	}

	public static void addFluidTransposerDrain(ItemStack in, ItemStack out, int energy, FluidStack f, int chance) {
		addFluidTransposerDrain(in, out, energy, f, chance, false);
	}

	public static void addFluidTransposerDrain(ItemStack in, ItemStack out, int energy, FluidStack f, int chance, boolean reversible) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());
		toSend.setTag("fluid", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		out.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", reversible);
		toSend.setInteger("chance", chance);
		f.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage("ThermalExpansion", "TransposerExtractRecipe", toSend);
		fireEvent(ThermalRecipeEvent.ThermalMachine.TRANSPOSER, in, null, out, null, 0, energy);
	}

	public static void addInductionSmelter(ItemStack in1, ItemStack in2, ItemStack out1, int energy) {
		addInductionSmelter(in1, in2, out1, null, energy);
	}

	public static void addInductionSmelter(ItemStack in1, ItemStack in2, ItemStack out1, ItemStack out2, int energy) {
		addInductionSmelter(in1, in2, out1, out2, 100, energy);
	}

	public static void addInductionSmelter(ItemStack in1, ItemStack in2, ItemStack out1, ItemStack out2, int out2chance, int energy) {
		addTwoInTwoOutWithChance("SmelterRecipe", in1, in2, out1, out2, out2chance, energy);
	}

	public static void addPulverizerRecipe(ItemStack in, ItemStack out1, ItemStack out2, int energy) {
		addPulverizerRecipe(in, out1, out2, 100, energy);
	}

	public static void addPulverizerRecipe(ItemStack in, ItemStack out, int energy) {
		addPulverizerRecipe(in, out, null, energy);
	}

	public static void addPulverizerRecipe(ItemStack in, ItemStack out1, ItemStack out2, int out2chance, int energy) {
		addOneInTwoOutWithChance("PulverizerRecipe", in, out1, out2, out2chance, energy);
	}

	public static void addSawmillRecipe(ItemStack in, ItemStack out1, ItemStack out2, int energy) {
		addSawmillRecipe(in, out1, out2, 100, energy);
	}

	public static void addSawmillRecipe(ItemStack in, ItemStack out, int energy) {
		addSawmillRecipe(in, out, null, energy);
	}

	public static void addSawmillRecipe(ItemStack in, ItemStack out1, ItemStack out2, int out2chance, int energy) {
		addOneInTwoOutWithChance("SawmillRecipe", in, out1, out2, out2chance, energy);
	}

	public static void addCrucibleRecipe(ItemStack in, FluidStack f, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("output", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		f.writeToNBT(toSend.getCompoundTag("output"));
		FMLInterModComms.sendMessage("ThermalExpansion", "CrucibleRecipe", toSend);
		fireEvent(ThermalRecipeEvent.ThermalMachine.CRUCIBLE, in, f, energy);
	}

	private static void addTwoInTwoOutWithChance(String type, ItemStack in1, ItemStack in2, ItemStack out1, ItemStack out2, int out2chance, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setTag("primaryInput", new NBTTagCompound());
		toSend.setTag("secondaryInput", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());
		if (out2 != null)
			toSend.setTag("secondaryOutput", new NBTTagCompound());

		in1.writeToNBT(toSend.getCompoundTag("primaryInput"));
		in2.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		out1.writeToNBT(toSend.getCompoundTag("primaryOutput"));
		if (out2 != null)
			out2.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
		if (out2chance < 100)
			toSend.setInteger("secondaryChance", out2chance);
		FMLInterModComms.sendMessage("ThermalExpansion", type, toSend);
		fireEvent(ThermalRecipeEvent.ThermalMachine.getType(type), in1, in2, out1, out2, out2chance, energy);
	}

	private static void addOneInTwoOutWithChance(String type, ItemStack in, ItemStack out1, ItemStack out2, int out2chance, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setTag("input", new NBTTagCompound());
		toSend.setTag("primaryOutput", new NBTTagCompound());
		if (out2 != null)
			toSend.setTag("secondaryOutput", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		out1.writeToNBT(toSend.getCompoundTag("primaryOutput"));
		if (out2 != null)
			out2.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
		if (out2chance < 100)
			toSend.setInteger("secondaryChance", out2chance);
		FMLInterModComms.sendMessage("ThermalExpansion", type, toSend);
		fireEvent(ThermalRecipeEvent.ThermalMachine.getType(type), in, null, out1, out2, out2chance, energy);
	}

}
