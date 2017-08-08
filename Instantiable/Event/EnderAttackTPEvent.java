/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class EnderAttackTPEvent extends LivingAttackEvent {

	public EnderAttackTPEvent(EntityEnderman entity, DamageSource source, float ammount) {
		super(entity, source, ammount);
	}

	public static boolean fire(EntityEnderman e, DamageSource src, float amt) {
		Event evt = new EnderAttackTPEvent(e, src, amt);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return src instanceof EntityDamageSourceIndirect;
		}
	}

}
