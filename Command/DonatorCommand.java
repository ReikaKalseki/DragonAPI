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
import net.minecraft.entity.player.EntityPlayerMP;
import Reika.DragonAPI.Auxiliary.Trackers.DonatorController;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

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
	protected boolean isAdminOnly() {
		return false;
	}

}
