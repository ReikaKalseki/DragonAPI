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
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.ConfigReloadEvent;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Instantiable.IO.ModLogger;


public class ConfigReloadCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args[0].equals("all") || args[0].equals("*")) {
			ControlledConfig.reloadAll();
			MinecraftForge.EVENT_BUS.post(new ConfigReloadEvent());
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"All configs reloaded. Note that some settings may require restart.");
			return;
		}

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
