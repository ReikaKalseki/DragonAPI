/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * This code is provided under a modified visible-source license that is as follows:
 * 
 * Any and all users are permitted to use the source for educational purposes, or to create other mods that call
 * parts of this code and use DragonAPI as a dependency.
 * 
 * Unless given explicit written permission - electronic writing is acceptable - no user may redistribute this
 * source code nor any derivative works. These pre-approved works must prominently contain this copyright notice.
 * 
 * Additionally, no attempt may be made to achieve monetary gain from this code by anyone except the original author.
 * In the case of pre-approved derivative works, any monetary gains made will be shared between the original author
 * and the other developer(s), proportional to the ratio of derived to original code.
 * 
 * Finally, any and all displays, duplicates or derivatives of this code must be prominently marked as such, and must
 * contain attribution to the original author, including a link to the original source. Any attempts to claim credit
 * for this code will be treated as intentional theft.
 * 
 * Due to the Mojang and Minecraft Mod Terms of Service and Licensing Restrictions, compiled versions of this code
 * must be provided for free. However, with the exception of pre-approved derivative works, only the original author
 * may distribute compiled binary versions of this code.
 * 
 * Failure to comply with these restrictions is a violation of copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.DragonAPICore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public final class ReikaPacketHelper extends DragonAPICore {

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, List<Integer> data) {
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
        	//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(drops));
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

	public static void sendLongPacket(String ch, int id, TileEntity te, EntityPlayer player, List<Long> data) {
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
        	//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(drops));
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

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, int data) {
		sendPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendLongPacket(String ch, int id, TileEntity te, EntityPlayer player, long data) {
		sendLongPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2) {
		sendPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2}));
	}

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2, int data3) {
		sendPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3}));
	}

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, int data1, int data2, int data3, int data4) {
		sendPacket(ch, id, te, player, ReikaJavaLibrary.makeListFromArray(new Object[]{data1, data2, data3, data4}));
	}

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player, long data) {
		sendLongPacket(ch, id, te, player, ReikaJavaLibrary.makeListFrom(data));
	}

	public static void sendPacket(String ch, int id, TileEntity te, EntityPlayer player) {
		sendPacket(ch, id, te, player, null);
	}

}
