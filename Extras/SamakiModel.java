/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class SamakiModel extends ModifiedPlayerModel {

	ModelRenderer earR;
	ModelRenderer earL;
	ModelRenderer tail;

	public SamakiModel() {
		super();
	}

	@Override
	protected void setPositions() {
		earL.setRotationPoint(0F, 0F, 0F);
		earR.setRotationPoint(0F, 0F, 0F);
		tail.setRotationPoint(0F, 0F, 0F);

		this.setRotation(earL, 0F, 0F, 0.7853982F);
		this.setRotation(earR, 0F, 0F, 0.7853982F);
		this.setRotation(tail, 0.1745329F, 0F, 0F);
	}

	@Override
	protected void init() {
		earL = new ModelRenderer(this, 48, 0);
		earL.addBox(-5.7F, -8.3F, -2F, 3, 3, 1);
		earL.setTextureSize(64, 32);
		earL.mirror = true;

		earR = new ModelRenderer(this, 48, 0);
		earR.addBox(-8.3F, -5.7F, -2F, 3, 3, 1);
		earR.setTextureSize(64, 32);
		earR.mirror = true;

		tail = new ModelRenderer(this, 32, 0);
		tail.addBox(-1.5F, 10F, 0F, 3, 11, 3);
		tail.setTextureSize(64, 32);
		tail.mirror = true;

		this.setPositions();
	}

	@Override
	protected void setPartAngles(EntityPlayer ep, float tick) {
		float pitch = -ep.rotationPitch;
		float yawHead = -ep.rotationYaw%360-tick*(ep.rotationYaw-ep.prevRotationYaw);
		float yaw = -ep.renderYawOffset%360-tick*(ep.renderYawOffset-ep.prevRenderYawOffset)+180;

		pc = pitch*RADIAN;
		yc = yaw*RADIAN;

		//this.compensateAngles(tick);
		yhc = yawHead*RADIAN;

		Tessellator.instance.startDrawing(GL11.GL_LINE_LOOP);
		Tessellator.instance.addVertex(0, 0, 0);
		Tessellator.instance.addVertex(0, 2, 0);
		Tessellator.instance.draw();
		;//earL.rotateAngleX = 0;
		;//earR.rotateAngleX = 0;
		;//earL.rotateAngleY = 0;//yhc-yc;
		;//earR.rotateAngleY = 0;//yhc-yc;
		;//earL.rotateAngleZ = 0;//45*RADIAN;
		;//earR.rotateAngleZ = 0;//45*RADIAN;

		//earR.rotateAngleY = yhc-yc;
		//earL.rotateAngleY = yhc-yc;

		//tail.rotateAngleY = yc;
	}

	@Override
	public void bindTexture() {
		ReikaTextureHelper.bindFinalTexture(DragonAPICore.class, "/Reika/DragonAPI/Resources/samaki_tex.png");
	}

	@Override
	public void renderBodyParts(EntityPlayer ep, float tick) {
		//this.setPartAngles(ep, tick);

		float pitch = ep.rotationPitch;
		float yawHead = -ep.rotationYaw%360-tick*(ep.rotationYaw-ep.prevRotationYaw);

		if (tick == 1.0F) {
			yawHead = yhc-6;
		}
		yc = ep.rotationYaw;
		yhc = ep.rotationYawHead;
		pc = ep.rotationPitch;
		float rc = ep.renderYawOffset-yc;

		float f5 = 0.0625F;

		double d = 0.1825;

		tail.render(f5);

		if (ep.isSneaking()) {
			d = 0.25;

			GL11.glTranslated(0.02, -0.1, 0.05);
			GL11.glRotated(-22.5, 1, 0, 0);
		}
		GL11.glTranslated(0, d, 0);
		GL11.glRotated(rc, 0, 1, 0);
		GL11.glRotated(pc, 1, 0, 0);
		earL.render(f5);
		earR.render(f5);
		GL11.glRotated(-pc, 1, 0, 0);
		GL11.glRotated(-rc, 0, 1, 0);
		GL11.glTranslated(0, -d, 0);
	}

}
