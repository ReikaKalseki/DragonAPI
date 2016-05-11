/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class TestControlCommand extends CommandBase {

	private final String tag = "debugtest";

	@Override
	public String getCommandName() {
		return tag;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/"+tag;
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		DragonAPICore.debugtest = !DragonAPICore.debugtest;
		ReikaChatHelper.sendChatToAllOnServer("Debug Test Mode: "+DragonAPICore.debugtest);
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
}
