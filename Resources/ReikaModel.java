/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Resources;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

public class ReikaModel extends ModifiedPlayerModel {

	public ModelRenderer hornL;
	public ModelRenderer hornR;
	public ModelRenderer wingL;
	public ModelRenderer wingR;
	public ModelRenderer tail;
	public ModelRenderer tail2;
	public ModelRenderer tail3;
	public ModelRenderer back;
	public ModelRenderer back2;
	public ModelRenderer back3;

	public float getHornY() {
		return -9F;
	}

	public float getHornX() {
		return -1F;
	}

	public float getWingAngle() {
		return 0.7853982F;
	}

	public float getHornZ() {
		return -3F;
	}

	public ReikaModel() {
		super();
	}

	public void renderAll(Entity e, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(e, par2, par3, par4, par5, par6, par7);
		this.renderBodyParts((EntityPlayer)e, 0);
	}

	@Override
	public void render(Entity e, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(e, par2, par3, par4, par5, par6, par7);
	}

	@Override
	public void renderBodyParts(EntityPlayer ep, float tick) {
		this.setPartAngles(ep, tick);
		float f5 = 0.0625F;
		tail.render(f5);
		tail3.render(f5);
		tail2.render(f5);
		back.render(f5);
		back2.render(f5);
		back3.render(f5);
		wingL.render(f5);
		wingR.render(f5);

		double d = 0.25;
		GL11.glTranslated(0, d, 0);
		hornL.render(f5);
		hornR.render(f5);
		GL11.glTranslated(0, -d, 0);
	}

	@Override
	public void setPartAngles(EntityPlayer ep, float tick) {
		float pitch = -ep.rotationPitch;
		float yawHead = -ep.rotationYaw%360-tick*(ep.rotationYaw-ep.prevRotationYaw);
		float yaw = -ep.renderYawOffset%360-tick*(ep.renderYawOffset-ep.prevRenderYawOffset)+180;

		float pc = pitch*RADIAN;
		float yc = yaw*RADIAN;
		float yhc = yawHead*RADIAN;
		hornL.rotateAngleX = pc;
		//hornR.rotateAngleY = yawBody / (180F / (float)Math.PI);
		hornR.rotateAngleX = pc;


		hornR.rotateAngleY = yhc;
		hornL.rotateAngleY = yhc;

		tail.rotateAngleY = yc;
		tail3.rotateAngleY = yc;
		tail2.rotateAngleY = yc;
		back.rotateAngleY = yc;
		back2.rotateAngleY = yc;
		back3.rotateAngleY = yc;
		wingL.rotateAngleY = this.getWingAngle()+yc;
		wingR.rotateAngleY = -this.getWingAngle()+yc;

		//this.init();
	}

	@Override
	public void init() {
		//Head code:
		//bipedHead = new ModelRenderer(this, 0, 0);
		//bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, par1);
		//bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);

		hornR = new ModelRenderer(this, 32, 12);
		hornR.addBox(this.getHornX()-2F, this.getHornY(), this.getHornZ(), 2, 1, 3);
		hornR.setTextureSize(64, 32);
		hornR.mirror = true;

		hornL = new ModelRenderer(this, 32, 12);
		hornL.addBox(this.getHornX()+2F, this.getHornY(), this.getHornZ(), 2, 1, 3);
		hornL.setTextureSize(64, 32);
		hornL.mirror = true;


		tail = new ModelRenderer(this, 32, 0);
		tail.addBox(-1.5F, 10F, 2F, 3, 5, 3);
		tail.setTextureSize(64, 32);
		tail.mirror = true;

		tail2 = new ModelRenderer(this, 32, 0);
		tail2.addBox(-1.5F, 15.8F, -3.4F, 3, 5, 3);
		tail2.setTextureSize(64, 32);
		tail2.mirror = true;

		tail3 = new ModelRenderer(this, 32, 0);
		tail3.addBox(-1.5F, 16.6F, -15.5F, 3, 5, 3);
		tail3.setTextureSize(64, 32);
		tail3.mirror = true;


		back = new ModelRenderer(this, 32, 8);
		back.addBox(-0.5F, 7F, 2F, 1, 2, 1);
		back.setTextureSize(64, 32);
		back.mirror = true;

		back2 = new ModelRenderer(this, 32, 8);
		back2.addBox(-0.5F, 1F, 2F, 1, 2, 1);
		back2.setTextureSize(64, 32);
		back2.mirror = true;

		back3 = new ModelRenderer(this, 32, 8);
		back3.addBox(-0.5F, 4F, 2F, 1, 2, 1);
		back3.setTextureSize(64, 32);
		back3.mirror = true;


		wingL = new ModelRenderer(this, 44, 0);
		wingL.addBox(0F, 1F, 3F, 1, 12, 7);
		wingL.setTextureSize(64, 32);
		wingL.mirror = true;

		wingR = new ModelRenderer(this, 44, 0);
		wingR.addBox(-1F, 1F, 3F, 1, 12, 7);
		wingR.setTextureSize(64, 32);
		wingR.mirror = true;

		this.setPositions();
	}

	@Override
	public void setPositions() {
		hornR.setRotationPoint(0, 0, 0);
		this.setRotation(hornR, 0F, 0F, 0F);

		hornL.setRotationPoint(0, 0, 0);
		this.setRotation(hornL, 0F, 0F, 0F);


		tail.setRotationPoint(0F, 0F, 0F);
		this.setRotation(tail, 0.0698132F, 0F, 0F);

		tail2.setRotationPoint(0F, 0F, 0F);
		this.setRotation(tail2, 0.418879F, 0F, 0F);

		tail3.setRotationPoint(0F, 0F, 0F);
		this.setRotation(tail3, 1.047198F, 0F, 0F);


		back.setRotationPoint(0F, 0F, 0F);
		this.setRotation(back, 0F, 0F, 0F);

		back2.setRotationPoint(0F, 0F, 0F);
		this.setRotation(back2, 0F, 0F, 0F);

		back3.setRotationPoint(0F, 0F, 0F);
		this.setRotation(back3, 0F, 0F, 0F);


		wingL.setRotationPoint(0F, 0F, 0F);
		this.setRotation(wingL, 0F, this.getWingAngle(), 0F);

		wingR.setRotationPoint(0F, 0F, 0F);
		this.setRotation(wingR, 0F, -this.getWingAngle(), 0F);
	}

}
