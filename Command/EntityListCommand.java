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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityListCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 1) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use /"+this.getCommandString()+" <side>.");
			return;
		}

		Side side = null;

		try {
			side = Side.valueOf(args[0].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			StringBuilder sb = new StringBuilder();
			sb.append(EnumChatFormatting.RED+"Invalid side. Use one of the following: ");
			for (int i = 0; i < Side.values().length; i++) {
				sb.append("'");
				sb.append(Side.values()[i].name().toLowerCase(Locale.ENGLISH));
				sb.append("'");
				if (i < Side.values().length-1)
					sb.append(", ");
			}
			sb.append(".");
			this.sendChatToSender(ics, sb.toString());
			return;
		}

		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		ReikaChatHelper.sendChatToPlayer(ep, "Found entities:");
		this.perform(side, ep);
	}

	@Override
	public String getCommandString() {
		return "entitylist";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void dumpClientside() {
		ArrayList<String> data = getData(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
		for (String s : data) {
			ReikaChatHelper.writeString(s);
		}
	}

	private void perform(Side side, EntityPlayerMP ep) {
		switch(side) {
			case CLIENT:
				this.sendPacket(ep);
				break;
			case SERVER:
				ArrayList<String> data = getData(ep, side);
				for (String s : data) {
					ReikaChatHelper.sendChatToPlayer(ep, s);
				}
				break;
		}
	}

	private void sendPacket(EntityPlayerMP ep) {
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.ENTITYDUMP.ordinal(), ep);
	}

	private static ArrayList<String> getData(EntityPlayer ep, Side side) {
		ArrayList<String> li = new ArrayList();
		String sd = ReikaStringParser.capFirstChar(side.name());
		for (Class c : ((Map<Class, String>)EntityList.classToStringMapping).keySet()) {
			String s = (String)EntityList.classToStringMapping.get(c);
			if (s == null)
				s = "[NO NAME]";
			else if (s.isEmpty())
				s = "[EMPTY STRING]";
			Integer id = (Integer)EntityList.stringToIDMapping.get(s);
			String sid = id != null ? String.valueOf(id) : "[NO ID]";
			String loc = ReikaEntityHelper.getEntityDisplayName(s);
			li.add(String.format("%s - '%s': Class = %s; ID = %s; Name = '%s'", sd, s, c.getName(), sid, loc));
		}
		return li;
	}

}
