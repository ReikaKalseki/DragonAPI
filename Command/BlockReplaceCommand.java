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

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

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
			HashSet<Integer> id1 = new HashSet();
			int id2 = -1;
			try {
				if (from.equals("all_ores")) {
					for (ReikaOreHelper ore : ReikaOreHelper.oreList) {
						for (ItemStack is : ore.getAllOreBlocks()) {
							id1.add(Block.getIdFromBlock(Block.getBlockFromItem(is.getItem())));
						}
					}
					for (ModOreList ore : ModOreList.oreList) {
						for (ItemStack is : ore.getAllOreBlocks()) {
							id1.add(Block.getIdFromBlock(Block.getBlockFromItem(is.getItem())));
						}
					}
				}
				else if (from.startsWith("class_")) {
					String cl = from.substring(6).toLowerCase();
					for (String n : ((Collection<String>)Block.blockRegistry.getKeys())) {
						Block b = Block.getBlockFromName(n);
						if (b.getClass().getSimpleName().toLowerCase().equals(cl)) {
							id1.add(Block.getIdFromBlock(b));
						}
					}
				}
				else {
					String[] parts = from.split(",");
					for (int i = 0; i < parts.length; i++) {
						id1.add(Integer.parseInt(parts[i]));
					}
				}
				id2 = Integer.parseInt(to);
			}
			catch (NumberFormatException e) {

			}
			boolean number = !id1.isEmpty() && id2 != -1;
			int range = ReikaJavaLibrary.safeIntParse(args[2]);
			if (range <= 0) {
				String sg = EnumChatFormatting.RED+"Invalid range '"+range+"'.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
				return;
			}
			Block bto = number ? Block.getBlockById(id2) : Block.getBlockFromName(to);
			for (int nid1 : id1) {
				Block bfrom = number ? Block.getBlockById(nid1) : Block.getBlockFromName(from);
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
					if (count > 0) {
						String s = number ? "ID "+nid1 : from;
						String s2 = number ? "ID "+to : to;
						String sg = EnumChatFormatting.GREEN+"Replaced "+count+" of "+s+" with "+s2+".";
						ReikaChatHelper.sendChatToPlayer(ep, sg);
					}
				}
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
