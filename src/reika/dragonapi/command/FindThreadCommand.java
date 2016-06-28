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
import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;


public class FindThreadCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length < 1) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify a thread name, and optionally an action.");
			return;
		}
		Thread t = ReikaJavaLibrary.getThreadByName(args[0]);
		if (t == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"No such thread '"+args[0]+"'");
			return;
		}
		this.sendChatToSender(ics, "Thread class: "+t.getClass().getName());
		this.sendChatToSender(ics, "Thread status: "+t.getState());
		this.sendChatToSender(ics, "Thread priority: "+t.getPriority());
		this.sendChatToSender(ics, "Thread is alive: "+t.isAlive());
		this.sendChatToSender(ics, "Thread is daemon: "+t.isDaemon());

		if (args.length == 2) {
			switch(args[1].toLowerCase(Locale.ENGLISH)) {
				case "stack":
				case "stacktrace":
				case "trace":
					this.sendChatToSender(ics, "Thread class: "+t.getStackTrace());
					break;
				case "kill":
				case "end":
				case "terminate":
				case "stop":
					t.stop();
					this.sendChatToSender(ics, "Thread '"+t.getName()+"' stopped.");
					break;
			}
		}
	}

	@Override
	public String getCommandString() {
		return "findthread";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
