/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.relauncher.Side;

public class ChatLogger implements IChatListener {

	private List<String> chats = new ArrayList<String>();
	private List<String> clientChats = new ArrayList<String>();

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		chats.add(message.message);
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		clientChats.add(message.message);
		return message;
	}

	public String getLastMessage(Side s) {
		if (s == Side.SERVER)
			return chats.get(chats.size()-1);
		else if (s == Side.CLIENT)
			return clientChats.get(clientChats.size()-1);
		return null;
	}

	public String getLastMessageFromPlayer(Side s, EntityPlayer ep) {
		if (s == Side.SERVER) {
			for (int i = chats.size()-1; i >= 0; i--) {
				String msg = chats.get(i).substring(1);
				if (chats.get(i).startsWith(ep.getEntityName()))
					return msg;
			}
			return null;
		}
		else if (s == Side.CLIENT) {
			for (int i = clientChats.size()-1; i >= 0; i--) {
				String msg = clientChats.get(i).substring(1);
				if (clientChats.get(i).startsWith(ep.getEntityName()))
					return msg;
			}
			return null;
		}
		return null;
	}

}
