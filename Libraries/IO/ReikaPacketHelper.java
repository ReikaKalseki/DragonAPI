/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.HybridTank;
import Reika.DragonAPI.Instantiable.IO.PacketPipeline;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.PacketHandler;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;

import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaPacketHelper extends DragonAPICore {

	private static final HashMap<String, PacketPipeline> pipelines = new HashMap();
	private static final HashBiMap<Short, PacketHandler> handlers = HashBiMap.create();
	private static short handlerID = 0;

	public static void registerPacketHandler(DragonAPIMod mod, String channel, PacketHandler handler) {
		SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(channel);
		PacketPipeline p = new PacketPipeline(mod, channel, handler, wrapper);
		p.registerPacket(DataPacket.class);
		//p.registerPacket(NBTPacket.class);
		handlers.put(handlerID, handler);
		pipelines.put(channel, p);
		handlerID++;
	}

	public static void registerPacketClass(String channel, Class<? extends PacketObj> c) {
		PacketPipeline pipe = pipelines.get(channel);
		if (pipe == null)
			throw new MisuseException("Cannot register a packet class to a null pipeline!");
		pipe.registerPacket(c);
	}

	private static short getHandlerID(PacketHandler handler) {
		return handlers.containsValue(handler) ? handlers.inverse().get(handler) : -1;
	}

	private static PacketHandler getHandlerFromID(short id) {
		return handlers.get(id);
	}
	/*
	public static void initPipelines() {
		for (PacketPipeline p : pipelines.values()) {
			p.initialize();
		}
	}

	public static void postInitPipelines() {
		for (PacketPipeline p : pipelines.values()) {
			p.postInitialize();
		}
	}*/

	public static void sendNIntPacket(String ch, int id, PacketTarget p, List<Integer> data) {
		int npars = 1+data.size(); //+1 for the size

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeInt(data.size());
			if (data != null) {
				for (int i : data) {
					outputStream.writeInt(i);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.PREFIXED, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		p.dispatch(pipe, pack);
	}

	public static void sendNIntPacket(String ch, int id, PacketTarget p, int... data) {
		sendNIntPacket(ch, id, p, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendRawPacket(String ch, ByteArrayOutputStream bos) {
		DataOutputStream outputStream = new DataOutputStream(bos);
		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.RAW, pipe);
		pack.setData(dat);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);

		}
		else if (side == Side.CLIENT) {
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, ByteArrayOutputStream bos) {
		DataOutputStream outputStream = new DataOutputStream(bos);
		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);

		}
		else if (side == Side.CLIENT) {
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, int id, EntityPlayerMP ep, int... data) {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < data.length; i++) {
			li.add(data[i]);
		}
		sendDataPacket(ch, id, li, ep);
	}

	public static void sendDataPacket(String ch, int id, List<Integer> data, EntityPlayerMP ep) {
		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeInt(data.get(i));
				}

			outputStream.writeInt(0);
			outputStream.writeInt(0); //xyz
			outputStream.writeInt(0);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		//PacketDispatcher.sendPacketToPlayer(packet, (Player)ep);
		pipe.sendToPlayer(pack, ep);
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayerMP ep, int... data) {
		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.length+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.length; i++) {
					outputStream.writeInt(data[i]);
				}
			outputStream.writeInt(te.xCoord);
			outputStream.writeInt(te.yCoord);
			outputStream.writeInt(te.zCoord);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			pipe.sendToPlayer(pack, ep);
		}
		else if (side == Side.CLIENT) {

		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int radius, List<Integer> data) {
		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		PacketTarget pt = new PacketTarget.RadiusTarget(te, radius);

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeInt(data.get(i));
				}
			outputStream.writeInt(te.xCoord);
			outputStream.writeInt(te.yCoord);
			outputStream.writeInt(te.zCoord);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pt.dispatch(pipe, pack);
		}
		else if (side == Side.CLIENT) {
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, int id, PacketTarget pt, List<Integer> data) {
		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeInt(data.get(i));
				}
			outputStream.writeInt(0);
			outputStream.writeInt(0);
			outputStream.writeInt(0);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pt.dispatch(pipe, pack);
		}
		else if (side == Side.CLIENT) {
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacketToEntireServer(String ch, int id, List<Integer> data) {

		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeInt(data.get(i));
				}
			outputStream.writeInt(0);
			outputStream.writeInt(0);
			outputStream.writeInt(0);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToAllOnServer(pack);
		}
		else if (side == Side.CLIENT) {
			//PacketDispatcher.sendPacketToServer(packet);
			//pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, int id, World world, int x, int y, int z, List<Integer> data) {

		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeInt(data.get(i));
				}
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		if (!world.isRemote) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (world.isRemote) {
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendLongDataPacket(String ch, int id, World world, int x, int y, int z, List<Long> data) {
		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(((npars-4)*8)+2*4); //4 bytes an int + 8 bytes a long
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeLong(data.get(i));
				}
			}
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendUUIDPacket(String ch, int id, World world, int x, int y, int z, UUID data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(2*8+3*4); //4 bytes an int + 8 bytes a long
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeLong(data.getMostSignificantBits());
			outputStream.writeLong(data.getLeastSignificantBits());
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.DATA, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacketToEntireServer(String ch, int id, int... data) {
		sendDataPacketToEntireServer(ch, id, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int data) {
		sendDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendDataPacketWithRadius(String ch, int id, TileEntity te, int radius, int... data) {
		sendDataPacket(ch, id, te, radius, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendDataPacket(String ch, int id, PacketTarget pt, int... data) {
		sendDataPacket(ch, id, pt, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendDataPacket(String ch, int id, World world, int x, int y, int z, int data) {
		sendDataPacket(ch, id, world, x, y, z, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendDataPacket(String ch, int id, World world, int x, int y, int z, int data1, int data2) {
		sendDataPacket(ch, id, world, x, y, z, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2}));
	}

	public static void sendDataPacket(String ch, int id, World world, int x, int y, int z, int... data) {
		sendDataPacket(ch, id, world, x, y, z, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendLongDataPacket(String ch, int id, TileEntity te, long data) {
		sendLongDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int data1, int data2) {
		sendDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int data1, int data2, int data3) {
		sendDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int data1, int data2, int data3, int data4) {
		sendDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3, data4}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, int... data) {
		sendDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeIntListFromArray(data));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, long data) {
		sendLongDataPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void writeDirectSound(String ch, int id, World world, double x, double y, double z, String name, float vol, float pitch, boolean scale) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);

			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);

			writeString(name, outputStream);

			outputStream.writeFloat(vol);
			outputStream.writeFloat(pitch);

			outputStream.writeBoolean(scale);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Sound Packet for sound '"+name+"' @ "+x+", "+y+", "+z+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.FULLSOUND, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//EntityPlayerMP player2 = (EntityPlayerMP) player;
			//PacketDispatcher.sendPacketToAllAround(x, y, z, 20, world.provider.dimensionId, packet);
			pipe.sendToAllAround(pack, world, x, y, z, 20);
		}
		else if (side == Side.CLIENT) {

		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendSoundPacket(String ch, SoundEnum s, World world, double x, double y, double z, float vol, float pitch, boolean atten) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(s.ordinal());
			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);

			outputStream.writeFloat(vol);
			outputStream.writeFloat(pitch);

			outputStream.writeBoolean(atten);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Sound Packet for "+s+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.SOUND, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//EntityPlayerMP player2 = (EntityPlayerMP) player;
			//PacketDispatcher.sendPacketToAllAround(x, y, z, 20, world.provider.dimensionId, packet);
			pipe.sendToAllAround(pack, world, x, y, z, 20);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//EntityClientPlayerMP player2 = (EntityClientPlayerMP) player;
			//PacketDispatcher.sendPacketToServer(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringPacket(String ch, int id, String sg, PacketTarget pt) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		pt.dispatch(pipe, pack);
	}

	public static void sendStringPacket(String ch, int id, String sg, TileEntity te) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			pipe.sendToDimension(pack, te.worldObj);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			pipe.sendToServer(pack);
			//pipe.sendToDimension(pack, te.worldObj); //SERVER ONLY
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringPacket(String ch, int id, String sg, World world, int x, int y, int z) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringIntPacket(String ch, int id, EntityPlayerMP ep, String sg, int... data) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToPlayer(pack, ep);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringPacket(String ch, int id, String sg) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
			outputStream.writeInt(0);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToAllOnServer(pack);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringPacketWithRadius(String ch, int id, TileEntity te, int radius, String sg) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			writeString(sg, outputStream);
			outputStream.writeInt(id);
			outputStream.writeInt(te.xCoord);
			outputStream.writeInt(te.yCoord);
			outputStream.writeInt(te.zCoord);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.STRING, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToAllAround(pack, te, radius);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllPlayers(packet);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendUpdatePacket(String ch, int id, TileEntity te) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		String name = te.getBlockType().getLocalizedName();

		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("TileEntity "+name+" threw an update packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.UPDATE, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			pipe.sendToDimension(pack, te.worldObj);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendUpdatePacket(String ch, int id, World world, int x, int y, int z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Coordinates "+x+", "+y+", "+z+" threw an update packet exception!");
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.UPDATE, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendFloatPacket(String ch, int id, TileEntity te, float data) {
		sendFloatPacket(ch, id, te.worldObj, te.xCoord, te.yCoord, te.zCoord, data);
	}

	public static void sendFloatPacket(String ch, int id, World world, int x, int y, int z, float data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeFloat(data);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.FLOAT, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToDimension(pack, world);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendPositionPacket(String ch, int id, Entity e, int data, PacketTarget pt) {
		sendPositionPacket(ch, id, e.worldObj, e.posX, e.posY, e.posZ, data, pt);
	}

	public static void sendPositionPacket(String ch, int id, World world, double x, double y, double z, int data, PacketTarget pt) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);
			outputStream.writeInt(data);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.POS, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pt.dispatch(pipe, pack);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, world.provider.dimensionId);
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendSyncPacket(String ch, TileEntity te, String field) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			Field f = ReikaReflectionHelper.getProtectedInheritedField(te, field);
			f.setAccessible(true);
			int data = f.getInt(te);
			writeString(field, outputStream);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
			outputStream.writeInt(data);
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.SYNC, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			new PacketTarget.RadiusTarget(te, 24).dispatch(pipe, pack);
		}
		else if (side == Side.CLIENT) {
			DragonAPICore.logError(te+" sent a sync packet from the client! This is not allowed!");
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendTankSyncPacket(String ch, TileEntity te, String tankField) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			Field f = ReikaReflectionHelper.getProtectedInheritedField(te, tankField);
			f.setAccessible(true);
			HybridTank tank = (HybridTank)f.get(te);
			writeString(tankField, outputStream);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
			outputStream.writeInt(tank.getLevel());
		}
		catch (ClassCastException ex) {
			//ex.printStackTrace();
			DragonAPICore.logError(te+" tried to sync its tank, but it is not a HybridTank instance!");
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}

		byte[] dat = bos.toByteArray();
		DataPacket pack = new DataPacket();
		pack.init(PacketTypes.TANK, pipe);
		pack.setData(dat);

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
			new PacketTarget.RadiusTarget(te, 24).dispatch(pipe, pack);
		}
		else if (side == Side.CLIENT) {
			DragonAPICore.logError(te+" sent a sync packet from the client! This is not allowed!");
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendNBTPacket(String ch, int id, NBTTagCompound nbt) {
		DataPacket pack = getNBTPacket(id, nbt);
		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		pack.init(PacketTypes.NBT, pipe);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			pipe.sendToAllOnServer(pack);
		}
		else if (side == Side.CLIENT) {
			pipe.sendToServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendNBTPacket(String ch, int id, EntityPlayerMP ep, NBTTagCompound nbt) {
		DataPacket pack = getNBTPacket(id, nbt);
		PacketPipeline pipe = pipelines.get(ch);
		if (pipe == null) {
			DragonAPICore.logError("Attempted to send a packet from an unbound channel!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		pack.init(PacketTypes.NBT, pipe);
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			pipe.sendToPlayer(pack, ep);
		}
		else if (side == Side.CLIENT) {
			pipe.sendToAllOnServer(pack);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	private static DataPacket getNBTPacket(int id, NBTTagCompound nbt) {
		DataPacket pack = new DataPacket();
		pack.setData(id, nbt);
		return pack;
	}

	public static void updateTileEntityData(World world, int x, int y, int z, String name, int data) {
		if (world.checkChunksExist(x, y, z, x, y, z)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te == null) {
				DragonAPICore.logError("Null TileEntity for syncing field "+name);
				return;
			}
			try {
				Field f = ReikaReflectionHelper.getProtectedInheritedField(te, name);
				if (f == null) {
					//DragonAPICore.log("Null field for syncing tank field "+name);
					return;
				}
				f.setAccessible(true);
				f.set(te, data);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateTileEntityTankData(World world, int x, int y, int z, String name, int level) {
		if (world.checkChunksExist(x, y, z, x, y, z)) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te == null) {
				DragonAPICore.logError("Null TileEntity for syncing tank field "+name);
				return;
			}
			try {
				Field f = ReikaReflectionHelper.getProtectedInheritedField(te, name);
				if (f == null) {
					//DragonAPICore.log("Null field for syncing tank field "+name);
					return;
				}
				f.setAccessible(true);
				HybridTank tank = (HybridTank)f.get(te);
				if (level <= 0) {
					tank.empty();
				}
				else if (level > tank.getCapacity())
					level = tank.getCapacity();

				if (tank.isEmpty()) {

				}
				else {
					Fluid fluid = tank.getActualFluid();
					tank.setContents(level, fluid);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void writeString(String par0Str, DataOutput par1DataOutput) throws IOException
	{
		if (par0Str.length() > 32767)
		{
			throw new IOException("String too big");
		}
		else
		{
			par1DataOutput.writeShort(par0Str.length());
			par1DataOutput.writeChars(par0Str);
		}
	}

	private static String readString(DataInput par0DataInput) throws IOException
	{
		short short1 = par0DataInput.readShort();

		if (short1 > Short.MAX_VALUE)
		{
			throw new IOException("Received string length longer than maximum allowed!");
		}
		else if (short1 < 0)
		{
			throw new IOException("Received string length is less than zero!");
		}
		else
		{
			StringBuilder stringbuilder = new StringBuilder();

			for (int j = 0; j < short1; ++j)
			{
				stringbuilder.append(par0DataInput.readChar());
			}

			return stringbuilder.toString();
		}
	}

	public static Packet getPacket(String channel, PacketObj p) {
		PacketPipeline pipe = pipelines.get(channel);
		return pipe != null ? pipe.getMinecraftPacket(p) : null;
	}

	public static class DataPacket extends PacketObj
	{
		protected byte[] bytes;
		private DataInputStream in;

		public DataPacket() {
			super();
		}

		private void setData(byte[] data) {
			bytes = new byte[data.length];
			System.arraycopy(data, 0, bytes, 0, bytes.length);
		}

		private void setData(int id, NBTTagCompound tag) {
			try {
				byte[] most = this.writeNBTTagCompoundToBytes(tag);
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeInt(id);
				out.write(most);
				bytes = out.toByteArray();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void readData(ByteBuf data) {
			super.readData(data);

			byte[] dat = data.array();
			bytes = new byte[dat.length-byteIndex-1];
			System.arraycopy(dat, byteIndex+1, bytes, 0, bytes.length);
			//DragonAPICore.log("received "+this);
		}

		@Override
		public void writeData(ByteBuf data) {
			super.writeData(data);
			data.writeBytes(bytes);
			//DragonAPICore.log("sent "+this);
		}

		public NBTTagCompound asNBT() {
			try {
				byte[] abyte = new byte[bytes.length-4]; //remove control int
				System.arraycopy(bytes, 4, abyte, 0, abyte.length);
				return this.readNBTTagCompoundFromBuffer(abyte);
			}
			catch (IOException e) {
				return null;
			}
		}

		public int getSize() {
			return bytes.length;
		}

		public boolean isEmpty() {
			return this.getSize() == 0;
		}

		@Override
		public DataInputStream getDataIn() {
			if (in == null)
				in = new DataInputStream(new ByteArrayInputStream(bytes));
			return in;
		}

		@Override
		protected String getDataAsString() {
			return Arrays.toString(bytes);
		}
	}

	public static abstract class PacketObj implements IMessage {

		protected PacketHandler handler;
		protected PacketTypes type;
		protected int byteIndex = 0;

		protected PacketObj() {

		}

		public final void fromBytes(ByteBuf buf) {
			this.readData(buf);
		}

		public final void toBytes(ByteBuf buf) {
			this.writeData(buf);
		}

		public void init(PacketTypes p, PacketPipeline l) {
			type = p;
			handler = l.getHandler();
		}

		public void readData(ByteBuf data) {
			short id = this.readShort(data);
			handler = getHandlerFromID(id);
			byte type = this.readByte(data);
			this.type = PacketTypes.getPacketType(type);
		}

		protected int readInt(ByteBuf data) {
			byteIndex += 4;
			return data.readInt();
		}

		protected short readShort(ByteBuf data) {
			byteIndex += 2;
			return data.readShort();
		}

		protected byte readByte(ByteBuf data) {
			byteIndex += 1;
			return data.readByte();
		}

		public void writeData(ByteBuf data) {
			data.writeShort(getHandlerID(handler));
			data.writeByte(type.ordinal());
		}

		@SideOnly(Side.CLIENT)
		public final void handleClient(NetHandlerPlayClient nh) {
			try {
				handler.handleData(this, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			this.close();
		}

		public final void handleServer(NetHandlerPlayServer nh) {
			try {
				handler.handleData(this, nh.playerEntity.worldObj, nh.playerEntity);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			this.close();
		}

		@Override
		public String toString() {
			String hd = handler.getClass().getCanonicalName()+" (ID "+this.handlerID()+")";
			return "type "+this.getType()+"; Data: "+this.getDataAsString()+" from "+hd;
		}

		protected abstract String getDataAsString();

		private void close() {
			try {
				this.getDataIn().close();
			}
			catch (IOException e) {
				DragonAPIInit.instance.getModLogger().logError("Error closing packet "+this+". Memory may leak.");
				e.printStackTrace();
			}
		}

		public abstract DataInputStream getDataIn();

		public final String readString() {
			try {
				return ReikaPacketHelper.readString(this.getDataIn());
			}
			catch (IOException e) {
				e.printStackTrace();
				return "ERROR";
			}
		}

		protected final byte[] writeNBTTagCompoundToBytes(NBTTagCompound tag) throws IOException
		{
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();
			if (tag == null)
				buf.writeShort(-1);
			else {
				byte[] abyte = CompressedStreamTools.compress(tag);
				buf.writeShort((short)abyte.length);
				buf.write(abyte);
			}
			return buf.toByteArray();
		}

		protected final NBTTagCompound readNBTTagCompoundFromBuffer(byte[] bytes) throws IOException
		{
			ByteArrayDataInput buf = ByteStreams.newDataInput(bytes);
			short short1 = buf.readShort();
			if (short1 < 0)
				return null;
			else {
				byte[] abyte = new byte[short1];
				buf.readFully(abyte);
				return CompressedStreamTools.func_152457_a(abyte, NBTSizeTracker.field_152451_a);
			}
		}

		public final PacketTypes getType() {
			return type;
		}

		protected final int handlerID() {
			return handlers.inverse().get(handler);
		}
	}

	public static void registerVanillaPacketType(DragonAPIMod mod, int id, Class<? extends Packet> c, Side s, EnumConnectionState state) {
		switch(s) {
			case CLIENT:
				if (state.func_150753_a().containsKey(id))
					throw new IDConflictException(mod, "Packet "+c+" ID "+id+" is already occupied by "+state.func_150753_a().get(id)+"!");
				state.func_150753_a().put(Integer.valueOf(id), c);
				break;
			case SERVER:
				if (state.func_150755_b().containsKey(id))
					throw new IDConflictException(mod, "Packet "+c+" ID "+id+" is already occupied by "+state.func_150755_b().get(id)+"!");
				state.func_150755_b().put(Integer.valueOf(id), c);
				break;
		}
		EnumConnectionState.field_150761_f.put(c, state);
		mod.getModLogger().log("Registering vanilla-type packet "+c+" with ID "+id+" on side "+s);
	}

	public static void syncTileEntity(TileEntity tile) {
		NBTTagCompound NBT = new NBTTagCompound();
		tile.writeToNBT(NBT);
		List<EntityPlayerMP> li = tile.worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, ReikaAABBHelper.getBlockAABB(tile.xCoord, tile.yCoord, tile.zCoord).expand(4, 4, 4));
		for (EntityPlayerMP ep : li)
			sendNBTPacket(DragonAPIInit.packetChannel, PacketIDs.VTILESYNC.ordinal(), ep, NBT);
	}

}
