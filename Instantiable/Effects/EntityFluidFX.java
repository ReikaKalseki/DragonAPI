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
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderModeFlags;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Interfaces.Entity.CustomRenderFX;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;

public class EntityFluidFX extends EntityFX implements CustomRenderFX {

	private Fluid type;

	private MotionController motionController;
	private PositionController positionController;

	private boolean colliding = false;

	private boolean renderOverLimit = false;

	private static final RenderMode renderMode = new RenderMode().setFlag(RenderModeFlags.ADDITIVE, false).setFlag(RenderModeFlags.DEPTH, true).setFlag(RenderModeFlags.LIGHT, false).setFlag(RenderModeFlags.ALPHACLIP, false);

	public EntityFluidFX(World world, double x, double y, double z, Fluid f) {
		this(world, x, y, z, 0, 0, 0, f);
	}

	public EntityFluidFX(World world, double x, double y, double z, double vx, double vy, double vz, Fluid f) {
		super(world, x, y, z, vx, vy, vz);
		particleIcon = ReikaLiquidRenderer.getFluidIconSafe(f);
		particleScale = 1F;
		type = f;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
	}

	public final EntityFluidFX setColliding() {
		noClip = false;
		colliding = true;
		return this;
	}

	public EntityFluidFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public EntityFluidFX setScale(float s) {
		particleScale = s;
		return this;
	}

	public EntityFluidFX setLife(int l) {
		particleMaxAge = l;
		return this;
	}

	public EntityFluidFX setMotionController(MotionController m) {
		motionController = m;
		return this;
	}

	public EntityFluidFX setPositionController(PositionController m) {
		positionController = m;
		return this;
	}

	public final EntityFluidFX forceIgnoreLimits() {
		renderOverLimit = true;
		return this;
	}

	@Override
	public int getBrightnessForRender(float par1)
	{
		return type.getLuminosity() > 12 ? 240 : super.getBrightnessForRender(par1);
	}

	@Override
	public int getFXLayer()
	{
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (colliding) {
			if (isCollidedVertically) {
				double v = rand.nextDouble()*0.0625;
				double vel = ReikaMathLibrary.py3d(motionX, 0, motionZ);
				motionX = motionX*v/vel;
				motionY = 0;
				motionZ = motionZ*v/vel;
				colliding = false;
				particleGravity *= 4;
			}
			if (isCollidedHorizontally) {

			}
		}

		int fadeTicks = Math.min(particleMaxAge/2, 8);
		if (particleMaxAge-particleAge <= fadeTicks) {
			particleAlpha -= 1D/fadeTicks;
		}

		if (motionController != null) {
			motionX = motionController.getMotionX(this);
			motionY = motionController.getMotionY(this);
			motionZ = motionController.getMotionZ(this);
			motionController.update(this);
		}

		if (positionController != null) {
			posX = positionController.getPositionX(this);
			posY = positionController.getPositionY(this);
			posZ = positionController.getPositionZ(this);
			positionController.update(this);
		}
	}

	public boolean rendersOverLimit() {
		return renderOverLimit;
	}

	@Override
	public RenderMode getRenderMode() {
		return renderMode;
	}

	@Override
	public TextureMode getTexture() {
		return ParticleEngine.blockTex;
	}

}
