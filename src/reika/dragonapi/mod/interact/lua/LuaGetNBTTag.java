/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.lua;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.libraries.ReikaNBTHelper;
import dan200.computercraft.api.lua.LuaException;


public class LuaGetNBTTag extends LuaMethod {

	public LuaGetNBTTag() {
		super("getNBTTag", TileEntity.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		NBTTagCompound nbt = new NBTTagCompound();
		te.writeToNBT(nbt);
		Object o = null;
		String tag = (String)args[0];
		NBTBase b = nbt.getTag(tag);
		if (b != null) {
			switch(ReikaNBTHelper.getTagType(b)) {
				case BYTE:
					o = ((NBTTagByte)b).func_150290_f();
					break;
				case DOUBLE:
					o = ((NBTTagDouble)b).func_150286_g();
					break;
				case FLOAT:
					o = ((NBTTagFloat)b).func_150288_h();
					break;
				case INT:
					o = ((NBTTagInt)b).func_150287_d();
					break;
				case LONG:
					o = ((NBTTagLong)b).func_150291_c();
					break;
				case SHORT:
					o = ((NBTTagShort)b).func_150289_e();
					break;
				case STRING:
					o = ((NBTTagString)b).func_150285_a_();
					break;
				case INTA:
					o = ((NBTTagIntArray)b).func_150302_c();
					break;
				case BYTEA:
					o = ((NBTTagByteArray)b).func_150292_c();
					break;
				case LIST:
				case COMPOUND:
					o = b.toString();
					break;
				case END:
					break;
			}
		}
		return o != null ? new Object[]{o} : null;
	}

	@Override
	public String getDocumentation() {
		return "Returns the value of an NBT tag.\nArgs: tagName\nReturns: tagValue";
	}

	@Override
	public String getArgsAsString() {
		return "String tagName";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.ARRAY;
	}

}
