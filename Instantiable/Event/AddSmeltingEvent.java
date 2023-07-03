/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class AddSmeltingEvent extends Event {

	private final ItemStack input;
	private final ItemStack output;

	public final float originalXP;
	public float experienceValue;

	private boolean isInvalid = false;

	public static boolean isVanillaPass = false;

	public AddSmeltingEvent(ItemStack in, ItemStack out, float xp) {
		this(in, out, xp, isVanillaPass);
	}

	public AddSmeltingEvent(ItemStack in, ItemStack out, float xp, boolean v) {
		input = in;
		output = out;

		this.validate();

		originalXP = xp;
		experienceValue = xp;

		isVanillaPass = v;
	}

	public ItemStack getInput() {
		return input == null ? null : input.copy();
	}

	public ItemStack getOutput() {
		return output == null ? null : output.copy();
	}

	public void markInvalid() {
		isInvalid = true;
		this.setCanceled(true);
	}

	@Override
	public void setCanceled(boolean cancel) {
		if (isInvalid)
			cancel = true;
		super.setCanceled(cancel);
	}

	@Override
	public boolean isCanceled() {
		return super.isCanceled() || isInvalid;
	}

	public boolean isValid() {
		return !isInvalid;
	}

	private void validate() {
		if (input == null || input.getItem() == null) {
			DragonAPICore.logError("Found a null-input (or null-item input) smelting recipe! "+null+" > "+output+"! This is invalid!");
			Thread.dumpStack();
			isInvalid = true;
		}
		else if (output == null || output.getItem() == null) {
			DragonAPICore.logError("Found a null-output (or null-item output) smelting recipe! "+input+" > "+null+"! This is invalid!");
			Thread.dumpStack();
			isInvalid = true;
		}
		else if (!ReikaItemHelper.verifyItemStack(input, true)) {
			DragonAPICore.logError("Found a smelting recipe with an invalid input!");
			Thread.dumpStack();
			isInvalid = true;
		}
		else if (!ReikaItemHelper.verifyItemStack(output, true)) {
			DragonAPICore.logError("Found a smelting recipe with an invalid output!");
			Thread.dumpStack();
			isInvalid = true;
		}
	}

	/** Returns true if recipe was added. */
	public static boolean fire(ItemStack in, ItemStack out, float xp) {
		AddSmeltingEvent evt = new AddSmeltingEvent(in, out, xp);
		return !MinecraftForge.EVENT_BUS.post(evt);
	}

	private static String toString(ItemStack in) {
		return in == null ? "null" : (in.getItem() == null ? "null-item" : in.toString());
	}

}
