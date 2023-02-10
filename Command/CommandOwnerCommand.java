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
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import Reika.DragonAPI.Libraries.IO.ReikaCommandHelper;


public class CommandOwnerCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length == 0) {
			Collection<ICommand> li = ReikaCommandHelper.getCommandList();
			for (ICommand c : li) {
				this.sendChatToSender(ics, "Command '"+c+"'");
			}
		}
		else if (args.length == 1) {
			List<String> li = MinecraftServer.getServer().getCommandManager().getPossibleCommands(ics, args[0]);
			for (String c : li) {
				this.sendChatToSender(ics, "Command '"+c+"'");
			}
		}
	}

	@Override
	public String getCommandString() {
		return "commandowners";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
