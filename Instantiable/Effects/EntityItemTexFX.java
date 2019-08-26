/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;


public class EntityItemTexFX extends EntityFX {

	private final ItemStack item;

	public EntityItemTexFX(World world, double x, double y, double z, double vx, double vy, double vz, ItemStack is) {
		super(world, x, y, z);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		item = is.copy();
	}

	public EntityItemTexFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public EntityItemTexFX setScale(float s) {
		particleScale = s;
		return this;
	}

	public EntityItemTexFX setLife(int l) {
		particleMaxAge = l;
		return this;
	}

	@Override
	public void onUpdate() {
		double vx = motionX;
		double vy = motionY;
		double vz = motionZ;
		super.onUpdate();
		motionX = vx;
		motionY = vy;
		motionZ = vz;
	}

	public EntityItemTexFX applyRenderColor() {
		int j = item.getItem().getColorFromItemStack(item, 0);
		particleRed *= (j >> 16 & 255) / 255.0F;
		particleGreen *= (j >> 8 & 255) / 255.0F;
		particleBlue *= (j & 255) / 255.0F;
		return this;
	}

	@Override
	public int getFXLayer() {
		return 2;
	}

	@Override
	public void renderParticle(Tessellator v5, float ptick, float rx, float ry, float rz, float rw, float rh) {
		IIcon ico = item.getIconIndex();
		float f6 = ico.getMinU();//ico.getInterpolatedU(particleTextureJitterX / 4.0F * 16.0F);
		float f7 = ico.getMaxU();//ico.getInterpolatedU((particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
		float f8 = ico.getMinV();//ico.getInterpolatedV(particleTextureJitterY / 4.0F * 16.0F);
		float f9 = ico.getMaxV();//ico.getInterpolatedV((particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
		float f10 = 0.1F * particleScale;

		float f11 = (float)(prevPosX + (posX - prevPosX) * ptick - interpPosX);
		float f12 = (float)(prevPosY + (posY - prevPosY) * ptick - interpPosY);
		float f13 = (float)(prevPosZ + (posZ - prevPosZ) * ptick - interpPosZ);
		v5.setColorOpaque_F(particleRed, particleGreen, particleBlue);
		v5.addVertexWithUV(f11 - rx * f10 - rw * f10, f12 - ry * f10, f13 - rz * f10 - rh * f10, f6, f9);
		v5.addVertexWithUV(f11 - rx * f10 + rw * f10, f12 + ry * f10, f13 - rz * f10 + rh * f10, f6, f8);
		v5.addVertexWithUV(f11 + rx * f10 + rw * f10, f12 + ry * f10, f13 + rz * f10 + rh * f10, f7, f8);
		v5.addVertexWithUV(f11 + rx * f10 - rw * f10, f12 - ry * f10, f13 + rz * f10 - rh * f10, f7, f9);
	}

}
