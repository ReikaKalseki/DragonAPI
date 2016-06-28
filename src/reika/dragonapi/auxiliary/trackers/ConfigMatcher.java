/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary.trackers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.APIPacketHandler.PacketIDs;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.data.maps.PlayerMap;
import reika.dragonapi.instantiable.event.client.ClientLoginEvent;
import reika.dragonapi.instantiable.io.PacketTarget;
import reika.dragonapi.interfaces.config.BooleanConfig;
import reika.dragonapi.interfaces.config.ConfigList;
import reika.dragonapi.interfaces.config.DecimalConfig;
import reika.dragonapi.interfaces.config.IntegerConfig;
import reika.dragonapi.interfaces.config.MatchingConfig;
import reika.dragonapi.interfaces.config.StringConfig;
import reika.dragonapi.libraries.ReikaPlayerAPI;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigMatcher {

	public static final ConfigMatcher instance = new ConfigMatcher();

	private final HashMap<String, Integer> data = new HashMap();

	private final PlayerMap<HashMap<String, Mismatch>> mismatch = new PlayerMap();
	private final PlayerMap<HashSet<String>> checkedValues = new PlayerMap();

	private ConfigMatcher() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addConfigList(DragonAPIMod mod, MatchingConfig[] cfg) {
		for (int i = 0; i < cfg.length; i++) {
			if (cfg[i].enforceMatch())
				this.addConfig(mod, cfg[i]);
		}
	}

	public void addConfig(DragonAPIMod mod, MatchingConfig cfg) {
		data.put(this.getString(mod, cfg), this.getInt(cfg));
	}

	private String getString(DragonAPIMod mod, ConfigList cfg) {
		return mod.getTechnicalName()+"::"+cfg.getLabel();
	}

	private int getInt(ConfigList cfg) {
		if (cfg instanceof IntegerConfig && ((IntegerConfig)cfg).isNumeric())
			return ((IntegerConfig)cfg).getValue();
		else if (cfg instanceof BooleanConfig && ((BooleanConfig)cfg).isBoolean())
			return ((BooleanConfig)cfg).getState() ? 1 : 0;
		else if (cfg instanceof DecimalConfig && ((DecimalConfig)cfg).isDecimal())
			return (int)(((DecimalConfig)cfg).getFloat()*10000);
		else if (cfg instanceof StringConfig && ((StringConfig)cfg).isString())
			return ((StringConfig)cfg).getString().hashCode();
		return -1;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void dispatch(ClientLoginEvent evt) {
		for (String s : data.keySet()) {
			ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, PacketIDs.CONFIGSYNC.ordinal(), new PacketTarget.ServerTarget(), s, data.get(s));
		}
	}

	public void register(EntityPlayer ep, String s, int val) {
		HashMap<String, Mismatch> map = mismatch.get(ep);
		if (map == null) {
			map = new HashMap();
			mismatch.put(ep, map);
		}
		HashSet<String> set = checkedValues.get(ep);
		if (set == null) {
			set = new HashSet(data.keySet());
			checkedValues.put(ep, set);
		}

		Integer get = data.get(s);
		if (get == null) { //Clientside config not expected by server
			map.put(s, new ConfigStructureMismatch(s));
		}
		else if (get.intValue() != val) {
			map.put(s, new Mismatch(s, get.intValue(), val));
		}
		DragonAPICore.debug("Player "+ep.getCommandSenderName()+" logging in. Performing config check. "+mismatch+" / "+data);
	}

	public void match(EntityPlayerMP ep) {
		Collection<Mismatch> c = mismatch.get(ep).values();
		for (String s : checkedValues.get(ep)) { //Serverside configs for which client never sent data
			c.add(new ConfigStructureMismatch(s));
		}
		if (!c.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			sb.append("Player "+ep.getCommandSenderName()+" kicked: ");
			for (Mismatch m : c) {
				sb.append(m.getMessage());
				sb.append("; ");
				sb2.append(m.getLogMessage(ep));
				sb2.append("; ");
			}
			ReikaPlayerAPI.kickPlayer(ep, sb.toString());
			DragonAPICore.log(sb2.toString());
		}
	}

	private static class Mismatch {

		protected final String displayName;
		protected final int serverValue;
		protected final int clientValue;

		protected Mismatch(String tag, int s, int c) {
			serverValue = s;
			clientValue = c;

			displayName = getDisplayName(tag);
		}

		private static String getDisplayName(String tag) {
			String[] parts = tag.split("::");
			return parts[0]+" - \""+parts[1]+"\"";
		}

		protected String getMessage() {
			return "Critical config mismatch. Config Entry: "+displayName+", Server Value="+serverValue+", Client Value="+clientValue;
		}

		protected final String getLogMessage(EntityPlayer ep) {
			return "Client "+ep.getCommandSenderName()+" attempted to join with a "+this.getMessage()+". Login cannot continue.";
		}

	}

	private static class ConfigStructureMismatch extends Mismatch {

		protected ConfigStructureMismatch(String s) {
			super(s, 0, 0);
		}

		@Override
		protected String getMessage() {
			return "Different mod configuration structure: "+displayName+" missing on one side, likely indicating mod version mismatch";
		}

	}

}
