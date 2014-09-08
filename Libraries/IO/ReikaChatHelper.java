/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaChatHelper extends DragonAPICore {

	@SideOnly(Side.CLIENT)
	public static void clearChat() {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
	}

	/** Writes an itemstack to the chat.
	 * Args: World, itemstack */
	@SideOnly(Side.CLIENT)
	public static void writeItemStack(World world, ItemStack is) {
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String msg;
		if (is == null)
			msg = "Null Stack!";
		else
			msg = String.format("%d, %d, %d", is.getItem(), is.stackSize, is.getItemDamage());
		sendChatToPlayer(Minecraft.getMinecraft().thePlayer, msg);
	}

	/** Writes coordinates to the chat.
	 * Args: World, x, y, z */
	@SideOnly(Side.CLIENT)
	public static void writeCoords(World world, double x, double y, double z) {
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String msg;
		msg = String.format("%.2f, %.2f, %.2f", x, y, z);
		sendChatToPlayer(Minecraft.getMinecraft().thePlayer, msg);
	}

	/** Writes a block ID:metadata and coordinates to the chat.
	 * Args: World, x, y, z */
	@SideOnly(Side.CLIENT)
	public static void writeBlockAtCoords(World world, int x, int y, int z) {
		StringBuilder sb = new StringBuilder();
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String name;
		Block id = world.getBlock(x, y, z);
		if (id != Blocks.air)
			name = id.getLocalizedName();
		else
			name = "Air";
		int meta = world.getBlockMetadata(x, y, z);
		sb.append(String.format("Block %s (ID %d Metadata %d) @ x=%d, y=%d, z=%d", name, Block.getIdFromBlock(id), meta, x, y, z)+"\n");
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			sb.append("No Tile Entity at this location.");
		}
		else {
			sb.append("Tile Entity at this location:\n");
			sb.append(te.toString());
		}
		sendChatToPlayer(Minecraft.getMinecraft().thePlayer, sb.toString());
	}

	/** Writes an integer to the chat. Args: Integer */
	@SideOnly(Side.CLIENT)
	public static void writeInt(int num) {
		writeString(String.format("%d", num));
	}

	/** Writes any general-purpose string to the chat. Args: String */
	@SideOnly(Side.CLIENT)
	public static void writeString(String sg) {
		if (Minecraft.getMinecraft().thePlayer != null)
			sendChatToPlayer(Minecraft.getMinecraft().thePlayer, sg);
	}

	/** Automatically translates if possible. */
	@SideOnly(Side.CLIENT)
	public static void writeLocalString(String tag) {
		writeString(StatCollector.translateToLocal(tag));
	}

	/** A general object-to-chat function. Autoclips doubles to 3 decimals. Args: Object */
	@SideOnly(Side.CLIENT)
	public static void write(Object obj) {
		if (obj == null) {
			writeString("null");
			return;
		}
		String str;
		if (obj.getClass() == Double.class)
			str = String.format("%.3f", obj);
		else
			str = String.valueOf(obj);
		writeString(str);
	}

	@SideOnly(Side.CLIENT)
	public static void writeFormattedString(String str, EnumChatFormatting... fm) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fm.length; i++)
			sb.append(fm[i].toString());
		writeString(sb.toString()+str);
	}

	@SideOnly(Side.CLIENT)
	public static void writeEntity(World world, Entity ent) {
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (ent == null)
			writeString("null");
		else
			writeString(ent.getCommandSenderName()+" @ "+String.format("%.2f, %.2f, %.2f", ent.posX, ent.posY, ent.posZ));
	}

	@SideOnly(Side.CLIENT)
	public static void writeItem(World world, Item id, int dmg) {
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (id == null)
			writeString("Null Item");
		//else if (id < 256)
		//	writeBlock(world, id, dmg);
		else
			writeString(id+":"+dmg+" is "+id.getItemStackDisplayName(new ItemStack(id, 1, dmg)));
	}

	@SideOnly(Side.CLIENT)
	public static void writeBlock(World world, Block id, int meta) {
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (id == Blocks.air)
			writeString("Null Item");
		//else if (id > 4096)
		//	writeItem(world, id, meta);
		else
			writeString(id+":"+meta+" is "+id.getLocalizedName());
	}

	@SideOnly(Side.CLIENT)
	public static void writeSide() {
		if (Minecraft.getMinecraft().thePlayer != null)
			sendChatToPlayer(Minecraft.getMinecraft().thePlayer, String.valueOf(FMLCommonHandler.instance().getEffectiveSide()));
	}

	public static void sendChatToPlayer(EntityPlayer ep, String sg) {
		String[] parts = sg.split("\\n");
		for (int i = 0; i < parts.length; i++) {
			ChatComponentTranslation chat = new ChatComponentTranslation(parts[i]);
			ep.addChatMessage(chat);
		}
	}

	public static void sendChatToAllOnServer(String sg) {
		String[] parts = sg.split("\\n"); // \n no longer works in chat as of 1.7
		MinecraftServer srv = MinecraftServer.getServer();
		if (srv != null) {
			ServerConfigurationManager cfg = srv.getConfigurationManager();
			if (cfg != null) {
				for (int i = 0; i < parts.length; i++) {
					ChatComponentTranslation chat = new ChatComponentTranslation(parts[i]);
					cfg.sendChatMsg(chat);
				}
			}
			else {
				ReikaJavaLibrary.pConsole("Something tried to send chat to a server with null configurations!");
				ReikaJavaLibrary.dumpStack();
			}
		}
		else {
			ReikaJavaLibrary.pConsole("Something tried to send chat to a null server!");
			ReikaJavaLibrary.dumpStack();
		}
	}

}
