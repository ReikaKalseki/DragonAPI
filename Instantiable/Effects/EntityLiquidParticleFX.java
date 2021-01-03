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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;

public class EntityLiquidParticleFX extends EntityFX {

	public EntityLiquidParticleFX(World world, double x, double y, double z, Fluid f) {
		this(world, x, y, z, 0, 0, 0, f);
	}

	public EntityLiquidParticleFX(World world, double x, double y, double z, double vx, double vy, double vz, Fluid f) {
		super(world, x, y, z, vx, vy, vz);
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		particleIcon = ReikaLiquidRenderer.getFluidIconSafe(f);
	}

	@Override
	public int getFXLayer()
	{
		return 2;
	}

	@Override
	public void renderParticle(Tessellator v5, float x, float y, float z, float a, float b, float c) {
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		BlendMode.DEFAULT.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, x, y, z, a, b, c);
		v5.draw();
		v5.startDrawingQuads();
	}

}
