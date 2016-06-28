/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.event.client;

import net.minecraft.client.particle.EntityFX;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

@Cancelable
@SideOnly(Side.CLIENT)
public class AddParticleEvent extends Event {

	public final EntityFX particle;

	public AddParticleEvent(EntityFX fx) {
		particle = fx;
	}

}
