/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.TameHostile;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class SelectiveKillCommand extends DragonCommandBase {

	@Override
	public String getCommandString() {
		return "selectivekill";
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

		if (args.length == 2) {
			String c = args[0];
			int percentage = ReikaJavaLibrary.safeIntParse(args[1]);
			if (percentage == 0) {
				String sg = EnumChatFormatting.RED+"Invalid percentage.";
				ReikaChatHelper.sendChatToPlayer(ep, sg);
				return;
			}
			int amt = this.killEntity(ep.worldObj, c, percentage);
			String sg = EnumChatFormatting.GREEN+"Killed "+amt+" of "+c+".";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
		}
		else {
			String sg = EnumChatFormatting.RED+"You must specify an entity class and a kill percentage!";
			String sg2 = "'EntityAnimal', 'EntityMob', 'EntityCreature', and 'EntityLiving' are accepted parent classes.";
			ReikaChatHelper.sendChatToPlayer(ep, sg);
			ReikaChatHelper.sendChatToPlayer(ep, sg2);
		}
	}

	private int killEntity(World world, String name, int percentage) {
		List<Entity> li = world.loadedEntityList;
		boolean isMobs = name.equals("EntityMob");
		boolean isAnimals = name.equals("EntityAnimal");
		boolean isCreatures = name.equals("EntityCreature");
		boolean allLiving = name.equals("EntityLiving");
		int killed = 0;
		for (int i = 0; i < li.size(); i++) {
			Entity e = li.get(i);
			if (allLiving) {
				if (e instanceof EntityLiving) {
					boolean protect = false;
					if (e instanceof EntityTameable) {
						protect = ((EntityTameable)e).isTamed();
					}
					if (e instanceof TameHostile)
						protect = true;
					if (!protect) {
						if (ReikaRandomHelper.doWithChance(percentage)) {
							e.setDead();
							killed++;
						}
					}
				}
			}
			else if (isMobs) {
				if (e instanceof EntityMob && !(e instanceof TameHostile)) {
					if (ReikaRandomHelper.doWithChance(percentage)) {
						e.setDead();
						killed++;
					}
				}
			}
			else if (isAnimals) {
				if (e instanceof EntityAnimal) {
					boolean protect = false;
					if (e instanceof EntityTameable) {
						protect = ((EntityTameable)e).isTamed();
					}
					if (!protect) {
						if (ReikaRandomHelper.doWithChance(percentage)) {
							e.setDead();
							killed++;
						}
					}
				}
			}
			else if (isCreatures) {
				if (e instanceof EntityCreature) {
					boolean protect = false;
					if (e instanceof EntityTameable) {
						protect = ((EntityTameable)e).isTamed();
					}
					if (!protect) {
						if (ReikaRandomHelper.doWithChance(percentage)) {
							e.setDead();
							killed++;
						}
					}
				}
			}
			else if (e.getClass().getSimpleName().equals(name)) {
				if (ReikaRandomHelper.doWithChance(percentage)) {
					e.setDead();
					killed++;
				}
			}
		}
		return killed;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

}
