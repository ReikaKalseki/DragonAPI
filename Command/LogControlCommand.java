/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public class LogControlCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "modlogger";
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
			else if ("source".equals(args[0].toLowerCase())) {
				ReikaJavaLibrary.toggleStackTrace();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All console prints now show a stack trace: "+ReikaJavaLibrary.dumpStack);
			}
			else if ("silence".equals(args[0].toLowerCase())) {
				ReikaJavaLibrary.toggleSilentMode();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All console prints disabled: "+ReikaJavaLibrary.silent);
			}
			else if ("default".equals(args[0].toLowerCase())) {
				ModLogger.setAllLoggingDefault();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging restored to config settings. Change affects "+n+" loggers.");
			}
			else
				ReikaChatHelper.sendChatToPlayer(ep, r+"You must specify \"enable\", \"disable\", \"source\", or \"default\".");
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