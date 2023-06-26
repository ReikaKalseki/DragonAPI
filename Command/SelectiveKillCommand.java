/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.IEntitySelector;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class SelectiveKillCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "selectivekill";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		World world = null;
		if (ics instanceof CommandBlockLogic) {
			world = ((CommandBlockLogic)ics).getEntityWorld();
		}
		else {
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			world = ep.worldObj;
		}

		if (args.length == 2) {
			String c = args[0];
			int percentage = ReikaJavaLibrary.safeIntParse(args[1]);
			if (percentage == 0) {
				String sg = EnumChatFormatting.RED+"Invalid percentage.";
				sendChatToSender(ics, sg);
				return;
			}
			int amt = this.killEntities(world, c, percentage);
			String sg;
			if (percentage < 0)
				sg = EnumChatFormatting.GREEN+"Killed all but "+amt+" of "+c+".";
			else
				sg = EnumChatFormatting.GREEN+"Killed "+amt+" of "+c+".";
			sendChatToSender(ics, sg);
		}
		else {
			String sg = EnumChatFormatting.RED+"You must specify an entity class and a kill percentage!";
			String sg2 = "'EntityAnimal', 'EntityMob', 'EntityCreature', and 'EntityLiving' are accepted parent classes.";
			sendChatToSender(ics, sg);
			sendChatToSender(ics, sg2);
		}
	}

	private int killEntities(World world, String name, int percentage) {
		ArrayList<Entity> li = new ArrayList(world.loadedEntityList);
		IEntitySelector sel = this.getFilter(name);
		li.removeIf(e -> !sel.isEntityApplicable(e));
		if (li.isEmpty())
			return 0;
		int killed = 0;
		if (percentage < 0) {
			for (int i = 0; i < percentage && !li.isEmpty(); i++) {
				li.remove(world.rand.nextInt(li.size()));
			}
			percentage = 100;
		}
		for (Entity e : li) {
			if (ReikaRandomHelper.doWithChance(percentage)) {
				e.setDead();
				killed++;
			}
		}
		return killed;
	}

	private IEntitySelector getFilter(String name) {
		switch(name) {
			case "Tamed":
				return e -> ReikaEntityHelper.isTamed(e);
			case "EntityMob":
				return e -> e instanceof EntityMob && !ReikaEntityHelper.isTamed(e);
			case "EntityAnimal":
				return e -> e instanceof EntityAnimal && !ReikaEntityHelper.isTamed(e);
			case "EntityCreature":
				return e -> e instanceof EntityCreature && !ReikaEntityHelper.isTamed(e);
			case "EntityLiving":
				return e -> e instanceof EntityLiving && !ReikaEntityHelper.isTamed(e);
			case "Entity":
				return e -> !(e instanceof EntityPlayer);
			default:
				return e -> e.getClass().getSimpleName().equals(name);
		}
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
