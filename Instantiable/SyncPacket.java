/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.HashMap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public final class SyncPacket {

	private final HashMap<String, Object> data = new HashMap();
	private final HashMap<String, Object> oldData = new HashMap();

	public void setInteger(String key, int value) {
		data.put(key, value);
	}

	public void setLong(String key, long value) {
		data.put(key, value);
	}

	public void setShort(String key, short value) {
		data.put(key, value);
	}

	public void setByte(String key, byte value) {
		data.put(key, value);
	}

	public void setFloat(String key, float value) {
		data.put(key, value);
	}

	public void setDouble(String key, double value) {
		data.put(key, value);
	}

	public void setBoolean(String key, boolean value) {
		data.put(key, value);
	}

	public void setString(String key, String value) {
		data.put(key, value);
	}

	public void setIntArray(String key, int[] value) {
		data.put(key, value);
	}

	public void setTag(String key, NBTBase value) {
		data.put(key, value);
	}

	public void writeToNBT(NBTTagCompound NBT) {
		for (String key : data.keySet()) {

		}
	}

	public void readFromNBT(NBTTagCompound NBT) {

	}

}
