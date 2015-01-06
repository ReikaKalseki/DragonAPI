/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;

public class ChestRepopulationCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {

	}

	@Override
	public String getCommandString() {
		return "regenchests";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
