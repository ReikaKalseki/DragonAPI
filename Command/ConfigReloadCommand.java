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
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;


public class ConfigReloadCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		ControlledConfig c = ControlledConfig.getForMod(args[0]);
		if (c == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"No such config for mod '"+args[0]+"'!");
		}
		else {
			c.reload();
			ModLogger.reloadLoggers();
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Config for '"+args[0]+"' reloaded. Note that some settings may require restart.");
		}
	}

	@Override
	public String getCommandString() {
		return "reloadconfig";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
