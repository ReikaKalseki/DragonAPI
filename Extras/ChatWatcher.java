/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import cpw.mods.fml.common.network.IChatListener;

public class ChatWatcher implements IChatListener {

	public static final ChatWatcher instance = new ChatWatcher();

	private ChatWatcher() {

	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

}
