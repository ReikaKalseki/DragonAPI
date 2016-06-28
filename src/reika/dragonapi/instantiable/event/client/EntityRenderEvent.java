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

import net.minecraft.entity.Entity;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRenderEvent extends Event {

	public final Entity entity;
	public final double renderPosX;
	public final double renderPosY;
	public final double renderPosZ;
	public final float partialTickTime;

	public final float float2;
	public final boolean flag;

	public EntityRenderEvent(Entity te, double par2, double par4, double par6, float par8, float par9, boolean b) {
		entity = te;
		renderPosX = par2;
		renderPosY = par4;
		renderPosZ = par6;
		partialTickTime = par8;

		float2 = par9;
		flag = b;
	}

}
