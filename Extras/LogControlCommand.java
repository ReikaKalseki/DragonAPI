/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class LogControlCommand extends CommandBase {

	private final String tag = "modlogger";

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
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		int n = ModLogger.getActiveLoggers();
		EnumChatFormatting g = EnumChatFormatting.GREEN;
		EnumChatFormatting r = EnumChatFormatting.RED;
		if (args == null || args.length != 1)
			ReikaChatHelper.sendChatToPlayer(ep, r+"You must specify \"enable\", \"disable\", or \"default\".");
		else {
			if ("enable".equals(args[0].toLowerCase())) {
				ModLogger.setAllLoggingTrue();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging enabled. Change affects "+n+" loggers.");
			}
			else if ("disable".equals(args[0].toLowerCase())) {
				ModLogger.setAllLoggingFalse();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging disabled. Change affects "+n+" loggers.");
			}
			else if ("default".equals(args[0].toLowerCase())) {
				ModLogger.setAllLoggingDefault();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging restored to config settings. Change affects "+n+" loggers.");
			}
			else
				ReikaChatHelper.sendChatToPlayer(ep, r+"You must specify \"enable\", \"disable\", or \"default\".");
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return DragonAPICore.isSinglePlayer() ? 0 : 4;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return DragonAPICore.isSinglePlayer() ? true : super.canCommandSenderUseCommand(sender);
	}

}
