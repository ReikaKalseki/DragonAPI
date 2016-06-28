/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class AttackAggroEvent extends LivingAttackEvent {

	public AttackAggroEvent(EntityMob entity, DamageSource source, float ammount) {
		super(entity, source, ammount);
	}

	public static boolean fire(EntityMob e, DamageSource src, float amt) {
		Event evt = new AttackAggroEvent(e, src, amt);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				Entity att = src.getEntity();
				return att != e && (e instanceof EntityPigZombie ? att instanceof EntityPlayer : true);
		}
	}

}
