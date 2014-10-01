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
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
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
		NBTTagList nbttaglist = NBT.getTagList("Items", NBTTypes.COMPOUND.ID);
		ItemStack[] inv = new ItemStack[nbttaglist.tagCount()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
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
			return ((NBTTagInt)NBT).func_150287_d();
		}
		else if (NBT instanceof NBTTagByte) {
			return ((NBTTagByte)NBT).func_150290_f();
		}
		else if (NBT instanceof NBTTagShort) {
			return ((NBTTagShort)NBT).func_150289_e();
		}
		else if (NBT instanceof NBTTagLong) {
			return ((NBTTagLong)NBT).func_150291_c();
		}
		else if (NBT instanceof NBTTagFloat) {
			return ((NBTTagFloat)NBT).func_150288_h();
		}
		else if (NBT instanceof NBTTagDouble) {
			return ((NBTTagDouble)NBT).func_150286_g();
		}
		else if (NBT instanceof NBTTagIntArray) {
			return ((NBTTagIntArray)NBT).func_150302_c();
		}
		else if (NBT instanceof NBTTagString) {
			return ((NBTTagString)NBT).func_150285_a_();
		}
		else if (NBT instanceof NBTTagByteArray) {
			return ((NBTTagByteArray)NBT).func_150292_c();
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

	public static boolean isNumberTag(NBTBase tag) {
		return isIntNumberTag(tag) || tag instanceof NBTTagFloat || tag instanceof NBTTagDouble;
	}

	public static NBTBase compressNumber(NBTBase tag) {
		if (!isIntNumberTag(tag))
			throw new MisuseException("Only integer-type numbers (byte, short, int, and long) can be compressed!");
		long value = (Long)getValue(tag);
		if (value > Integer.MAX_VALUE) {
			return new NBTTagLong(value);
		}
		else if (value > Short.MAX_VALUE) {
			return new NBTTagInt((int)value);
		}
		else if (value > Byte.MAX_VALUE) {
			return new NBTTagShort((short)value);
		}
		else {
			return new NBTTagByte((byte)value);
		}
	}

	public static ArrayList<String> parseNBTAsLines(NBTTagCompound nbt) {
		ArrayList<String> li = new ArrayList();
		Iterator<NBTBase> it = nbt.func_150296_c().iterator();
		for (Object o : nbt.func_150296_c()) {
			String key = (String)o;
			NBTBase b = nbt.getTag(key);/*
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
			}*/
			li.add(key+": "+b.toString());
		}
		return li;
	}

	public static enum NBTTypes {
		INT(new NBTTagInt(0).getId()),
		BYTE(new NBTTagByte((byte)0).getId()),
		SHORT(new NBTTagShort((short)0).getId()),
		FLOAT(new NBTTagFloat(0).getId()),
		DOUBLE(new NBTTagDouble(0).getId()),
		LONG(new NBTTagLong(0).getId()),
		INTA(new NBTTagIntArray(new int[0]).getId()),
		BYTEA(new NBTTagByteArray(new byte[0]).getId()),
		STRING(new NBTTagString("").getId()),
		LIST(new NBTTagList().getId()),
		COMPOUND(new NBTTagCompound().getId()),
		END(new NBTTagEnd().getId());

		public final int ID;

		private NBTTypes(int id) {
			ID = id;
		}
	}

}
