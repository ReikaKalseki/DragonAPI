/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public abstract class ChatEvent extends Event {

	public final String chatMessage;

	public ChatEvent(String message) {
		super();
		chatMessage = message;
	}

	public static class ChatEventPre extends ChatEvent {

		public ChatEventPre(String msg) {
			super(msg);
		}

	}

	public static class ChatEventPost extends ChatEvent {

		public ChatEventPost(String msg) {
			super(msg);
		}

	}

	public static void firePre(IChatComponent msg) {
		MinecraftForge.EVENT_BUS.post(new ChatEventPre(msg.getUnformattedText()));
	}

	public static void firePost(IChatComponent msg) {
		MinecraftForge.EVENT_BUS.post(new ChatEventPost(msg.getUnformattedText()));
	}

}
