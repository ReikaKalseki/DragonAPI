/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public final class ReikaPacketHelper extends DragonAPICore {

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, List<Integer> data) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		String name = te.getBlockType().getLocalizedName();

		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(npars*4); //4 bytes an int
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketTypes.DATA.ordinal());
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
			throw new RuntimeException("TileEntity "+name+" threw a packet exception! Null data: "+(data == null)+"; Npars: "+npars);
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ch;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			EntityPlayerMP player2 = (EntityPlayerMP) player;
			PacketDispatcher.sendPacketToServer(packet);
			PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			EntityClientPlayerMP player2 = (EntityClientPlayerMP) player;
			PacketDispatcher.sendPacketToServer(packet);
			//PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendLongDataPacket(String ch, int id, TileEntity te, EntityPlayer player, List<Long> data) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		String name = te.getBlockType().getLocalizedName();

		int npars;
		if (data == null)
			npars = 4;
		else
			npars = data.size()+4;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(((npars-4)*8)+2*4); //4 bytes an int + 8 bytes a long
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketTypes.DATA.ordinal());
			outputStream.writeInt(id);
			if (data != null)
				for (int i = 0; i < data.size(); i++) {
					outputStream.writeLong(data.get(i));
				}
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("TileEntity "+name+" threw a long packet exception! Null data: "+(data == null)+"; Npars: "+npars);
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ch;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			EntityPlayerMP player2 = (EntityPlayerMP) player;
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			EntityClientPlayerMP player2 = (EntityClientPlayerMP) player;
			PacketDispatcher.sendPacketToServer(packet);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, int data) {
		sendDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendLongDataPacket(String ch, int id, TileEntity te, EntityPlayer player, long data) {
		sendLongDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2) {
		sendDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2, int data3) {
		sendDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2, int data3, int data4) {
		sendDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3, data4}));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te, EntityPlayer player, long data) {
		sendLongDataPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendDataPacket(String ch, int id, TileEntity te) {
		sendDataPacket(ch, id, te, null, null);
	}

	public static void sendSoundPacket(String ch, String path, double x, double y, double z, float vol, float pitch) {
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketTypes.SOUND.ordinal());
			Packet.writeString(path, outputStream);
			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);

			outputStream.writeFloat(vol);
			outputStream.writeFloat(pitch);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Sound Packet for "+path+" threw a packet exception!");
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ch;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			//EntityPlayerMP player2 = (EntityPlayerMP) player;
			PacketDispatcher.sendPacketToAllPlayers(packet);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			//EntityClientPlayerMP player2 = (EntityClientPlayerMP) player;
			//PacketDispatcher.sendPacketToServer(packet);
		}
		else {
			// We are on the Bukkit server.
		}
	}

	public static void sendStringPacket(String ch, int id, String sg, TileEntity te) {
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		int length = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(PacketTypes.STRING.ordinal());
			outputStream.writeInt(id);
			Packet.writeString(sg, outputStream);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("String Packet for "+sg+" threw a packet exception!");
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ch;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			PacketDispatcher.sendPacketToServer(packet);
			PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			PacketDispatcher.sendPacketToServer(packet);
			PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
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
			outputStream.writeInt(PacketTypes.UPDATE.ordinal());
			outputStream.writeInt(id);
			outputStream.writeInt(x);
			outputStream.writeInt(y);
			outputStream.writeInt(z);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("TileEntity "+name+" threw an update packet exception!");
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ch;
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			// We are on the server side.
			PacketDispatcher.sendPacketToServer(packet);
			PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
		}
		else if (side == Side.CLIENT) {
			// We are on the client side.
			PacketDispatcher.sendPacketToServer(packet);
			PacketDispatcher.sendPacketToAllInDimension(packet, te.worldObj.provider.dimensionId);
		}
		else {
			// We are on the Bukkit server.
		}
	}

}
