/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;

public final class ReikaNBTHelper extends DragonAPICore {

	/** Saves an inventory to NBT. Args: Inventory, NBT Tag */
	public static void writeInvToNBT(ItemStack[] inv, NBTTagCompound NBT) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

	/** Reads an inventory from NBT. Args: NBT Tag */
	public static ItemStack[] getInvFromNBT(NBTTagCompound NBT) {
		NBTTagList nbttaglist = NBT.getTagList("Items");
		ItemStack[] inv = new ItemStack[nbttaglist.tagCount()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length)
			{
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		return inv;
	}

	public static Fluid getFluidFromNBT(NBTTagCompound NBT) {
		String name = NBT.getString("liquid");
		if (name == null || name.isEmpty() || name.equals("empty"))
			return null;
		return FluidRegistry.getFluid(name);
	}

	public static void writeFluidToNBT(NBTTagCompound NBT, Fluid f) {
		String name = f != null ? f.getName() : "empty";
		NBT.setString("liquid", name);
	}

	public static Object getValue(NBTBase NBT) {
		if (NBT instanceof NBTTagInt) {
			return ((NBTTagInt)NBT).data;
		}
		else if (NBT instanceof NBTTagByte) {
			return ((NBTTagByte)NBT).data;
		}
		else if (NBT instanceof NBTTagShort) {
			return ((NBTTagShort)NBT).data;
		}
		else if (NBT instanceof NBTTagLong) {
			return ((NBTTagLong)NBT).data;
		}
		else if (NBT instanceof NBTTagFloat) {
			return ((NBTTagFloat)NBT).data;
		}
		else if (NBT instanceof NBTTagDouble) {
			return ((NBTTagDouble)NBT).data;
		}
		else if (NBT instanceof NBTTagIntArray) {
			return ((NBTTagIntArray)NBT).intArray;
		}
		else if (NBT instanceof NBTTagString) {
			return ((NBTTagString)NBT).data;
		}
		else if (NBT instanceof NBTTagByteArray) {
			return ((NBTTagByteArray)NBT).byteArray;
		}
		else if (NBT instanceof NBTTagCompound) {
			return NBT;
		}
		else if (NBT instanceof NBTBase) {
			return NBT;
		}
		else {
			return null;
		}
	}

	public static boolean isIntNumberTag(NBTBase tag) {
		return tag instanceof NBTTagInt || tag instanceof NBTTagByte || tag instanceof NBTTagShort || tag instanceof NBTTagLong;
	}

	public static NBTBase compressNumber(NBTBase tag) {
		if (!isIntNumberTag(tag))
			throw new MisuseException("Only integer-type numbers (byte, short, int, and long) can be compressed!");
		String name = tag.getName();
		long value = (Long)getValue(tag);
		if (value > Integer.MAX_VALUE) {
			return new NBTTagLong(name, value);
		}
		else if (value > Short.MAX_VALUE) {
			return new NBTTagInt(name, (int)value);
		}
		else if (value > Byte.MAX_VALUE) {
			return new NBTTagShort(name, (short)value);
		}
		else {
			return new NBTTagByte(name, (byte)value);
		}
	}

	public static ArrayList<String> parseNBTAsLines(NBTTagCompound nbt) {
		ArrayList<String> li = new ArrayList();
		Iterator<NBTBase> it = nbt.getTags().iterator();
		while (it.hasNext()) {
			NBTBase b = it.next();
			if (b instanceof NBTTagByteArray) {
				li.add(b.getName()+": "+Arrays.toString(((NBTTagByteArray)b).byteArray));
			}
			else if (b instanceof NBTTagIntArray) {
				li.add(b.getName()+": "+Arrays.toString(((NBTTagIntArray)b).intArray));
			}
			else if (b instanceof NBTTagCompound) {
				li.add(EnumChatFormatting.GOLD+b.getName()+": "+b.toString());
			}
			else {
				li.add(b.getName()+": "+b.toString());
			}
		}
		return li;
	}

}
