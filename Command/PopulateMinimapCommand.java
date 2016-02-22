/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.EnumSet;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class PopulateMinimapCommand extends DragonCommandBase {

	private static boolean init = false;

	private static void init() {
		TickRegistry.instance.registerTickHandler(PopulationTickHandler.instance);
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length < 1 || args.length > 3) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Invalid arguments. Format: /populatemap [range] <stepsize> <delay>");
			return;
		}
		if (!init) {
			init();
			init = true;
		}
		if (args[0].equalsIgnoreCase("clear")) {
			PopulationTickHandler.instance.movements.clear();
			return;
		}
		int r = Integer.parseInt(args[0]);
		int d = args.length >= 2 ? Integer.parseInt(args[1]) : 500;
		int t = args.length >= 3 ? Integer.parseInt(args[2]) : 100;
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		PopulationTickHandler.instance.movements.put(ep, new PlayerMovement(ep, r, d, t));
	}

	@Override
	public String getCommandString() {
		return "populatemap";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	private static class PopulationTickHandler implements TickHandler {

		private static final PopulationTickHandler instance = new PopulationTickHandler();

		private PlayerMap<PlayerMovement> movements = new PlayerMap();

		private PopulationTickHandler() {

		}

		@Override
		public void tick(TickType type, Object... tickData) {
			EntityPlayer ep = (EntityPlayer)tickData[0];
			if (!ep.worldObj.isRemote) {
				PlayerMovement p = movements.get(ep);
				if (p != null) {
					if (p.update()) {
						movements.remove(ep);
					}
				}
			}
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.PLAYER);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return "mappopulate";
		}

	}

	private static class PlayerMovement {

		private final EntityPlayer player;

		private final int stepSize;
		private final int delayTime;
		private final int range;

		private final int originX;
		private final int originZ;

		private int cooldown;
		private int posX;
		private int posZ;

		private PlayerMovement(EntityPlayer ep, int r, int s, int t) {
			player = ep;

			originX = MathHelper.floor_double(ep.posX);
			originZ = MathHelper.floor_double(ep.posZ);

			range = r;
			stepSize = s;
			delayTime = t;

			posX = -range;
			posZ = -range;
		}

		private boolean update() {
			if (cooldown > 0) {
				cooldown--;
				return false;
			}
			cooldown = delayTime;
			boolean flag1 = false;
			boolean flag2 = false;
			posX += stepSize;
			if (posX > originX+range) {
				posX = originX-range;
				flag1 = true;
			}
			if (flag1) {
				posZ += stepSize;
				//ReikaJavaLibrary.pConsole(posY+" > "+posZ+":"+range+" > "+ores.getSize(), Side.SERVER);
				if (posZ > originZ+range) {
					posZ = originZ-range;
					flag2 = true;
				}
				if (flag2) {
					DragonAPICore.log("Map population of radius "+range+" complete.");
					return true;
				}
			}
			player.setPositionAndUpdate(posX, 110, posZ);
			DragonAPICore.log("Populating map: Stepping player "+player.getCommandSenderName()+" to "+posX+", "+posZ);
			return false;
		}

	}

}
