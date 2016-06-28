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

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.event.entity.living.LivingEvent;

public class CreeperExplodeEvent extends LivingEvent {

	public final EntityCreeper creeper;

	public CreeperExplodeEvent(EntityCreeper e) {
		super(e);
		creeper = e;
	}

}
