/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

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
	public void setPartAngles(EntityPlayer ep, float tick) {
		//float pitch = -ep.rotationPitch;
		//float yawHead = -ep.rotationYaw%360-tick*(ep.rotationYaw-ep.prevRotationYaw);
		float yaw = -ep.renderYawOffset%360-tick*(ep.renderYawOffset-ep.prevRenderYawOffset)+180;

		//float pc = pitch*RADIAN;
		yc = yaw*RADIAN;

		this.compensateAngles(tick);
		//float yhc = yawHead*RADIAN;

		//earL.rotateAngleX = pc;
		//hornR.rotateAngleY = yawBody / (180F / (float)Math.PI);
		//earR.rotateAngleX = pc;

		//earR.rotateAngleY = yhc;
		//earL.rotateAngleY = yhc;

		tail.rotateAngleY = yc;
	}

	@Override
	public void renderBodyParts(EntityPlayer ep, float tick) {
		if (ep.equals(Minecraft.getMinecraft().thePlayer) && !Minecraft.getMinecraft().thePlayer.getEntityName().equals("FurryDJ"))
			return;
		this.setPartAngles(ep, tick);

		float pitch = -ep.rotationPitch;
		float yawHead = -ep.rotationYaw%360-tick*(ep.rotationYaw-ep.prevRotationYaw);

		if (tick == 1.0F) {
			yawHead = yhc/RADIAN-6;
			pitch = pc/RADIAN;
		}

		float f5 = 0.0625F;

		double d = 0.1825;
		if (ep.isSneaking())
			d = 0.25;
		GL11.glTranslated(0, d, 0);
		GL11.glRotated(yawHead, 0, 1, 0);
		GL11.glRotated(pitch, 1, 0, 0);
		earL.render(f5);
		earR.render(f5);
		GL11.glRotated(-pitch, 1, 0, 0);
		GL11.glRotated(yawHead, 0, -1, 0);
		GL11.glTranslated(0, -d, 0);

		tail.render(f5);
	}

}
