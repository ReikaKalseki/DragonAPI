/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.command;

import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.instantiable.io.ModLogger;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;

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
			if ("enable".equals(args[0].toLowerCase(Locale.ENGLISH))) {
				ModLogger.setAllLoggingTrue();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging enabled. Change affects "+n+" loggers.");
			}
			else if ("disable".equals(args[0].toLowerCase(Locale.ENGLISH))) {
				ModLogger.setAllLoggingFalse();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging disabled. Change affects "+n+" loggers.");
			}
			else if ("source".equals(args[0].toLowerCase(Locale.ENGLISH))) {
				ReikaJavaLibrary.toggleStackTrace();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All console prints now show a stack trace: "+ReikaJavaLibrary.dumpStack);
			}
			else if ("silence".equals(args[0].toLowerCase(Locale.ENGLISH))) {
				ReikaJavaLibrary.toggleSilentMode();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All console prints disabled: "+ReikaJavaLibrary.silent);
			}
			else if ("default".equals(args[0].toLowerCase(Locale.ENGLISH))) {
				ModLogger.setAllLoggingDefault();
				ReikaChatHelper.sendChatToPlayer(ep, g+"All logging restored to config settings. Change affects "+n+" loggers.");
			}
			else
				ReikaChatHelper.sendChatToPlayer(ep, r+"You must specify \"enable\", \"disable\", \"source\", or \"default\".");
		}
	}

	@Override
	protected boolean isAdminOnly() {
		return !DragonAPICore.isSinglePlayer();
	}
}
