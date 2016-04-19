package Reika.DragonAPI.Command;

import java.util.Collection;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;


public class CommandOwnerCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length == 0) {
			Collection<ICommand> li = MinecraftServer.getServer().getCommandManager().getCommands().values();
			for (ICommand c : li) {
				this.printCommand(ics, c);
			}
		}
		else if (args.length == 1) {
			List<ICommand> li = MinecraftServer.getServer().getCommandManager().getPossibleCommands(ics, args[0]);
			for (ICommand c : li) {
				this.printCommand(ics, c);
			}
		}
	}

	private void printCommand(ICommandSender ics, ICommand c) {
		this.sendChatToSender(ics, "Command '"+c.getCommandName()+"': "+c);
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
