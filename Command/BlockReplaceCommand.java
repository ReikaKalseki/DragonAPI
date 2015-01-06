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

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BlockReplaceCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "blockreplace";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

		if (args.length == 3) {
			String from = args[0];
			String to = args[1];
			int id1 = -1;
			int id2 = -1;
			try {
				id1 = Integer.parseInt(from);
				id2 = Integer.parseInt(to);
			}
			catch (NumberFormatException e) {

			}
			boolean number = id1 != -1 && id2 != -1;
			int range = ReikaJavaLibrary.safeIntParse(args[2]);
			if (range <= 0) {
				String sg = EnumChatFormatting.RED+"Invalid range '"+range+"'.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
				return;
			}
			Block bfrom = number ? Block.getBlockById(id1) : Block.getBlockFromName(from);
			Block bto = number ? Block.getBlockById(id2) : Block.getBlockFromName(to);
			if (bfrom == null) {
				String s = number ? "ID "+from : from;
				String sg = EnumChatFormatting.RED+"No such block '"+s+"'.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
			}
			else if (bto == null) {
				String s = number ? "ID "+to : to;
				String sg = EnumChatFormatting.RED+"No such block '"+s+"'.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
			}
			else {
				int count = 0;
				for (int i = -range; i <= range; i++) {
					for (int j = -range; j <= range; j++) {
						for (int k = -range; k <= range; k++) {
							int x = MathHelper.floor_double(ep.posX)+i;
							int y = MathHelper.floor_double(ep.posY)+j;
							int z = MathHelper.floor_double(ep.posZ)+k;
							if (ep.worldObj.getBlock(x, y, z) == bfrom) {
								int meta = ep.worldObj.getBlockMetadata(x, y, z);
								ep.worldObj.setBlock(x, y, z, bto, meta, 3);
								count++;
							}
						}
					}
				}
				String s = number ? "ID "+from : from;
				String s2 = number ? "ID "+to : to;
				String sg = EnumChatFormatting.GREEN+"Replaced "+count+" of "+s+" with "+s2+".";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
			}
		}
		else {
			String sg = EnumChatFormatting.RED+"You must specify a source block, a target block, and a range!";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
		}
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
