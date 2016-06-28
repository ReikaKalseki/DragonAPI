/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import reika.dragonapi.libraries.ReikaEntityHelper;

public class ConfigurableEntitySelector implements IEntitySelector {

	public boolean hostiles = true;
	public boolean animals = true;
	public boolean players = true;

	@Override
	public boolean isEntityApplicable(Entity e) {
		if (e instanceof EntityLivingBase) {
			if (e instanceof EntityPlayer)
				return players;
			if (e instanceof EntityAnimal)
				return animals;
			if (ReikaEntityHelper.isHostile((EntityLivingBase)e))
				return hostiles;
		}
		return false;
	}

}
