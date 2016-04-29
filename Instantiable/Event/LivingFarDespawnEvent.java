/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class LivingFarDespawnEvent extends LivingEvent {

	public LivingFarDespawnEvent(EntityLiving entity) {
		super(entity);
	}

	public static boolean fire(EntityLiving e) {
		return !MinecraftForge.EVENT_BUS.post(new LivingFarDespawnEvent(e));
	}

}
