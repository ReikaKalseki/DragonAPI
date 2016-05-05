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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class ItemNBTCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		ItemStack is = ep.getCurrentEquippedItem();
		String s = is != null ? is.stackTagCompound != null ? is.stackTagCompound.toString() : "{No Tag}" : "[No Item]";
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
