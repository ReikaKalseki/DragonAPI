package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;


public class GetUUIDCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer caller = this.getCommandSenderAsPlayer(ics);
		EntityPlayer target = args.length == 0 ? caller : caller.worldObj.getPlayerEntityByName(args[1]);
		this.sendChatToSender(ics, target.getCommandSenderName()+"'s UUID: "+target.getUniqueID().toString());
	}

	@Override
	public String getCommandString() {
		return "getuuid";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
