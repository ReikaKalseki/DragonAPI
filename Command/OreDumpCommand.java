/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreDumpCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Use /"+this.getCommandString()+" <tag> <side>.");
			return;
		}

		String type = args[0];
		Side side = null;
		try {
			side = Side.valueOf(args[1].toUpperCase());
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

		sendChatToSender(ics, "Found Items for tag '"+type+"':");
		this.perform(side, ics, type);
	}

	@Override
	public String getCommandString() {
		return "dumpore";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void dumpClientside(String id) {
		ReikaChatHelper.writeString("[CLIENT]");
		for (ItemStack is : getData(id)) {
			ReikaChatHelper.writeString(fullID(is));
		}
	}

	private void perform(Side side, ICommandSender ics, String type) {
		switch(side) {
			case CLIENT:
				this.sendPacket(this.getCommandSenderAsPlayer(ics), type);
				break;
			case SERVER:
				for (ItemStack is : getData(type)) {
					sendChatToSender(ics, fullID(is));
				}
				break;
		}
	}

	private void sendPacket(EntityPlayerMP ep, String tag) {
		ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.OREDUMP.ordinal(), tag, new PacketTarget.PlayerTarget(ep));
	}

	private static List<ItemStack> getData(String id) {
		ArrayList<ItemStack> li = new ArrayList(OreDictionary.getOres(id));
		ReikaItemHelper.sortItems(li);
		return li;
	}

	private static final String fullID(ItemStack is) {
		if (is == null)
			return "[null]";
		else if (is.getItem() == null)
			return "[null-item stack]";
		String n = Item.itemRegistry.getNameForObject(is.getItem());
		n = n.substring(n.indexOf(':')+1);
		String p1 = n+"@"+is.getItemDamage();
		String p2 = "nbt={"+is.stackTagCompound+"};";
		String p3 = "mod=["+ReikaItemHelper.getRegistrantMod(is)+"]";
		//p1 = ReikaStringParser.padToLength(p1, 48, " ");
		//p2 = ReikaStringParser.padToLength(p2, 40, " ");
		return p1+"; "+p2+" "+p3;
	}
}
