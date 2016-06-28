/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.io;

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
