package Reika.DragonAPI.Auxiliary.Trackers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;


public class AFKTracker {

	public static final AFKTracker instance = new AFKTracker();

	public static final int TIMER = 20*DragonOptions.AFK.getValue();

	private PlayerMap<Long> lastActivity = new PlayerMap();
	private PlayerMap<PositionData> lastPosition = new PlayerMap();

	private AFKTracker() {

	}

	public boolean isPlayerAFK(EntityPlayer ep) {
		return TIMER > 0 && lastActivity.containsKey(ep) && lastActivity.get(ep)+TIMER < ep.worldObj.getTotalWorldTime();
	}

	public void markPlayerAFK(EntityPlayer ep) {
		lastActivity.put(ep, 0L);
	}

	public static void refreshPlayer(NetHandlerPlayServer nh, Packet p) {
		if (TIMER == 0)
			return;

		if (p.getClass() != C03PacketPlayer.class) {
			EntityPlayer ep = nh.playerEntity;
			if (p instanceof C04PacketPlayerPosition) { //sent once a second even if afk
				C04PacketPlayerPosition pk = (C04PacketPlayerPosition)p;
				PositionData pd = new PositionData(pk.func_149464_c(), pk.func_149467_d(), pk.func_149471_f(), pk.func_149472_e(), pk.func_149465_i());
				if (pd.equals(instance.lastPosition.get(ep)))
					return;
				instance.lastPosition.put(ep, pd);
			}
			//ReikaJavaLibrary.pConsole("Refreshing "+ep+" @ "+ep.worldObj.getTotalWorldTime());
			if (instance.isPlayerAFK(ep))
				ReikaChatHelper.sendChatToPlayer(ep, "You are no longer AFK.");
			instance.lastActivity.put(ep, ep.worldObj.getTotalWorldTime());
		}
	}

	private static class PositionData {

		private final double posX;
		private final double posY;
		private final double posZ;
		private final double bottomY;
		private final boolean onGround;

		private PositionData(double x, double by, double y, double z, boolean ground) {
			posX = x;
			posY = y;
			posZ = z;
			bottomY = by;
			onGround = ground;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof PositionData) {
				PositionData pd = (PositionData)o;
				return pd.posX == posX && pd.posY == posY && pd.posZ == posZ && pd.bottomY == bottomY && pd.onGround == onGround;
			}
			return false;
		}
	}

}
