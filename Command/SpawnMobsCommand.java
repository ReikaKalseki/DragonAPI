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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class SpawnMobsCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length != 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"You must specify an entity name and a number to spawn.");
			return;
		}
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		World world = ep.worldObj;
		Entity test = EntityList.createEntityByName(args[0], world);
		if (test != null) {
			int n = Integer.parseInt(args[1]);
			for (int i = 0; i < n; i++) {
				double ex = ReikaRandomHelper.getRandomPlusMinus(ep.posX, 12);
				double ey = ReikaRandomHelper.getRandomPlusMinus(ep.posY, 4);
				double ez = ReikaRandomHelper.getRandomPlusMinus(ep.posZ, 12);
				int dy = MathHelper.floor_double(ey)-1;
				int dx = MathHelper.floor_double(ex);
				int dz = MathHelper.floor_double(ez);
				while (world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
					ey--;
					dy--;
				}
				dy = MathHelper.floor_double(ey)+1;
				while (!world.getBlock(dx, dy, dz).isAir(world, dx, dy, dz)) {
					ey++;
					dy++;
				}
				Entity e = EntityList.createEntityByName(args[0], world);
				e.setLocationAndAngles(ex, ey, ez, ReikaRandomHelper.getSafeRandomInt(360), (float)ReikaRandomHelper.getRandomPlusMinus(0, 45D));
				if (e instanceof EntityLiving) {
					((EntityLiving)e).onSpawnWithEgg(null);
				}
				world.spawnEntityInWorld(e);
			}
		}
		else {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"Unrecognized entity '"+args[0]+"'.");
			return;
		}
	}

	@Override
	public String getCommandString() {
		return "spawnmobs";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
