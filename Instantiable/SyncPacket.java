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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet132TileEntityData;
import Reika.DragonAPI.Base.TileEntityBase;

public final class SyncPacket extends Packet132TileEntityData {

	private final HashMap<String, NBTBase> data = new HashMap();
	private final HashMap<String, NBTBase> oldData = new HashMap();
	private final HashMap<String, NBTBase> changes = new HashMap();

	private static final String ERROR_TAG = "erroredPacket";

	public SyncPacket () {
		super();
	}

	public void setData(TileEntityBase te, boolean force, NBTTagCompound NBT) {
		xPosition = te.xCoord;
		yPosition = te.yCoord;
		zPosition = te.zCoord;

		changes.clear();
		Collection c = NBT.getTags();
		Iterator<NBTBase> it = c.iterator();
		while (it.hasNext()) {
			NBTBase tag = it.next();
			this.addData(tag.getName(), tag, force);
		}
	}

	private void addData(String key, NBTBase value, boolean force) {
		NBTBase prev = data.get(key);
		oldData.put(key, prev);
		data.put(key, value);
		if (force || !this.match(prev, value)) {
			changes.put(key, value);
		}
	}

	public boolean isEmpty() {
		return changes.isEmpty();
	}

	@Override
	public void readPacketData(DataInput in) throws IOException {
		xPosition = in.readInt();
		yPosition = in.readShort();
		zPosition = in.readInt();

		NBTTagCompound received = readNBTTagCompound(in);
		if (!received.getBoolean(ERROR_TAG))
			this.populateFromStream(received);
	}

	private void populateFromStream(NBTTagCompound received) {
		Collection c = received.getTags();
		Iterator<NBTBase> it = c.iterator();
		while (it.hasNext()) {
			NBTBase tag = it.next();
			data.put(tag.getName(), tag);
		}
	}

	public void writeToNBT(NBTTagCompound NBT) {
		for (String key : data.keySet()) {
			NBT.setTag(key, data.get(key));
		}
	}

	@Override
	public void writePacketData(DataOutput out) throws IOException {
		out.writeInt(xPosition);
		out.writeShort(yPosition);
		out.writeInt(zPosition);

		NBTTagCompound toSend = new NBTTagCompound();
		try {
			this.saveChanges(toSend);
		}
		catch (Exception e) {
			toSend.setBoolean(ERROR_TAG, true);
			e.printStackTrace();
		}
		writeNBTTagCompound(toSend, out);
	}

	private void saveChanges(NBTTagCompound toSend) {
		Iterator<String> keys = changes.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			NBTBase val = changes.get(key);
			toSend.setTag(key, val);
		}
	}

	private boolean match(NBTBase old, NBTBase cur) {
		if (old == cur)
			return true;
		if (old == null || cur == null)
			return false;
		return cur.equals(old);
	}

	@Override
	public String toString() {
		return changes.isEmpty() ? "[Empty]" : changes.toString();
	}

}
