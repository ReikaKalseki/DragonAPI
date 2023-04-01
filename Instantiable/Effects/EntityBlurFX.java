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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderModeFlags;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;
import Reika.DragonAPI.Interfaces.ColorController;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Interfaces.PositionController;
import Reika.DragonAPI.Interfaces.Entity.CustomRenderFX;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBlurFX extends EntityFX implements CustomRenderFX {

	private float scale;

	private boolean noSlow = false;
	private boolean rapidExpand = false;
	private boolean alphaFade = false;

	private AxisAlignedBB bounds = null;
	private int bounceAction = 0;
	private double collideAngle;
	private boolean colliding = false;
	private int clearOnCollide = -1;

	private int lifeFreeze;

	private int preColor = -1;
	private int fadeColor = -1;

	private float defaultRed;
	private float defaultGreen;
	private float defaultBlue;

	private double accelerationX;
	private double accelerationY;
	private double accelerationZ;

	private double drag;

	private Coordinate destination;

	private EntityFX lock;
	private Collection<EntityFX> locks = new HashSet();

	private boolean additiveBlend = true;
	private boolean depthTest = true;
	private boolean alphaTest = false;

	private boolean renderOverLimit = false;

	private RenderMode renderMode;

	private MotionController motionController;
	private PositionController positionController;
	private ColorController colorController;

	public EntityBlurFX(World world, double x, double y, double z, IIcon ico) {
		this(world, x, y, z, 0, 0, 0, ico);
	}

	public EntityBlurFX(World world, double x, double y, double z, double vx, double vy, double vz, IIcon ico) {
		super(world, x, y, z, vx, vy, vz);
		particleGravity = 0;
		noClip = true;
		particleMaxAge = 60;
		motionX = vx;
		motionY = vy;
		motionZ = vz;
		scale = 1F;
		alphaTest = false;
		additiveBlend = true;
		particleIcon = ico;
	}

	public final EntityBlurFX setIcon(IIcon ii) {
		particleIcon = ii;
		return this;
	}

	public final EntityBlurFX setScale(float f) {
		scale = f;
		return this;
	}

	public final EntityBlurFX setLife(int time) {
		particleMaxAge = time;
		return this;
	}

	public final EntityBlurFX setNoSlowdown() {
		noSlow = true;
		return this;
	}

	public final EntityBlurFX setRapidExpand() {
		rapidExpand = true;
		return this;
	}

	public final EntityBlurFX setAlphaFading() {
		alphaFade = true;
		return this;
	}

	public final EntityBlurFX setGravity(float g) {
		particleGravity = g;
		return this;
	}

	public final EntityBlurFX setDrag(double d) {
		drag = d;
		return this;
	}

	public final EntityBlurFX setColor(int r, int g, int b) {
		particleRed = r/255F;
		particleGreen = g/255F;
		particleBlue = b/255F;
		defaultRed = particleRed;
		defaultGreen = particleGreen;
		defaultBlue = particleBlue;
		return this;
	}

	public final EntityBlurFX setColor(int rgb) {
		return this.setColor(ReikaColorAPI.getRed(rgb), ReikaColorAPI.getGreen(rgb), ReikaColorAPI.getBlue(rgb));
	}

	public final EntityBlurFX fadeColors(int c1, int c2) {
		preColor = c1;
		fadeColor = c2;
		return this.setColor(c1);
	}

	public final EntityBlurFX bound(AxisAlignedBB box, boolean bounce, boolean cull) {
		bounds = box;
		bounceAction = (bounce ? 1 : 0) | (cull ? 2 : 0);
		return this;
	}

	public final EntityBlurFX setColliding() {
		return this.setColliding(-1);
	}

	public final EntityBlurFX setColliding(int clear) {
		return this.setColliding(rand.nextDouble()*360, clear);
	}

	public final EntityBlurFX setColliding(double ang) {
		return this.setColliding(ang, -1);
	}

	public final EntityBlurFX setColliding(double ang, int clear) {
		noClip = false;
		colliding = true;
		collideAngle = ang;
		clearOnCollide = clear;
		this.onSetColliding();
		return this;
	}

	protected void onSetColliding() {

	}

	public final EntityBlurFX markDestination(int x, int y, int z) {
		destination = new Coordinate(x, y, z);
		return this;
	}

	public final EntityBlurFX lockTo(EntityFX fx) {
		lock = fx;
		if (this == fx) {
			DragonAPICore.logError("Cannot lock a particle to itself!");
			return this;
		}
		if (fx instanceof EntityBlurFX) {
			EntityBlurFX bfx = (EntityBlurFX)fx;
			if (!bfx.getRenderMode().equals(this.getRenderMode()))
				DragonAPICore.logError("Cannot accurately lock two different particle render types: "+fx+" & "+this);
			bfx.locks.add(this);
		}
		return this;
	}

	public final EntityBlurFX setAcceleration(double x, double y, double z) {
		accelerationX = x;
		accelerationY = y;
		accelerationZ = z;
		return this;
	}

	public final EntityBlurFX setAdditiveBlend() {
		additiveBlend = true;
		renderMode = null;
		return this;
	}

	public final EntityBlurFX setBasicBlend() {
		additiveBlend = false;
		renderMode = null;
		return this;
	}

	public final EntityBlurFX setNoDepthTest() {
		depthTest = false;
		renderMode = null;
		return this;
	}

	public final EntityBlurFX enableAlphaTest() {
		alphaTest = true;
		renderMode = null;
		return this;
	}

	public final EntityBlurFX forceIgnoreLimits() {
		renderOverLimit = true;
		return this;
	}

	public final EntityBlurFX setAge(int age) {
		particleAge = age;
		return this;
	}

	public final EntityBlurFX freezeLife(int ticks) {
		lifeFreeze = ticks;
		return this;
	}

	public final EntityBlurFX setMotionController(MotionController m) {
		motionController = m;
		return this;
	}

	public final EntityBlurFX setPositionController(PositionController m) {
		positionController = m;
		return this;
	}

	public final EntityBlurFX setColorController(ColorController m) {
		colorController = m;
		return this;
	}

	protected final boolean isAlphaFade() {
		return alphaFade;
	}

	public final int getMaxAge() {
		return particleMaxAge;
	}

	public final int getMaximumSizeAge() {
		return rapidExpand ? particleMaxAge/12 : particleMaxAge/2;
	}

	@Override
	public void onUpdate() {
		ticksExisted = particleAge;
		if (particleAge < 0) {
			return;
		}
		if (colliding) {
			if (isCollidedVertically) {
				double v = rand.nextDouble()*0.0625;
				if (Double.isFinite(collideAngle)) {
					motionX = v*Math.sin(Math.toRadians(collideAngle));
					motionZ = v*Math.cos(Math.toRadians(collideAngle));
				}
				else {
					double vel = ReikaMathLibrary.py3d(motionX, 0, motionZ);
					motionX = motionX*v/vel;
					motionZ = motionZ*v/vel;
				}
				colliding = false;
				this.setNoSlowdown();
				if (clearOnCollide != Integer.MIN_VALUE) {
					lifeFreeze = clearOnCollide >= 0 ? Math.min(clearOnCollide, 20) : 20;
				}
				particleGravity *= 4;
				if (clearOnCollide >= 0)
					this.setLife(Math.max(1, clearOnCollide-lifeFreeze));
				this.onCollision();
			}
			if (isCollidedHorizontally) {

			}
		}

		if (destination != null) {
			Coordinate c = new Coordinate(this);
			if (c.equals(destination)) {
				this.setDead();
			}
		}

		motionX += accelerationX;
		motionY += accelerationY;
		motionZ += accelerationZ;

		if (noSlow) {
			double mx = motionX;
			double my = motionY;
			double mz = motionZ;
			super.onUpdate();
			motionX = mx;
			motionY = my;
			motionZ = mz;
		}
		else {
			if (drag != 0) {
				motionX *= drag;
				motionY *= drag;
				motionZ *= drag;
			}
			super.onUpdate();
		}

		if (lifeFreeze > 0) {
			lifeFreeze--;
			particleAge--;
		}

		int age = Math.max(particleAge, 1);

		if (fadeColor != -1) {
			int c = ReikaColorAPI.mixColors(fadeColor, preColor, age/(float)particleMaxAge);
			this.setColor(c);
		}

		if (alphaFade) {
			particleScale = scale;
			float f = 1;
			if (rapidExpand) {
				f = (particleMaxAge/age >= 12 ? age*12F/particleMaxAge : 1-age/(float)particleMaxAge);
			}
			else {
				f = MathHelper.sin((float)Math.toRadians(180D*age/particleMaxAge));
			}
			if (additiveBlend) {
				particleRed = defaultRed*f;
				particleGreen = defaultGreen*f;
				particleBlue = defaultBlue*f;
			}
			else {
				particleAlpha = f;
			}
		}
		else {
			if (rapidExpand)
				particleScale = scale*(particleMaxAge/age >= 12 ? age*12F/particleMaxAge : 1-age/(float)particleMaxAge);
			else
				particleScale = scale*MathHelper.sin((float)Math.toRadians(180D*age/particleMaxAge));
			//if (particleAge < 10)
			//	particleScale = scale*(particleAge+1)/10F;
			//else if (particleAge > 50)
			//	particleScale = scale*(61-particleAge)/10F;
			//else
			//	particleScale = scale;
		}

		if (bounds != null) {
			boolean bounce = (bounceAction & 1) != 0;
			boolean cull = (bounceAction & 2) != 0;
			if ((posX <= bounds.minX && motionX < 0) || (posX >= bounds.maxX && motionX > 0)) {
				motionX = bounce ? -motionX : 0;
				if (cull)
					this.setDead();
			}
			if ((posY <= bounds.minY && motionY < 0) || (posY >= bounds.maxY && motionY > 0)) {
				motionY = bounce ? -motionY : 0;
				if (cull)
					this.setDead();
			}
			if ((posZ <= bounds.minZ && motionZ < 0) || (posZ >= bounds.maxZ && motionZ > 0)) {
				motionZ = bounce ? -motionZ : 0;
				if (cull)
					this.setDead();
			}
		}

		if (lock != null) {
			posX = lock.posX;
			posY = lock.posY;
			posZ = lock.posZ;
			motionX = lock.motionX;
			motionY = lock.motionY;
			motionZ = lock.motionZ;
		}

		if (!locks.isEmpty()) {
			for (EntityFX fx : locks) {
				//fx.posX = posX;
				//fx.posY = posY;
				//fx.posZ = posZ;
				fx.motionX = motionX;
				fx.motionY = motionY;
				fx.motionZ = motionZ;
			}
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
			if (positionController != motionController) //prevent double update
				positionController.update(this);
		}

		if (colorController != null) {
			int rgb = colorController.getColor(this);
			float f = 1;
			if (alphaFade) {
				if (rapidExpand) {
					f = (particleMaxAge/age >= 12 ? age*12F/particleMaxAge : 1-age/(float)particleMaxAge);
				}
				else {
					f = MathHelper.sin((float)Math.toRadians(180D*age/particleMaxAge));
				}
			}
			particleRed = ReikaColorAPI.getRed(rgb)*f/255F;
			particleGreen = ReikaColorAPI.getGreen(rgb)*f/255F;
			particleBlue = ReikaColorAPI.getBlue(rgb)*f/255F;
			colorController.update(this);
		}
	}

	@Override
	public void moveEntity(double vx, double vy, double vz) {
		if (noClip) {
			super.moveEntity(vx, vy, vz);
		}
		else { //streamlined, removed everything that was never applicable or desirable for a particle
			ySize *= 0.4F;

			double vxCopy = vx;
			double vyCopy = vy;
			double vzCopy = vz;

			List<AxisAlignedBB> list = worldObj.getCollidingBoundingBoxes(this, boundingBox.addCoord(vx, vy, vz));

			if (!list.isEmpty()) {
				for (AxisAlignedBB box : list) {
					vx = box.calculateXOffset(boundingBox, vx);
					vy = box.calculateYOffset(boundingBox, vy);
					vz = box.calculateZOffset(boundingBox, vz);
				}
			}

			boundingBox.offset(vx, vy, vz);

			posX = (boundingBox.minX + boundingBox.maxX) * 0.5;
			posY = boundingBox.minY + yOffset - ySize;
			posZ = (boundingBox.minZ + boundingBox.maxZ) * 0.5;
			isCollidedHorizontally = vxCopy != vx || vzCopy != vz;
			isCollidedVertically = vyCopy != vy;
			onGround = vyCopy != vy && vyCopy < 0.0D;
			isCollided = isCollidedHorizontally || isCollidedVertically;

			if (isCollidedHorizontally) {
				motionX = 0.0D;
				motionZ = 0.0D;
			}

			if (isCollidedVertically) {
				motionY = 0.0D;
			}
		}
	}

	protected void onCollision() {

	}
	/*
	@Override
	public void renderParticle(Tessellator v5, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		v5.draw();
		ReikaTextureHelper.bindTerrainTexture();
		if (additiveBlend)
			BlendMode.ADDITIVEDARK.apply();
		GL11.glColor4f(1, 1, 1, 1);
		v5.startDrawingQuads();
		v5.setBrightness(this.getBrightnessForRender(0));
		super.renderParticle(v5, par2, par3, par4, par5, par6, par7);
		v5.draw();
		BlendMode.DEFAULT.apply();
		v5.startDrawingQuads();
	}
	 */

	@Override
	public final int getBrightnessForRender(float par1) {
		return 240;
	}

	@Override
	public final int getFXLayer() {
		return 2;
	}

	@Override
	public final RenderMode getRenderMode() {
		if (renderMode == null)
			renderMode = new RenderMode().setFlag(RenderModeFlags.FOG, false).setFlag(RenderModeFlags.ADDITIVE, additiveBlend).setFlag(RenderModeFlags.DEPTH, depthTest).setFlag(RenderModeFlags.LIGHT, false).setFlag(RenderModeFlags.ALPHACLIP, alphaTest && additiveBlend);//additiveBlend ? RenderMode.ADDITIVEDARK : RenderMode.LIT;
		return renderMode;
	}

	@Override
	public final TextureMode getTexture() {
		return ParticleEngine.blockTex;
	}

	public boolean rendersOverLimit() {
		return renderOverLimit;
	}
	/*
	@Override
	public double getRenderRange() {
		return scale*96;
	}
	 */
}
