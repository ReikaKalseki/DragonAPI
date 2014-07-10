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
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public final class SyncPacket extends Packet132TileEntityData {

	private final HashMap<Integer, Object> data = new HashMap();
	private final HashMap<Integer, Object> oldData = new HashMap();
	private static final HashMap<Integer, ArgType> args = new HashMap(); //need to client/server sync!
	private static final HashMap<Integer, String> keyMap = new HashMap();

	public SyncPacket () {
		super();
	}

	public void setData(NBTTagCompound NBT) {

	}

	public void setInteger(String key, int value) {
		this.addData(key, value, ArgType.INT);
	}
	/*
	public void setLong(String key, long value) {
		this.addData(key, value, ArgType.LONG);
	}

	public void setShort(String key, short value) {
		this.addData(key, value, ArgType.SHORT);
	}

	public void setByte(String key, byte value) {
		this.addData(key, value, ArgType.BYTE);
	}*/

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
	/*
	public void setIntArray(String key, int[] value) {
		this.addData(key, value, ArgType.ARRAY);
	}*/

	private void addData(String label, Object value, ArgType arg) {
		int key = this.getKey(label);
		Object prev = data.get(key);
		oldData.put(key, prev);
		data.put(key, value);
		args.put(key, arg);
	}

	public int getInteger(String key) {
		Integer val = (Integer)data.get(this.getKey(key));
		return val != null ? val.intValue() : 0;
	}

	public float getFloat(String key) {
		Float val = (Float)data.get(this.getKey(key));
		return val != null ? val.floatValue() : 0;
	}

	public double getDouble(String key) {
		Double val = (Double)data.get(this.getKey(key));
		return val != null ? val.doubleValue() : 0;
	}

	public boolean getBoolean(String key) {
		Boolean val = (Boolean)data.get(this.getKey(key));
		return val != null ? val.booleanValue() : false;
	}

	public String getString(String key) {
		return (String)data.get(this.getKey(key));
	}

	public long getLong(String key) {
		Long val = (Long)data.get(this.getKey(key));
		return val != null ? val.longValue() : 0;
	}

	public short getShort(String key) {
		Short val = (Short)data.get(this.getKey(key));
		return val != null ? val.shortValue() : 0;
	}

	public byte getByte(String key) {
		Byte val = (Byte)data.get(this.getKey(key));
		return val != null ? val.byteValue() : 0;
	}

	private static enum ArgType {
		INT(),
		BOOLEAN(),
		FLOAT(),
		DOUBLE(),
		STRING(),
		//NBTTAG(),
		ARRAY(),
		SHORT(),
		LONG(),
		BYTE(),
		FLUIDSTACK(),
		ITEMSTACK();
	}
	/*
	private short getKey(String name) {
		short key = 0;
		if (key == 0 || data.containsKey(key)) { //hash collision protection
			key = this.getRandomKey();
		}
		keyMap.put(key, name);
		return key;
	}

	private static short getRandomKey() {
		short key = ReikaRandomHelper.getRandomShort();
		while (keyMap.containsKey(key))
			key = ReikaRandomHelper.getRandomShort();
		return key;
	}*/

	private int getKey(String name) {
		int key = name.hashCode();
		if (key == 0 || data.containsKey(key)) { //hash collision protection
			throw new HashCollisionException(name);
		}
		keyMap.put(key, name);
		return key;
	}

	private static final class HashCollisionException extends IllegalArgumentException {

		private HashCollisionException(String s) {
			super(s+" creates a conflicting hash in the SyncPacket!");
		}

	}

	@Override
	public void readPacketData(DataInput in) throws IOException {
		byte keys = in.readByte();
		for (int i = 0; i < keys; i++) {
			int key = in.readShort();
			Object obj = this.readIn(key, in);
			if (obj != null)
				data.put(key, obj);
		}
	}

	@Override
	public void writePacketData(DataOutput out) throws IOException {
		Iterator<Integer> keys = data.keySet().iterator();
		while (keys.hasNext()) {
			int key = keys.next();
			Object old = oldData.get(key);
			Object cur = data.get(key);
			if (this.match(old, cur))
				keys.remove();
		}
		out.writeByte(data.size());
		for (int key : data.keySet()) {
			out.writeInt(key);
			this.writeOut(key, out);
		}
	}

	private boolean match(Object old, Object cur) {
		if (old == cur)
			return true;
		if (old == null || cur == null)
			return false;
		return cur.equals(old);
	}

	private void writeOut(int key, DataOutput out) throws IOException {
		Object obj = data.get(key);
		switch(args.get(key)) {
		case ARRAY:
			break;
		case BOOLEAN:
			out.writeBoolean((Boolean)obj);
			break;
		case BYTE:
			out.writeByte((Byte)obj);
			break;
		case DOUBLE:
			out.writeDouble((Double)obj);
			break;
		case FLOAT:
			out.writeFloat((Float)obj);
			break;
		case FLUIDSTACK:
			break;
		case INT:
			out.writeInt((Integer)obj);
			break;
		case ITEMSTACK:
			break;
		case LONG:
			out.writeLong((Long)obj);
			break;
		case SHORT:
			out.writeShort((Short)obj);
			break;
		case STRING:
			out.writeUTF((String)obj);
			break;
		default:
			break;
		}
	}

	private Object readIn(int key, DataInput in) throws IOException {
		Object obj = data.get(key);
		switch(args.get(key)) {
		case BOOLEAN:
			return in.readBoolean();
		case BYTE:
			return in.readByte();
		case DOUBLE:
			return in.readDouble();
		case FLOAT:
			return in.readFloat();
		case INT:
			return in.readInt();
		case LONG:
			return in.readLong();
		case SHORT:
			return in.readShort();
		case STRING:
			return in.readUTF();
		default:
			return null;
		}
	}

	@Override
	public void processPacket(NetHandler nh) {
		nh.unexpectedPacket(this);
	}

	@Override
	public int getPacketSize() {
		return data.keySet().size()*4+data.entrySet().size()*8;
	}

	public void localize(TileEntity te) {
		xPosition = te.xCoord;
		yPosition = te.yCoord;
		zPosition = te.zCoord;
	}

}
