/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class SplashPotionEvent extends LivingEvent {

	public final Potion potion;
	public final EntityLivingBase thrower;
	public final double effectFactor;
	public final int potionAmplifier;

	public SplashPotionEvent(EntityLivingBase e, EntityLivingBase e2, Potion p, int lvl, double f) {
		super(e);
		thrower = e2;
		potion = p;
		effectFactor = f;
		potionAmplifier = lvl;
	}

	public static void fire(Potion p, EntityLivingBase e2, EntityLivingBase e, int lvl, double f) {
		if (!MinecraftForge.EVENT_BUS.post(new SplashPotionEvent(e, e2, p, lvl, f))) {

			//vanilla code
			int j;
			if ((p.id != Potion.heal.id || e.isEntityUndead()) && (p.id != Potion.harm.id || !e.isEntityUndead())) {
				if (p.id == Potion.harm.id && !e.isEntityUndead() || p.id == Potion.heal.id && e.isEntityUndead()) {
					j = (int)(f * (6 << lvl) + 0.5D);

					if (e2 == null) {
						e.attackEntityFrom(DamageSource.magic, j);
					}
					else {
						e.attackEntityFrom(DamageSource.causeIndirectMagicDamage(e, e2), j);
					}
				}
			}
			else {
				j = (int)(f * (4 << lvl) + 0.5D);
				e.heal(j);
			}
		}
	}

}
