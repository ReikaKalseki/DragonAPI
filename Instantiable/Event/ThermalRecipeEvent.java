/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.item.ItemStack;

public class ThermalRecipeEvent {

	public static enum ThermalMachine {
		INDUCTION(),
		CRUCIBLE(),
		TRANSPOSER(),
		PULVERIZER(),
		SAWMILL();
	}

	public final ThermalMachine machine;
	private final ItemStack input1;
	private final ItemStack input2;
	private final ItemStack output1;
	private final ItemStack output2;
	public final int chanceForOutput2;
	public final int energy;

	public ThermalRecipeEvent(ThermalMachine type, ItemStack in1, ItemStack in2, ItemStack out1, ItemStack out2, int out2chance, int rf) {
		machine = type;
		input1 = in1;
		input2 = in2;
		output1 = out1;
		output2 = out2;
		chanceForOutput2 = out2chance;
		energy = rf;
	}

	public boolean hasSecondInput() {
		return input2 != null;
	}

	public boolean hasSecondOutput() {
		return output2 != null && chanceForOutput2 > 0;
	}

	public ItemStack getInput1() {
		return input1 != null ? input1.copy() : null;
	}

	public ItemStack getInput2() {
		return input2 != null ? input2.copy() : null;
	}

	public ItemStack getOutput1() {
		return output1 != null ? output1.copy() : null;
	}

	public ItemStack getOutput2() {
		return output2 != null ? output2.copy() : null;
	}

}
