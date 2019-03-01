/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Interfaces.DataSync;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class SyncPacket extends S35PacketUpdateTileEntity implements DataSync {

	private final HashMap<String, NBTBase> data = new HashMap();
	private final HashMap<String, NBTBase> oldData = new HashMap();
	private final HashMap<String, NBTBase> changes = new HashMap();

	/** Is the packet currently being written by the network thread */
	private boolean dispatch;
	/** Is the packet currently being read by the network thread */
	private boolean receive;

	private static final String ERROR_TAG = "erroredPacket";

	public SyncPacket () {
		super();
	}

	public void setData(TileEntity te, boolean force, NBTTagCompound NBT) {
		if (dispatch) {
			if (DragonOptions.LOGSYNCCME.getState()) {
				DragonAPICore.log("The sync packet for "+te+" would have just CME'd, as the");
				DragonAPICore.log("Server-Thread data-writing code has overlapped with the Network-Thread byte[] dispatch.");
				DragonAPICore.log("Seeing this message frequently could indicate a serious issue.\n");
			}
			return;
		}

		field_148863_a = te.xCoord;
		field_148861_b = te.yCoord;
		field_148862_c = te.zCoord;

		changes.clear();
		Collection c = NBT.func_150296_c();
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (name == null) {
				DragonAPICore.logError("An NBT tag with a null key is being sent to the sync packet from "+te);
			}
			else {
				NBTBase tag = NBT.getTag(name);
				this.addData(name, tag, force);
			}
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
	public void readPacketData(PacketBuffer in) throws IOException {
		receive = true;
		try {
			field_148863_a = in.readInt();
			field_148861_b = in.readShort();
			field_148862_c = in.readInt();

			NBTTagCompound received = in.readNBTTagCompoundFromBuffer();
			if (!received.getBoolean(ERROR_TAG)) {
				//try {
				this.populateFromStream(received);
				//}
				//catch (Exception e) {
				//	e.printStackTrace();
				//	data.clear(); //discard packet
				//}
			}
		}
		catch (Exception e) {
			DragonAPICore.logError("Error reading Sync Tag!");
			e.printStackTrace();
			data.clear();
		}
		receive = false;
	}

	private void populateFromStream(NBTTagCompound received) {
		Collection c = received.func_150296_c();
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
			String name = it.next();
			NBTBase tag = received.getTag(name);
			data.put(name, tag);
		}
	}

	@SideOnly(Side.CLIENT)
	public void readForSync(TileEntity te, NBTTagCompound NBT) {
		if (dispatch) {
			if (DragonOptions.LOGSYNCCME.getState()) {
				DragonAPICore.log("The sync packet for "+te+" would have just CME'd, as the");
				DragonAPICore.log("Client-Thread data-reading code has overlapped with the Network-Thread byte[] reading.");
				DragonAPICore.log("Seeing this message frequently could indicate a serious issue.\n");
			}
			return;
		}

		for (String key : data.keySet()) {
			NBT.setTag(key, data.get(key));
		}
	}

	@Override
	public void writePacketData(PacketBuffer out) throws IOException {
		dispatch = true;
		out.writeInt(field_148863_a);
		out.writeShort(field_148861_b);
		out.writeInt(field_148862_c);

		NBTTagCompound toSend = new NBTTagCompound();
		try {
			this.saveChanges(toSend);
		}
		catch (Exception e) {
			toSend.setBoolean(ERROR_TAG, true);
			e.printStackTrace();
			//out.clear();
		}
		try {
			out.writeNBTTagCompoundToBuffer(toSend);
		}
		catch (Exception e) {
			DragonAPICore.logError("Error writing Sync Tag!");
			out.clear();
			e.printStackTrace();
		}
		dispatch = false;
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

	public boolean hasNoData() {
		return data.isEmpty();
	}

}
