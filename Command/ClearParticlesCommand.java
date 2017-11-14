package Reika.DragonAPI.Command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import Reika.DragonAPI.IO.ThrottleableEffectRenderer;


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
