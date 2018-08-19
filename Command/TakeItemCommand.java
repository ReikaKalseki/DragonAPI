/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;


public class TakeItemCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep1 = this.getCommandSenderAsPlayer(ics);
		EntityPlayer ep2 = ep1.worldObj.getPlayerEntityByName(args[0]);
		if (ep2 == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"No such player.");
			return;
		}
		ItemStack is = ep1.getCurrentEquippedItem();
		if (is == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Not holding an item.");
			return;
		}
		is = is.copy();
		int slot = ReikaInventoryHelper.locateInInventory(is, ep2.inventory, false);
		while (slot >= 0) {
			ep2.inventory.setInventorySlotContents(slot, null);
			slot = ReikaInventoryHelper.locateInInventory(is, ep2.inventory, false);
		}
	}

	@Override
	public String getCommandString() {
		return "takeitem";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
