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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;


public class WarpForwardCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);

		Vec3 vec = ep.getLookVec();
		if (args.length != 1) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"You need to specify a distance!");
		}
		double dist = Double.parseDouble(args[0]);
		ep.setPositionAndUpdate(ep.posX+vec.xCoord*dist, ep.posY+vec.yCoord*dist, ep.posZ+vec.zCoord*dist);
	}

	@Override
	public String getCommandString() {
		return "warpforward";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
