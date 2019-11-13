/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
public class EntityRenderEvent extends Event {

	public final Render renderer;
	public final Entity entity;
	public final double renderPosX;
	public final double renderPosY;
	public final double renderPosZ;
	public final float partialTickTime;

	public final float float2;

	public EntityRenderEvent(Render r, Entity e, double par2, double par4, double par6, float par8, float par9) {
		renderer = r;
		entity = e;
		renderPosX = par2;
		renderPosY = par4;
		renderPosZ = par6;
		partialTickTime = par8;

		float2 = par9;
	}

	public static void fire(Render r, Entity e, double par2, double par4, double par6, float par8, float par9) {
		EntityRenderEvent evt = new EntityRenderEvent(r, e, par2, par4, par6, par8, par9);
		if (!MinecraftForge.EVENT_BUS.post(evt)) {
			r.doRender(e, par2, par4, par6, par8, par9);
		}
	}

}
