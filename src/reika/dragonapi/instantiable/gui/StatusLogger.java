/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import reika.dragonapi.libraries.io.ReikaChatHelper;


public class StatusLogger {

	private final ArrayList<String> data = new ArrayList();

	public void addStatus(String sg, boolean state) {
		this.addStatus(sg, state ? State.ACTIVE : State.INACTIVE);
	}

	public void addStatus(String sg, State s) {
		data.add(s.color+sg+": "+s.tag);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s : data) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	public void sendToPlayer(EntityPlayer ep) {
		for (String s : data) {
			ReikaChatHelper.sendChatToPlayer(ep, s);
		}
	}

	public static enum State {
		ACTIVE(EnumChatFormatting.GREEN, "True"),
		INACTIVE(EnumChatFormatting.RED, "False"),
		CONDITIONAL(EnumChatFormatting.BLUE, "Conditional"),
		WARN(EnumChatFormatting.YELLOW, "Warning"),
		ERROR(EnumChatFormatting.LIGHT_PURPLE, "Error");

		private final EnumChatFormatting color;
		private final String tag;

		private State(EnumChatFormatting c, String s) {
			color = c;
			tag = s;
		}
	}

}
