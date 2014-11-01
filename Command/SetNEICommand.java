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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

/** Temporary class! */
public class SetNEICommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		int val = Integer.parseInt(args[0]);
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.NEIDEPTH.ordinal(), ep, val);
		ReikaChatHelper.sendChatToPlayer(ep, "NEI GUI render depth set to "+val+".");
	}

	@Override
	public String getCommandString() {
		return "neidepth";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
