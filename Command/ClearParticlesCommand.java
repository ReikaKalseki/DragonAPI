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

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import Reika.DragonAPI.Extras.ThrottleableEffectRenderer;


public class ClearParticlesCommand extends DragonClientCommand {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		int amt = ThrottleableEffectRenderer.getRegisteredInstance().getParticleCount();
		ThrottleableEffectRenderer.getRegisteredInstance().clearEffects(Minecraft.getMinecraft().theWorld);
		this.sendChatToSender(ics, "Cleared "+amt+" particles.");
	}

	@Override
	public String getCommandString() {
		return "clearfx";
	}

}
