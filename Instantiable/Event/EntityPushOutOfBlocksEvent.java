/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class EntityPushOutOfBlocksEvent extends EntityEvent {

	public EntityPushOutOfBlocksEvent(Entity e) {
		super(e);
	}

}
