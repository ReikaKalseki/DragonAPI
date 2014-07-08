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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public final class SyncPacket extends Packet {

	private final HashMap<Integer, Object> data = new HashMap();
	private final HashMap<Integer, ArgType> args = new HashMap();
	private final HashMap<Integer, Object> oldData = new HashMap();
	private static final HashMap<Integer, String> keyMap = new HashMap();
	private static final Random rand = new Random();

	public void setInteger(String key, int value) {
		this.addData(key, value, ArgType.INT);
	}

	public void setLong(String key, long value) {
		this.addData(key, value, ArgType.NBTTAG);
	}

	public void setShort(String key, short value) {
		this.addData(key, value, ArgType.NBTTAG);
	}

	public void setByte(String key, byte value) {
		this.addData(key, value, ArgType.NBTTAG);
	}

	public void setFloat(String key, float value) {
		this.addData(key, value, ArgType.FLOAT);
	}

	public void setDouble(String key, double value) {
		this.addData(key, value, ArgType.DOUBLE);
	}

	public void setBoolean(String key, boolean value) {
		this.addData(key, value, ArgType.BOOLEAN);
	}

	public void setString(String key, String value) {
		this.addData(key, value, ArgType.STRING);
	}

	public void setIntArray(String key, int[] value) {
		this.addData(key, value, ArgType.ARRAY);
	}

	public void setTag(String key, NBTBase value) {
		this.addData(key, value, ArgType.NBTTAG);
	}

	private void addData(String label, Object value, ArgType arg) {
		int key = this.getKey(label);
		data.put(key, value);
		args.put(key, arg);
	}

	public void writeToNBT(NBTTagCompound NBT) {
		for (int key : data.keySet()) {
			ArgType arg = args.get(key);
			String s = String.valueOf(key);
			Object obj = data.get(key);
			switch(arg) {
			case ARRAY:
				NBT.setIntArray(s, (int[])obj);
				break;
			case BOOLEAN:
				break;
			case BYTE:
				break;
			case DOUBLE:
				break;
			case FLOAT:
				break;
			case INT:
				break;
			case LONG:
				break;
			case NBTTAG:
				break;
			case SHORT:
				break;
			case STRING:
				break;
			default:
				break;
			}
		}
	}

	public void readFromNBT(NBTTagCompound NBT) {

	}

	private static enum ArgType {
		INT(),
		BOOLEAN(),
		FLOAT(),
		DOUBLE(),
		STRING(),
		NBTTAG(),
		ARRAY(),
		SHORT(),
		LONG(),
		BYTE(),
		FLUIDSTACK(),
		ITEMSTACK();
	}

	private int getKey(String name) {
		int key = name.hashCode();
		if (data.containsKey(key)) { //hash collision protection
			key = this.getRandomKey();
		}
		keyMap.put(key, name);
		return key;
	}

	private static int getRandomKey() {
		int key = rand.nextInt();
		while (keyMap.containsKey(key))
			key = rand.nextInt();
		return key;
	}

	private static final class HashCollisionException extends IllegalArgumentException {

		private HashCollisionException(String s) {
			super(s+" creates a conflicting hash in the SyncPacket!");
		}

	}

	@Override
	public void readPacketData(DataInput in) throws IOException {

	}

	@Override
	public void writePacketData(DataOutput out) throws IOException {
		for (int key : data.keySet()) {

		}
	}

	@Override
	public void processPacket(NetHandler nh) {

	}

	@Override
	public int getPacketSize() {
		return 0;
	}

}
