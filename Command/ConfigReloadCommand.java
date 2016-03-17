package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;


public class ConfigReloadCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		ControlledConfig c = ControlledConfig.getForMod(args[0]);
		if (c == null) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"No such config for mod '"+args[0]+"'!");
		}
		else {
			c.reload();
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
