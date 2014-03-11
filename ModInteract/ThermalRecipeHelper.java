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
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;

public class ThermalRecipeHelper {

	public static void addFluidTransposerFill(ItemStack in, ItemStack out, int energy, FluidStack f) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setCompoundTag("input", new NBTTagCompound());
		toSend.setCompoundTag("output", new NBTTagCompound());
		toSend.setCompoundTag("fluid", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		out.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", true);
		f.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage("ThermalExpansion", "TransposerFillRecipe", toSend);
	}

	public static void addFluidTransposerDrain(ItemStack in, ItemStack out, int energy, FluidStack f, int chance) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setCompoundTag("input", new NBTTagCompound());
		toSend.setCompoundTag("output", new NBTTagCompound());
		toSend.setCompoundTag("fluid", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		out.writeToNBT(toSend.getCompoundTag("output"));
		toSend.setBoolean("reversible", true);
		toSend.setInteger("chance", chance);
		f.writeToNBT(toSend.getCompoundTag("fluid"));
		FMLInterModComms.sendMessage("ThermalExpansion", "TransposerExtractRecipe", toSend);
	}

	public static void addInductionSmelter(ItemStack in1, ItemStack in2, ItemStack out, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setCompoundTag("primaryInput", new NBTTagCompound());
		toSend.setCompoundTag("secondaryInput", new NBTTagCompound());
		toSend.setCompoundTag("primaryOutput", new NBTTagCompound());

		in1.writeToNBT(toSend.getCompoundTag("primaryInput"));
		in2.writeToNBT(toSend.getCompoundTag("secondaryInput"));
		out.writeToNBT(toSend.getCompoundTag("primaryOutput"));
		FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);
	}

	public static void addCrucibleRecipe(ItemStack in, FluidStack f, int energy) {
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setInteger("energy", energy);
		toSend.setCompoundTag("input", new NBTTagCompound());
		toSend.setCompoundTag("output", new NBTTagCompound());

		in.writeToNBT(toSend.getCompoundTag("input"));
		f.writeToNBT(toSend.getCompoundTag("output"));
		FMLInterModComms.sendMessage("ThermalExpansion", "CrucibleRecipe", toSend);
	}

}
