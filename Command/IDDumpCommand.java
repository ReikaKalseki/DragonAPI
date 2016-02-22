/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Extras.IDHelper;
import Reika.DragonAPI.Extras.IDType;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IDDumpCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use /"+this.getCommandString()+" <idtype> <side>.");
			return;
		}

		IDType type = null;

		try {
			type = IDType.valueOf(args[0].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			StringBuilder sb = new StringBuilder();
			sb.append(EnumChatFormatting.RED+"Invalid id type. Use one of the following: ");
			for (int i = 0; i < IDType.list.length; i++) {
				sb.append(EnumChatFormatting.RED.toString());
				sb.append("'");
				sb.append(IDType.list[i].name().toLowerCase());
				sb.append("'");
				if (i < IDType.list.length-1)
					sb.append(", ");
			}
			sb.append(".");
			this.sendChatToSender(ics, sb.toString());
			return;
		}

		Side side = null;

		try {
			side = Side.valueOf(args[1].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			StringBuilder sb = new StringBuilder();
			sb.append(EnumChatFormatting.RED+"Invalid side. Use one of the following: ");
			for (int i = 0; i < Side.values().length; i++) {
				sb.append("'");
				sb.append(Side.values()[i].name().toLowerCase());
				sb.append("'");
				if (i < Side.values().length-1)
					sb.append(", ");
			}
			sb.append(".");
			this.sendChatToSender(ics, sb.toString());
			return;
		}

		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		ReikaChatHelper.sendChatToPlayer(ep, "Found IDs:");
		this.perform(side, ep, type);
	}

	@Override
	public String getCommandString() {
		return "dumpids";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void dumpClientside(int type) {
		IDType id = IDType.list[type];
		Map<String, Integer> data = getData(id);
		for (String s : data.keySet()) {
			String sg = String.format("Client %s ID %d = %s", ReikaStringParser.capFirstChar(id.name()), data.get(s), s);
			ReikaChatHelper.writeString(sg);
		}
	}

	private void perform(Side side, EntityPlayerMP ep, IDType type) {
		switch(side) {
			case CLIENT:
				this.sendPacket(ep, type);
				break;
			case SERVER:
				Map<String, Integer> data = getData(type);
				for (String s : data.keySet()) {
					String sg = String.format("%s %s ID %d = %s", ReikaStringParser.capFirstChar(side.name()), type.getName(), data.get(s), s);
					ReikaChatHelper.sendChatToPlayer(ep, sg);
				}
				break;
		}
	}

	private void sendPacket(EntityPlayerMP ep, IDType type) {
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.IDDUMP.ordinal(), ep, type.ordinal());
	}

	private static Map<String, Integer> getData(IDType type) {
		switch(type) {
			case BIOME:
				return IDHelper.getBiomeIDs();
			case BLOCK:
				return IDHelper.getBlockIDs();
			case ENTITY:
				return IDHelper.getEntityIDs();
			case FLUID:
				return IDHelper.getFluidIDs();
			case ITEM:
				return IDHelper.getItemIDs();
			case POTION:
				return IDHelper.getPotionIDs();
			case ENCHANTMENT:
				return IDHelper.getEnchantmentIDs();
			case FLUIDCONTAINER:
				return IDHelper.getFluidContainers();
			default:
				return null;
		}
	}

}
