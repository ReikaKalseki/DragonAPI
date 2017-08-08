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

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

public class EntityCountCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "entitycount";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

		if (args.length == 1) {
			String c = args[0];
			int amt = this.countEntity(ep.worldObj, c);
			String sg = EnumChatFormatting.GREEN+"Found "+amt+" of "+c+".";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
		}
		else {
			String sg = EnumChatFormatting.RED+"You must specify an entity class!";
			String sg2 = "'EntityAnimal', 'EntityMob', 'EntityCreature', 'EntityLiving', and 'Entity', are accepted parent classes.";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
			ReikaChatHelper.sendChatToPlayer(ep, sg2);
		}
	}

	private int countEntity(World world, String name) {
		List<Entity> li = world.loadedEntityList;
		boolean isMobs = name.equals("EntityMob");
		boolean isAnimals = name.equals("EntityAnimal");
		boolean isCreatures = name.equals("EntityCreature");
		boolean allLiving = name.equals("EntityLiving");
		boolean allEntity = name.equals("Entity");
		int found = 0;
		for (Entity e : li) {
			if (allEntity) {
				found++;
			}
			else if (allLiving) {
				if (e instanceof EntityLiving) {
					found++;
				}
			}
			else if (isMobs) {
				if (e instanceof EntityMob) {
					found++;
				}
			}
			else if (isAnimals) {
				if (e instanceof EntityAnimal) {
					found++;
				}
			}
			else if (isCreatures) {
				if (e instanceof EntityCreature) {
					found++;
				}
			}
			else if (e.getClass().getSimpleName().equals(name)) {
				found++;
			}
		}
		return found;
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
