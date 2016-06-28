/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.extras;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.ModList;
import reika.dragonapi.APIPacketHandler.PacketIDs;
import reika.dragonapi.auxiliary.trackers.CommandableUpdateChecker;
import reika.dragonapi.auxiliary.trackers.PlayerFirstTimeTracker;
import reika.dragonapi.auxiliary.trackers.PlayerHandler.PlayerTracker;
import reika.dragonapi.libraries.ReikaPlayerAPI;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
import cpw.mods.fml.common.eventhandler.Event;

public final class LoginHandler implements PlayerTracker {

	public static final LoginHandler instance = new LoginHandler();

	private static final String reikaMessage = getReikaWelcome();

	private LoginHandler() {

	}

	private static String getReikaWelcome() {
		StringBuilder sb = new StringBuilder();
		sb.append("Welcome ");
		sb.append(EnumChatFormatting.LIGHT_PURPLE.toString()+"Reika");
		sb.append(EnumChatFormatting.WHITE.toString()+", Developer of:\n");
		sb.append(EnumChatFormatting.GOLD.toString()+"   DragonAPI\n");
		List<ModList> li = ModList.getReikasMods();
		for (int i = 0; i < li.size(); i++) {
			ModList mod = li.get(i);
			sb.append(EnumChatFormatting.GOLD.toString()+"   ");
			sb.append(mod.getModLabel());
			sb.append(" ");
			if (mod.isLoaded()) {
				sb.append(EnumChatFormatting.GREEN.toString()+"(Installed)");
			}
			else {
				sb.append(EnumChatFormatting.RED.toString()+"(Not Installed)");
			}
			sb.append(EnumChatFormatting.WHITE.toString()+"\n");
		}
		sb.append("to the server!");
		return sb.toString();
	}

	@Override
	public void onPlayerLogin(EntityPlayer ep) {
		boolean flag = ReikaObfuscationHelper.isDeObfEnvironment();
		if (ReikaPlayerAPI.isReika(ep)) {
			ReikaChatHelper.sendChatToAllOnServer(reikaMessage);
		}
		else {

		}

		PlayerFirstTimeTracker.checkPlayer(ep);
		CommandableUpdateChecker.instance.notifyPlayer(ep);
		if (ep instanceof EntityPlayerMP) {
			EntityPlayerMP emp = (EntityPlayerMP)ep;
			syncPlayer(emp);
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.LOGIN.ordinal(), emp, 1);
		}
		MinecraftForge.EVENT_BUS.post(new PlayerEnteredDimensionEvent(ep, ep.worldObj.provider.dimensionId));
	}

	@Override
	public void onPlayerLogout(EntityPlayer ep) {
		if (ep instanceof EntityPlayerMP) {
			EntityPlayerMP emp = (EntityPlayerMP)ep;
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.LOGOUT.ordinal(), emp);
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player, int from, int to) {
		MinecraftForge.EVENT_BUS.post(new PlayerEnteredDimensionEvent(player, player.worldObj.provider.dimensionId));
		if (player instanceof EntityPlayerMP) {
			syncPlayer((EntityPlayerMP)player);
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.LOGIN.ordinal(), (EntityPlayerMP)player, 0);
		}
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		MinecraftForge.EVENT_BUS.post(new PlayerEnteredDimensionEvent(player, player.worldObj.provider.dimensionId));
		if (player instanceof EntityPlayerMP) {
			syncPlayer((EntityPlayerMP)player);
		}
	}

	private static void syncPlayer(EntityPlayerMP player) {
		ReikaPlayerAPI.syncCustomData(player);
		ReikaPlayerAPI.syncAttributes(player);
	}

	public static final class PlayerEnteredDimensionEvent extends Event {

		public final int dimensionID;
		public final EntityPlayer player;

		public PlayerEnteredDimensionEvent(EntityPlayer ep, int dim) {
			player = ep;
			dimensionID = dim;
		}
	}

}
