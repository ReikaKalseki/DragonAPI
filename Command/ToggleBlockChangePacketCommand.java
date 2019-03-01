/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import net.minecraft.command.ICommandSender;

import Reika.DragonAPI.Extras.ChangePacketRenderer;

public class ToggleBlockChangePacketCommand extends DragonClientCommand {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		ChangePacketRenderer.isActive = args.length > 0 && Boolean.parseBoolean(args[0]);
		ChangePacketRenderer.instance.clear();
	}

	@Override
	public String getCommandString() {
		return "chgpkt";
	}

}
