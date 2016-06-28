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
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;


public class EntityAboutToRayTraceEvent extends EntityEvent {

	public final Vec3 startPos;
	public final Vec3 endPos;

	public EntityAboutToRayTraceEvent(Entity e) {
		super(e);

		startPos = Vec3.createVectorHelper(e.posX, e.posY, e.posZ);
		endPos = Vec3.createVectorHelper(e.posX + e.motionX, e.posY + e.motionY, e.posZ + e.motionZ);
	}

	public static void fire(Entity e) {
		MinecraftForge.EVENT_BUS.post(new EntityAboutToRayTraceEvent(e));
	}
}
