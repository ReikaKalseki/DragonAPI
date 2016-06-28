/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import reika.dragonapi.auxiliary.trackers.DonatorController;
import reika.dragonapi.libraries.io.ReikaChatHelper;

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
