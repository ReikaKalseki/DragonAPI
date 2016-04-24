package Reika.DragonAPI.Libraries.IO;

import java.util.Collection;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;


public class ReikaCommandHelper {

	public static ICommand getCommandByName(String n) {
		for (ICommand c : getCommandList()) {
			String n2 = c.getCommandName();
			if (n2.equalsIgnoreCase(n))
				return c;
		}
		return null;
	}

	public static Collection<ICommand> getCommandList() {
		return MinecraftServer.getServer().getCommandManager().getCommands().values();
	}

}
