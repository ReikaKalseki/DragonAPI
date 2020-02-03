package Reika.DragonAPI.ModInteract.DeepInteract;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

public class AEPatternHandling {

	public static boolean isCraftingRecipe(ItemStack is, World world) {
		ICraftingPatternDetails icpd = ((ICraftingPatternItem)is.getItem()).getPatternForItem(is, world);
		return icpd != null && icpd.isCraftable();
	}

	public static ItemStack[] getPatternInput(ItemStack is, World world) {
		ICraftingPatternDetails icpd = ((ICraftingPatternItem)is.getItem()).getPatternForItem(is, world);
		ItemStack[] ret = new ItemStack[9];
		IAEItemStack[] in = icpd.getInputs();
		for (int i = 0; i < ret.length && i < in.length; i++) {
			if (in[i] != null)
				ret[i] = in[i].getItemStack();
		}
		return ret;
	}

	public static ArrayList<ItemStack> getPatternOutputs(ItemStack is, World world) {
		ICraftingPatternDetails icpd = ((ICraftingPatternItem)is.getItem()).getPatternForItem(is, world);
		IAEItemStack[] out = icpd.getCondensedOutputs();
		ArrayList<ItemStack> li = new ArrayList();
		for (IAEItemStack iae : out) {
			li.add(iae.getItemStack());
		}
		return li;
	}

	public static ItemStack getEncodedPattern(ItemStack out, boolean isCrafting, boolean allowOreSubs, ItemStack... in) {
		return getEncodedPattern(ReikaJavaLibrary.makeListFrom(out), isCrafting, allowOreSubs, in);
	}

	public static ItemStack getEncodedPattern(Collection<ItemStack> out, boolean isCrafting, boolean allowOreSubs, ItemStack... in) {
		ItemStack output = new ItemStack(AppEngHandler.getInstance().getEncodedPattern());
		NBTTagCompound encodedValue = new NBTTagCompound();

		NBTTagList tagIn = new NBTTagList();
		NBTTagList tagOut = new NBTTagList();
		for (ItemStack i : in) {
			tagIn.appendTag(createItemTag(i));
		}
		for (ItemStack i : out) {
			tagOut.appendTag(createItemTag(i));
		}
		encodedValue.setTag("in", tagIn);
		encodedValue.setTag("out", tagOut);
		encodedValue.setBoolean("crafting", isCrafting);
		encodedValue.setBoolean("substitute", allowOreSubs);

		output.setTagCompound(encodedValue);
		return output;
	}

	private static NBTBase createItemTag(ItemStack i) {
		NBTTagCompound c = new NBTTagCompound();
		if (i != null) {
			i.writeToNBT(c);
		}
		return c;
	}

}
