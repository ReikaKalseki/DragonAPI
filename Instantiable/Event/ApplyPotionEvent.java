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
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class ApplyPotionEvent extends LivingEvent {

	public final Potion originalPotion;
	public final int originalPotionAmplifier;
	public final int originalPotionDuration;

	public Potion potion;
	public int potionAmplifier;
	public int potionDuration;

	public ApplyPotionEvent(EntityLivingBase e, PotionEffect pot) {
		super(e);

		originalPotion = Potion.potionTypes[pot.getPotionID()];
		originalPotionAmplifier = pot.getAmplifier();
		originalPotionDuration = pot.getDuration();

		potion = originalPotion;
		potionAmplifier = originalPotionAmplifier;
		potionDuration = originalPotionDuration;
	}

	public static PotionEffect fire(EntityLivingBase e, PotionEffect pot) {
		ApplyPotionEvent evt = new ApplyPotionEvent(e, pot);
		if (MinecraftForge.EVENT_BUS.post(evt))
			return null;
		return new PotionEffect(evt.potion.id, evt.potionDuration, evt.potionAmplifier, pot.getIsAmbient());
	}

}
