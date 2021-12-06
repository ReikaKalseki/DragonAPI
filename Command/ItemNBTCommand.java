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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class ItemNBTCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		ItemStack is = ep.getCurrentEquippedItem();
		if (is == null) {
			ReikaChatHelper.sendChatToPlayer(ep, "[No Item]");
			return;
		}
		ReikaChatHelper.sendChatToPlayer(ep, Item.itemRegistry.getNameForObject(is.getItem()));
		if (is.stackTagCompound == null) {
			ReikaChatHelper.sendChatToPlayer(ep, "{No Tag}");
			return;
		}
		ArrayList<String> li = ReikaNBTHelper.parseNBTAsLines(is.stackTagCompound);
		for (String s : li)
			ReikaChatHelper.sendChatToPlayer(ep, s);
	}

	@Override
	public String getCommandString() {
		return "itemnbt";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
