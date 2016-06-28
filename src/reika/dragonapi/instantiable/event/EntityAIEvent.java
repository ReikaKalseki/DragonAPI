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

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;

//Unimplemented
public class EntityAIEvent extends EntityEvent {

	public EntityAIEvent(EntityLivingBase e) {
		super(e);
	}

	public static boolean fire(EntityLivingBase e) {
		return false;
	}

}
