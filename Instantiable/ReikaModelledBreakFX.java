/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ReikaModelledBreakFX extends EntityDiggingFX {

	private final String tex;
	private double texpos[] = new double[2];

	public static final double pw = 0.03125;

	private Random r = new Random();

	public ReikaModelledBreakFX(World world, double x, double y, double z, double vx, double vy, double vz, Block b, int meta, int side, RenderEngine re, String texture, double u, double v) {
		super(world, x, y, z, vx, vy, vz, b, meta, side, re);
		tex = texture;
		texpos[0] = u;
		texpos[1] = v;
	}

	@Override
	public int getFXLayer()
	{
		return 1;
	}

	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (particleAge++ >= particleMaxAge)
		{
			this.setDead();
		}

		motionY -= 0.04D * particleGravity;
		this.moveEntity(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround)
		{
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
		}
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		float f6 = particleTextureIndexX / 16.0F;
		float f7 = f6 + 0.0624375F;
		float f8 = particleTextureIndexY / 16.0F;
		float f9 = f8 + 0.0624375F;
		float f10 = 0.1F * particleScale;
		float f11 = (float)(prevPosX + (posX - prevPosX) * par2 - interpPosX);
		float f12 = (float)(prevPosY + (posY - prevPosY) * par2 - interpPosY);
		float f13 = (float)(prevPosZ + (posZ - prevPosZ) * par2 - interpPosZ);
		float f14 = 1.0F;
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		Tessellator v5 = new Tessellator();
		v5.startDrawingQuads();
		par1Tessellator.addVertexWithUV(f11 - par3 * f10 - par6 * f10, f12 - par4 * f10, f13 - par5 * f10 - par7 * f10, texpos[0], texpos[1]);
		par1Tessellator.addVertexWithUV(f11 - par3 * f10 + par6 * f10, f12 + par4 * f10, f13 - par5 * f10 + par7 * f10, texpos[0]+pw, texpos[1]);
		par1Tessellator.addVertexWithUV(f11 + par3 * f10 + par6 * f10, f12 + par4 * f10, f13 + par5 * f10 + par7 * f10, texpos[0]+pw, texpos[1]+pw);
		par1Tessellator.addVertexWithUV(f11 + par3 * f10 - par6 * f10, f12 - par4 * f10, f13 + par5 * f10 - par7 * f10, texpos[0], texpos[1]+pw);
		v5.draw();
	}

}
