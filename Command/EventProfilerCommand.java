/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.Trackers.EventProfiler;
import Reika.DragonAPI.Auxiliary.Trackers.EventProfiler.EventProfile;
import Reika.DragonAPI.Auxiliary.Trackers.EventProfiler.ProfileStartStatus;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;


public class EventProfilerCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length < 1) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Wrong number of arguments. Specify 'disable', 'enable', or 'display'.");
			return;
		}
		switch(args[0].toLowerCase(Locale.ENGLISH)) {
			case "disable":
				EventProfiler.finishProfiling();
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Profiling finished.");
				break;
			case "enable":
				if (args.length < 2) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify an event type (class)!");
					return;
				}
				ProfileStartStatus st = EventProfiler.startProfiling(args[1]);
				switch (st) {
					case SUCCESS:
						this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Profiling started for events of type "+args[1]);
						break;
					case ALREADYRUNNING:
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Profiling already running!");
						break;
					case NOSUCHCLASS:
						this.sendChatToSender(ics, EnumChatFormatting.RED+"No such class '"+args[1]+"'!");
						break;
					case NOTANEVENT:
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Class '"+args[1]+"' does not extend Event!");
						break;
				}
				break;
			case "display":
				String type = EventProfiler.getProfiledEventType();
				ArrayList<EventProfile> li = EventProfiler.getProfilingData();
				int fires = EventProfiler.getEventFireCount();
				long total = EventProfiler.getTotalProfilingTime();
				String totalt = String.format("%.6f", total/1000000D);
				String desc = "Profiling data for event type "+type+" contains "+li.size()+" handlers across "+fires+" event fires, total time "+totalt+" ms:";
				this.sendChatToSender(ics, desc);
				DragonAPICore.log(desc);
				for (EventProfile g : li) {
					long time = g.getAverageTime();
					String s = ReikaStringParser.padToLength("'"+g.identifier+"'", 60, " ");
					double percent = g.getTotalTime()*100D/total;
					String sg = String.format("Handler %s - Average Time Per Fire: %7.3f microseconds (%2.3f%s)", s, time/1000D, percent, "%%");
					this.sendChatToSender(ics, sg);
					DragonAPICore.log(sg);
				}
				break;
			default:
				this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid argument. Specify 'disable', 'enable', or 'display'.");
				break;
		}
	}

	@Override
	public String getCommandString() {
		return "profileevent";
	}

	@Override
	protected boolean isAdminOnly() {
		return DragonOptions.ADMINPROFILERS.getState();
	}

}
