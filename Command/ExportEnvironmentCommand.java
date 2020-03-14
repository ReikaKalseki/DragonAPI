package Reika.DragonAPI.Command;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import Reika.DragonAPI.Extras.EnvironmentPackager;


public class ExportEnvironmentCommand extends DragonClientCommand {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		File f = EnvironmentPackager.instance.export();
		if (f == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Could not export environment file. Check your logs for errors.");
		}
		else {
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Game environment exported to file:");
			this.sendChatToSender(ics, f.getAbsolutePath());
		}
	}

	@Override
	public String getCommandString() {
		return "dumpenv";
	}

}
