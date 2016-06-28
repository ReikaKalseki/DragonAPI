/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.io;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.auxiliary.trackers.TickRegistry.TickHandler;
import reika.dragonapi.auxiliary.trackers.TickRegistry.TickType;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.dragonapi.interfaces.DataSync;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class CompoundSyncPacket extends Packet implements DataSync {

	private static final String ERROR_TAG = "erroredPacket";

	public static final CompoundSyncPacket instance = new CompoundSyncPacket();

	private final HashMap<WorldLocation, HashMap<String, NBTBase>> data = new HashMap();
	private final HashMap<WorldLocation, HashMap<String, NBTBase>> oldData = new HashMap();
	private final HashMap<WorldLocation, HashMap<String, NBTBase>> changes = new HashMap();

	/** Is the packet currently being written by the network thread */
	private boolean dispatch;
	/** Is the packet currently being read by the network thread */
	private boolean receive;

	private CompoundSyncPacket() {

	}

	public void setData(TileEntity te, boolean force, NBTTagCompound NBT) {
		if (dispatch) {
			if (DragonOptions.LOGSYNCCME.getState()) {
				DragonAPICore.log("The compound sync packet for "+te+" would have just CME'd, as the");
				DragonAPICore.log("Server-Thread data-writing code has overlapped with the Network-Thread byte[] dispatch.");
				DragonAPICore.log("Seeing this message frequently could indicate a serious issue.\n");
			}
			return;
		}

		WorldLocation loc = new WorldLocation(te);

		this.createMaps(loc);
		changes.remove(loc);

		Collection c = NBT.func_150296_c();
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (name == null) {
				DragonAPICore.log("An NBT tag with a null key is being sent to the compound sync packet from "+te);
			}
			else {
				NBTBase tag = NBT.getTag(name);
				this.addData(loc, name, tag, force);
			}
		}
	}

	private void createMaps(WorldLocation loc) {
		if (data.get(loc) == null) {
			data.put(loc, new HashMap());
		}
		if (oldData.get(loc) == null) {
			oldData.put(loc, new HashMap());
		}
	}

	private void addData(WorldLocation loc, String key, NBTBase value, boolean force) {
		NBTBase prev = data.get(loc).get(key);
		oldData.get(loc).put(key, prev);
		data.get(loc).put(key, value);
		if (force || !this.match(prev, value)) {
			//DragonAPICore.log("Changing '"+key+"' from "+prev+" to "+value+" @ "+loc.getTileEntity());
			this.addChange(loc, key, value);
		}
	}

	private void addChange(WorldLocation loc, String key, NBTBase value) {
		HashMap<String, NBTBase> map = changes.get(loc);
		if (map == null) {
			map = new HashMap();
			changes.put(loc, map);
		}
		map.put(key, value);
	}

	public boolean isEmpty() {
		return changes.isEmpty();
	}

	public boolean isEmpty(WorldLocation loc) {
		return changes.get(loc).isEmpty();
	}

	@SideOnly(Side.CLIENT)
	public void readForSync(TileEntity te, NBTTagCompound NBT) {
		if (dispatch) {
			if (DragonOptions.LOGSYNCCME.getState()) {
				DragonAPICore.log("The compound sync packet for "+te+" would have just CME'd, as the");
				DragonAPICore.log("Client-Thread data-reading code has overlapped with the Network-Thread byte[] reading.");
				DragonAPICore.log("Seeing this message frequently could indicate a serious issue.\n");
			}
			return;
		}

		WorldLocation loc = new WorldLocation(te);

		this.createMaps(loc);

		for (String key : data.get(loc).keySet()) {
			NBT.setTag(key, data.get(loc).get(key));
		}
	}

	@Override
	public String toString() {
		return changes.isEmpty() ? "[Empty]" : changes.toString();
	}

	private boolean match(NBTBase old, NBTBase cur) {
		if (old == cur)
			return true;
		if (old == null || cur == null)
			return false;
		return cur.equals(old);
	}

	@Override
	public void writePacketData(PacketBuffer out) throws IOException {
		dispatch = true;

		out.writeInt(changes.size());
		for (WorldLocation loc : changes.keySet()) {
			out.writeInt(loc.dimensionID);
			out.writeInt(loc.xCoord);
			out.writeShort(loc.yCoord);
			out.writeInt(loc.zCoord);
		}

		NBTTagCompound toSend = new NBTTagCompound();

		for (WorldLocation loc : changes.keySet()) {
			HashMap<String, NBTBase> map = changes.get(loc);
			try {
				NBTTagCompound local = new NBTTagCompound();
				this.saveChanges(map, local);
				toSend.setTag(loc.toSerialString(), local);
			}
			catch (Exception e) {
				toSend.setBoolean(ERROR_TAG, true);
				e.printStackTrace();
				//out.clear();
			}
		}

		try {
			out.writeNBTTagCompoundToBuffer(toSend);
		}
		catch (Exception e) {
			DragonAPICore.logError("Error writing Compound Sync Tag!");
			out.clear();
			e.printStackTrace();
		}

		DragonAPICore.log("Wrote "+changes.size()+" locations, data="+toSend);

		dispatch = false;
	}

	private void saveChanges(HashMap<String, NBTBase> changes, NBTTagCompound loc) {
		Iterator<String> keys = changes.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			NBTBase val = changes.get(key);
			loc.setTag(key, val);
		}
	}
	/*
	private void trim() {
		HashSet<WorldLocation> remove = new HashSet();
		for (WorldLocation loc : changes.keySet()) {
			if (changes.get(loc).isEmpty()) {
				remove.add(loc);
			}
		}
		for (WorldLocation loc : remove)
			changes.remove(loc);
	}
	 */
	@Override
	public void readPacketData(PacketBuffer in) throws IOException {
		receive = true;
		try {
			int num = in.readInt();
			for (int i = 0; i < num; i++) {
				int dim = in.readInt();
				int x = in.readInt();
				int y = in.readShort();
				int z = in.readInt();
				WorldLocation loc = new WorldLocation(dim, x, y, z);
				this.createMaps(loc);
			}

			NBTTagCompound received = in.readNBTTagCompoundFromBuffer();
			if (!received.getBoolean(ERROR_TAG)) {
				Collection c = received.func_150296_c();
				Iterator<String> it = c.iterator();
				while (it.hasNext()) {
					String name = it.next();
					NBTTagCompound local = received.getCompoundTag(name);
					WorldLocation loc = WorldLocation.fromSerialString(name);
					//try {
					this.populateFromStream(loc, local);
					//}
					//catch (Exception e) {
					//	e.printStackTrace();
					//	data.clear(); //discard packet
					//}
				}
			}
		}
		catch (Exception e) {
			DragonAPICore.logError("Error reading Compound Sync Tag!");
			e.printStackTrace();
			data.clear();
		}
		receive = false;
	}

	private void populateFromStream(WorldLocation loc, NBTTagCompound local) {
		Collection c = local.func_150296_c();
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
			String name = it.next();
			NBTBase tag = local.getTag(name);
			data.get(loc).put(name, tag);
		}
		DragonAPICore.log("Reading "+data.get(loc)+" from "+local+" @ "+loc.getTileEntity());
	}

	@Override
	public boolean hasNoData() {
		return data.isEmpty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void processPacket(INetHandler inh) { //Ignore default handling
		World world = Minecraft.getMinecraft().theWorld;
		for (WorldLocation loc : data.keySet()) {
			if (loc.dimensionID == world.provider.dimensionId) {
				if (world.blockExists(loc.xCoord, loc.yCoord, loc.zCoord)) {
					TileEntity te = world.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
					if (te instanceof CompoundSyncPacketHandler) {
						((CompoundSyncPacketHandler)te).handleCompoundSyncPacket(this);
					}
				}
			}
		}
	}

	public static interface CompoundSyncPacketHandler {

		public void handleCompoundSyncPacket(CompoundSyncPacket packet);

	}

	public static class CompoundSyncPacketTracker implements TickHandler {

		public static final CompoundSyncPacketTracker instance = new CompoundSyncPacketTracker();

		private int tickcount;
		private static final int MAXTICK = DragonOptions.SLOWSYNC.getState() ? 20 : 4;

		private CompoundSyncPacketTracker() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			tickcount++;
			if (tickcount >= MAXTICK) {
				if (!CompoundSyncPacket.instance.isEmpty())
					this.dispatchPacket((World)tickData[0]);
				tickcount = 0;
			}
		}

		private void dispatchPacket(World world) {
			for (EntityPlayerMP ep : ((List<EntityPlayerMP>)world.playerEntities))  {
				ep.playerNetServerHandler.sendPacket(CompoundSyncPacket.instance);
			}
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.WORLD);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.END;
		}

		@Override
		public String getLabel() {
			return "Compound Sync Packet";
		}

	}

}
