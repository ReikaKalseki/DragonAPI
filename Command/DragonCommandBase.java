/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public abstract class DragonCommandBase extends CommandBase {

	public abstract String getCommandString();

	@Override
	public final String getCommandName() {
		return this.getCommandString();
	}

	@Override
	public final String getCommandUsage(ICommandSender icommandsender) {
		return "/"+this.getCommandString();
	}

	protected static final void sendChatToSender(ICommandSender ics, String s) {
		ReikaChatHelper.sendChatToPlayer(getCommandSenderAsPlayer(ics), s);
	}

	@Override
	public final int getRequiredPermissionLevel()
	{
		return this.isAdminOnly() ? 4 : 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (DragonAPICore.isSinglePlayer())
			return true;
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP ep = (EntityPlayerMP)sender;
			return !this.isAdminOnly() || ReikaPlayerAPI.isAdmin(ep);
		}
		return super.canCommandSenderUseCommand(sender);
	}

	protected abstract boolean isAdminOnly();

}
