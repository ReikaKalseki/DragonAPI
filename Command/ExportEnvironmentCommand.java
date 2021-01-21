package Reika.DragonAPI.Command;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import Reika.DragonAPI.Extras.EnvironmentPackager;
import Reika.DragonAPI.IO.ReikaFileReader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ExportEnvironmentCommand extends DragonClientCommand {

	@Override
	@SideOnly(Side.CLIENT)
	public void processCommand(ICommandSender ics, String[] args) {
		if (!Minecraft.getMinecraft().gameSettings.snooperEnabled) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"You need snooper enabled for this to work!");
			return;
		}
		File f = EnvironmentPackager.instance.export();
		if (f == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Could not export environment file. Check your logs for errors.");
		}
		else {
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Game environment exported to file:");
			this.sendChatToSender(ics, ReikaFileReader.getRealPath(f));
		}
	}

	@Override
	public String getCommandString() {
		return "dumpenv";
	}

}
