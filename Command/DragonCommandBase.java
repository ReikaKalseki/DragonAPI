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

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

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

}
