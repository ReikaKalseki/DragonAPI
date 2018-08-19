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

import net.minecraft.command.ICommandSender;

@Deprecated
public class FlushChunkDataCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {

	}

	@Override
	public String getCommandString() {
		return "flushchunks";
	}

	@Override
	protected boolean isAdminOnly() {
		return false;
	}

}
