/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModFileVersionChecker {

	public static final ModFileVersionChecker instance = new ModFileVersionChecker();

	private final HashMap<String, String> data = new HashMap();

	private ModFileVersionChecker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addMod(DragonAPIMod mod) {
		data.put(mod.getModContainer().getModId(), mod.getFileHash());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void dispatch(ClientLoginEvent evt) {
		for (String mod : data.keySet()) {
			String s = mod+":"+data.get(mod);
			ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, PacketIDs.FILEMATCH.ordinal(), new PacketTarget.ServerTarget(), s);
		}
	}

	public void checkFiles(EntityPlayerMP ep, String s) {
		boolean flag = false;
		String[] parts = s.split(":");
		if (parts.length != 2)
			flag = true;
		String mod = parts[0];
		String hash = data.get(mod);
		if (!flag) {
			if (hash != null) { //Client-only mods will be ignored
				flag = !hash.equals(parts[1]);
			}
		}
		if (flag) {
			this.kick(ep, mod, parts[1], hash);
		}
		else {
			DragonAPICore.log("Player "+ep.getCommandSenderName()+" passed hash check for "+mod+". Hash: "+hash);
		}
	}

	private void kick(EntityPlayerMP ep, String mod, String client, String server) {
		HashKickEvent evt = new HashKickEvent(ep, mod, client, server);
		if (!MinecraftForge.EVENT_BUS.post(evt)) {
			String msg = mod+" jarfile mismatch. Client Hash: "+client+"; Expected (Server) Hash: "+server;
			ReikaPlayerAPI.kickPlayer(ep, msg);
			DragonAPICore.log("Player "+ep.getCommandSenderName()+" kicked due to "+msg);
		}
		else {
			DragonAPICore.log("Player "+ep.getCommandSenderName()+" not kicked for hash mismatch; kick cancelled");
		}
	}

	public static class HashKickEvent extends PlayerEvent {

		public final String serverHash;
		public final String clientHash;
		public final String mod;

		public HashKickEvent(EntityPlayer player, String mod, String client, String server) {
			super(player);
			this.mod = mod;
			serverHash = server;
			clientHash = client;
		}

	}

}
