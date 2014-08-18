/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import Reika.DragonAPI.Auxiliary.DonatorController;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class DonatorCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "dragondonators";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		ReikaChatHelper.sendChatToPlayer(ep, DonatorController.instance.getDisplayList());
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}



}
