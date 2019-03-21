package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;

import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;


public class ClearBreakersCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		ProgressiveRecursiveBreaker.instance.clearBreakers();
	}

	@Override
	public String getCommandString() {
		return "clearbreakers";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
