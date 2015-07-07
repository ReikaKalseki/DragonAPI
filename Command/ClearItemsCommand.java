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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;

public class ClearItemsCommand extends DragonCommandBase {

	private static long clearTime = -1;
	private static boolean clearAll = false;
	private static final Collection<Integer> clearIDs = new HashSet();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		clearIDs.clear();
		clearAll = false;
		if (args.length == 0 || (args.length == 1 && args[0].toLowerCase().equals("scan"))) {
			this.sendChatToSender(ics, EnumChatFormatting.RED.toString()+"You must specify at least one ID, or a '*'!");
			return;
		}
		boolean scan = false;
		if (args[0].toLowerCase().equals("scan")) {
			scan = true;
			String[] cp = new String[args.length-1];
			System.arraycopy(args, 1, cp, 0, cp.length);
			args = cp;
		}
		if (!scan)
			clearTime = System.currentTimeMillis();
		this.parseArgs(ics, args, scan);
	}

	private void parseArgs(ICommandSender ics, String[] args, boolean scan) {
		if (args[0].equals("*")) {
			if (scan) {
				this.scanItems(null);
			}
			else {
				clearAll = true;
			}
			this.sendChatToSender(ics, EnumChatFormatting.GREEN.toString()+"Cleared all dropped items.");
		}
		else {
			if (scan) {
				HashSet<KeyedItemStack> set = new HashSet();
				for (int i = 0; i < args.length; i++) {
					int id = Integer.parseInt(args[i]);
					Item item = Item.getItemById(id);
					KeyedItemStack ks = new KeyedItemStack(new ItemStack(item)).setIgnoreMetadata(true).setIgnoreNBT(true).setSized(false);
					set.add(ks);
				}
				this.scanItems(new ReikaEntityHelper.MultiItemSelector(set));
			}
			else {
				for (int i = 0; i < args.length; i++) {
					clearIDs.add(Integer.parseInt(args[i]));
				}
			}
			this.sendChatToSender(ics, EnumChatFormatting.GREEN.toString()+"Cleared all items with IDs '"+Arrays.toString(args)+"'.");
		}
	}


	private void scanItems(IEntitySelector sel) {
		World[] worlds = DimensionManager.getWorlds();
		for (World world : worlds) {
			ReikaEntityHelper.clearEntities(world, sel);
		}
	}

	@Override
	public String getCommandString() {
		return "clearitems";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	public static boolean clearItem(EntityItem ei) {
		return System.currentTimeMillis()-clearTime < 1000 && (clearAll || clearIDs.contains(Item.getIdFromItem(ei.getEntityItem().getItem())));
	}

}
