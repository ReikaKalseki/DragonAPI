/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import java.util.HashMap;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.command.DragonCommandBase;
import reika.dragonapi.exception.RegistrationException;


public class ModularLogger {

	public static final ModularLogger instance = new ModularLogger();

	private final HashMap<String, LoggerElement> loggers = new HashMap();

	private ModularLogger() {

	}

	public void addLogger(DragonAPIMod mod, String label) {
		label = label.toLowerCase(Locale.ENGLISH);
		if (loggers.containsKey(label))
			throw new RegistrationException(mod, "Modular logger name '"+label+"' is already taken!");
		loggers.put(label, new LoggerElement(mod, label));
	}

	public void log(String logger, String msg) {
		LoggerElement e = loggers.get(logger.toLowerCase(Locale.ENGLISH));
		if (e == null) {
			DragonAPICore.logError("Tried to use an unregistered logger '"+logger+"'!");
		}
		else {
			if (e.enabled) {
				e.mod.getModLogger().log(msg);
			}
		}
	}

	public boolean isEnabled(String logger) {
		LoggerElement e = loggers.get(logger.toLowerCase(Locale.ENGLISH));
		return e != null && e.enabled;
	}

	private static final class LoggerElement {

		private final DragonAPIMod mod;
		private final String label;

		private boolean enabled;

		public LoggerElement(DragonAPIMod mod, String s) {
			this.mod = mod;
			label = s;
		}

	}

	public static class ModularLoggerCommand extends DragonCommandBase {

		@Override
		public void processCommand(ICommandSender ics, String[] args) {
			if (args.length != 2) {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify a logger ID and a status!");
				return;
			}
			LoggerElement e = instance.loggers.get(args[0].toLowerCase(Locale.ENGLISH));
			if (e == null) {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"Unrecognized logger ID '"+args[0]+"'!");
				return;
			}
			e.enabled = args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("1") || Boolean.parseBoolean(args[1]);
			String status = e.enabled ? "enabled" : "disabled";
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Logger '"+args[0]+"' "+status+".");
		}

		@Override
		public String getCommandString() {
			return "modularlog";
		}

		@Override
		protected boolean isAdminOnly() {
			return true;
		}

	}

}
