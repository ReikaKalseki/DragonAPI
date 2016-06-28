/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.rendering;

import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityFallingBlock;

import org.lwjgl.opengl.GL11;
import reika.dragonapi.instantiable.EntityTumblingBlock;


public class RenderTumblingBlock extends RenderFallingBlock {

	@Override
	public void doRender(EntityFallingBlock e, double d1, double d2, double d3, float f1, float f2) {
		GL11.glPushMatrix();
		EntityTumblingBlock et = (EntityTumblingBlock)e;
		GL11.glTranslated(e.posX-RenderManager.renderPosX, e.posY-RenderManager.renderPosY, e.posZ-RenderManager.renderPosZ);
		GL11.glRotated(et.angleX(), 1, 0, 0);
		GL11.glRotated(et.angleY(), 0, 1, 0);
		GL11.glRotated(et.angleZ(), 0, 0, 1);
		GL11.glTranslated(-e.posX+RenderManager.renderPosX, -e.posY+RenderManager.renderPosY, -e.posZ+RenderManager.renderPosZ);
		super.doRender(e, d1, d2, d3, f1, f2);
		GL11.glPopMatrix();
	}

}
