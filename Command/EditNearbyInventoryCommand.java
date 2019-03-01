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

import java.util.Collection;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class EditNearbyInventoryCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		if (args.length != 5) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"You must specify an action, a range, and an ItemStack.");
			return;
		}
		ItemStack is = this.parseItem(args[2], args[3], args[4]);
		if (is == null) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No such item '"+args[2]+"'.");
			return;
		}
		Mode mode = Mode.get(args[0]);
		if (mode == null) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No such action '"+args[0]+"'.");
			return;
		}
		int r = ReikaJavaLibrary.safeIntParse(args[1]);
		if (r <= 0) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"Invalid range '"+args[1]+"'.");
			return;
		}
		int x = MathHelper.floor_double(ep.posX);
		int y = MathHelper.floor_double(ep.posY);
		int z = MathHelper.floor_double(ep.posZ);
		int num = mode.perform(ep.worldObj, x, y, z, is, r);
		if (num < 0) {
			ReikaChatHelper.sendChatToPlayer(ep, EnumChatFormatting.RED+"No inventories within range.");
			return;
		}
		String base = "%sSuccessfully %s inventories for %s within%s%s%d blocks of %d, %d, %d. %s%s";
		String act = mode == Mode.COUNT ? num+" items present." : num+"/"+is.stackSize+" changes made.";
		String g = EnumChatFormatting.GREEN.toString();
		String s = String.format(base, g, args[0], is.toString(), "\n", g, r, x, y, z, EnumChatFormatting.GOLD.toString(), act);
		ReikaChatHelper.sendChatToPlayer(ep, s);
	}

	private ItemStack parseItem(String item, String damage, String size) {
		int meta = Integer.parseInt(damage);
		int num = Integer.parseInt(size);
		int id = -1;
		try {
			id = Integer.parseInt(item);
		}
		catch (Exception e) {

		}

		Item i = id > 0 ? Item.getItemById(id) : (Item)Item.itemRegistry.getObject(item);
		return i != null ? new ItemStack(i, num, meta) : null;
	}

	@Override
	public String getCommandString() {
		return "nearinv";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	private static enum Mode {
		COUNT(),
		ADD(),
		REMOVE();

		private static Mode get(String mode) {
			for (int i = 0; i < values().length; i++) {
				Mode m = values()[i];
				if (m.name().toLowerCase(Locale.ENGLISH).equals(mode.toLowerCase(Locale.ENGLISH)))
					return m;
			}
			return null;
		}

		public int perform(World world, int x, int y, int z, ItemStack is, int r) {
			Collection<IInventory> c = ReikaWorldHelper.getAllInventories(world, x, y, z, r);
			if (c.isEmpty())
				return -1;
			int count = 0;
			for (IInventory ii : c) {
				if (this == COUNT || this == REMOVE) {
					int s = ii.getSizeInventory();
					for (int i = 0; i < s; i++) {
						ItemStack in = ii.getStackInSlot(i);
						if (ReikaItemHelper.matchStacks(is, in)) {
							if (this == REMOVE) {
								int num = Math.min(is.stackSize-count, in.stackSize);
								count += num;
								in.stackSize -= num;
								if (in.stackSize <= 0)
									ii.setInventorySlotContents(i, null);
								if (count >= is.stackSize)
									return count;
							}
							else {
								count += in.stackSize;
							}
						}
					}
				}
				if (this == ADD) {
					ItemStack is2 = ReikaItemHelper.getSizedItemStack(is, is.stackSize-count);
					count += is2.stackSize-ReikaInventoryHelper.addToInventoryWithLeftover(is2, ii, false);
					//ReikaJavaLibrary.pConsole(is+":"+count);
					if (count >= is.stackSize)
						return count;
				}
			}
			return count;
		}
	}

}
