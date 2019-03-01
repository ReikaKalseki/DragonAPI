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

import net.minecraft.command.ICommandSender;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class TestControlCommand extends DragonCommandBase {

	private final String tag = "debugtest";

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		DragonAPICore.debugtest = !DragonAPICore.debugtest;
		ReikaChatHelper.sendChatToAllOnServer("Debug Test Mode: "+DragonAPICore.debugtest);
	}

	@Override
	public String getCommandString() {
		return tag;
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}
}
