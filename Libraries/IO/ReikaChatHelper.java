/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
public final class ReikaChatHelper extends DragonAPICore {

	public static void clearChat() {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
	}

	/** Writes an itemstack to the chat.
	 * Args: World, itemstack */
	public static void writeItemStack(World world, ItemStack is) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String msg;
		if (is == null)
			msg = "Null Stack!";
		else
			msg = String.format("%d, %d, %d", is.itemID, is.stackSize, is.getItemDamage());
		Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
	}

	/** Writes coordinates to the chat.
	 * Args: World, x, y, z */
	public static void writeCoords(World world, double x, double y, double z) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String msg;
		msg = String.format("%.2f, %.2f, %.2f", x, y, z);
		Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
	}

	/** Writes a block ID:metadata and coordinates to the chat.
	 * Args: World, x, y, z */
	public static void writeBlockAtCoords(World world, int x, int y, int z) {
		StringBuilder sb = new StringBuilder();
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		String name;
		int id = world.getBlockId(x, y, z);
		if (id != 0)
			name = Block.blocksList[id].getLocalizedName();
		else
			name = "Air";
		int meta = world.getBlockMetadata(x, y, z);
		sb.append(String.format("Block "+name+" (ID %d Metadata %d) @ x=%d, y=%d, z=%d", id, meta, x, y, z)+"\n");
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te == null) {
			sb.append("No Tile Entity at this location.");
		}
		else {
			sb.append("Tile Entity at this location:\n");
			sb.append(te.toString());
		}
		Minecraft.getMinecraft().thePlayer.addChatMessage(sb.toString());
	}

	/** Writes an integer to the chat. Args: Integer */
	public static void writeInt(int num) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		writeString(String.format("%d", num));
	}

	/** Writes any general-purpose string to the chat. Args: String */
	public static void writeString(String sg) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer != null)
			Minecraft.getMinecraft().thePlayer.addChatMessage(sg);
	}

	/** Automatically translates if possible. */
	public static void writeLocalString(String tag) {
		writeString(StatCollector.translateToLocal(tag));
	}

	/** A general object-to-chat function. Autoclips doubles to 2 decimals. Args: Object */
	public static void write(Object obj) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (obj == null) {
			writeString("null");
			return;
		}
		String str;
		if (obj.getClass() == Double.class)
			str = String.format("%.2f", obj);
		else
			str = String.valueOf(obj);
		writeString(str);
	}

	public static void writeFormattedString(String str, EnumChatFormatting... fm) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fm.length; i++)
			sb.append(fm[i].toString());
		writeString(sb.toString()+str);
	}

	public static void writeEntity(World world, Entity ent) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (ent == null)
			writeString("null");
		else
			writeString(ent.getEntityName()+" @ "+String.format("%.2f, %.2f, %.2f", ent.posX, ent.posY, ent.posZ));
	}

	public static void writeItem(World world, int id, int dmg) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (id == 0)
			writeString("Null Item");
		else if (id < 256)
			writeBlock(world, id, dmg);
		else
			writeString(id+":"+dmg+" is "+Item.itemsList[id].getItemDisplayName(new ItemStack(id, 1, dmg)));
	}

	public static void writeBlock(World world, int id, int meta) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer == null || world == null)
			return;
		if (id == 0)
			writeString("Null Item");
		else if (id > 4096)
			writeItem(world, id, meta);
		else
			writeString(id+":"+meta+" is "+Block.blocksList[id].getLocalizedName());
	}

	public static void writeSide() {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		if (Minecraft.getMinecraft().thePlayer != null)
			Minecraft.getMinecraft().thePlayer.addChatMessage(String.valueOf(FMLCommonHandler.instance().getEffectiveSide()));
	}

	public static void sendChatToPlayer(EntityPlayer ep, String sg) {
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.addText(sg);
		ep.sendChatToPlayer(chat);
	}

	public static void sendChatToAllOnServer(String sg) {
		ChatMessageComponent chat = new ChatMessageComponent();
		chat.addText(sg);
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(chat);
	}

}
