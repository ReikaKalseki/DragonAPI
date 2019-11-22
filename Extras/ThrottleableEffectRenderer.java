/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class ThrottleableEffectRenderer extends EffectRenderer {

	public static boolean renderParticles = true;

	public final int limit;

	private final EffectRenderer original;

	private final HashMap<Class, EffectRenderer> delegates = new HashMap();
	private final HashSet<EffectRenderer> delegateSet = new HashSet();

	private boolean isRendering;
	@Deprecated
	private boolean isTicking;

	private ArrayList<ParticleSpawnHandler> particleSpawnHandlers = null;

	private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
	private static AxisAlignedBB particleBox = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

	public ThrottleableEffectRenderer(EffectRenderer eff) {
		super(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().renderEngine);
		limit = Math.max(250, DragonOptions.PARTICLELIMIT.getValue());
		original = eff;
	}

	public void registerDelegateRenderer(Class fxClass, EffectRenderer renderer) {
		delegates.put(fxClass, renderer);
		delegateSet.add(renderer);
	}

	public void addSpawnHandler(ParticleSpawnHandler p) {
		if (particleSpawnHandlers == null) {
			particleSpawnHandlers = new ArrayList();
		}
		particleSpawnHandlers.add(p);
	}

	@Override
	public void addEffect(EntityFX fx) {
		if (fx == null)
			throw new IllegalArgumentException("You cannot spawn a null particle! This is a bug in the mod calling this code!");
		//AddParticleEvent evt = AddParticleEvent.getForParticle(fx);
		//if (MinecraftForge.EVENT_BUS.post(evt))
		//	return;
		//if (this.isInWall(fx))
		//	return;
		if (particleSpawnHandlers != null) {
			for (ParticleSpawnHandler p : particleSpawnHandlers) {
				if (p.cancel(fx)) {
					return;
				}
			}
		}
		EffectRenderer eff = delegates.get(fx.getClass());
		if (eff != null) {
			eff.addEffect(fx);
			return;
		}

		if (isRendering) {
			DragonAPICore.logError("Tried adding a particle mid-render!");
			Thread.dumpStack();
			return;
		}
		/*
		if (isTicking) {
			DragonAPICore.logError("Tried adding a particle mid-update!");
			Thread.dumpStack();
			return;
		}*/

		int i = fx.getFXLayer();
		if (fxLayers[i].size() >= limit) {
			fxLayers[i].remove(0);
		}

		fxLayers[i].add(fx);
	}

	private boolean isInWall(EntityFX fx) {
		int x = MathHelper.floor_double(fx.posX);
		int y = MathHelper.floor_double(fx.posY);
		int z = MathHelper.floor_double(fx.posZ);
		Block b = fx.worldObj.getBlock(x, y, z);
		if (b.isOpaqueCube() && b.renderAsNormalBlock() && b.getRenderType() == 0) {
			double d = 0.4;
			double minX = x+b.getBlockBoundsMinX()+d;
			double minY = y+b.getBlockBoundsMinY()+d;
			double minZ = z+b.getBlockBoundsMinZ()+d;
			double maxX = x+b.getBlockBoundsMaxX()-d;
			double maxY = y+b.getBlockBoundsMaxY()-d;
			double maxZ = z+b.getBlockBoundsMaxZ()-d;
			if (ReikaMathLibrary.isValueInsideBounds(minX, maxX, fx.posX) && ReikaMathLibrary.isValueInsideBounds(minY, maxY, fx.posY) && ReikaMathLibrary.isValueInsideBounds(minZ, maxZ, fx.posZ)) {
				//DragonAPICore.log("Skipping particle "+fx+"; inside block");
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateEffects() {
		//isTicking = true;
		super.updateEffects();
		//isTicking = false;
		for (EffectRenderer eff : delegateSet) {
			eff.updateEffects();
		}
	}

	@Override
	public void renderParticles(Entity e, float ptick) {
		if (renderParticles) {
			if (renderThroughWalls())
				GL11.glDisable(GL11.GL_DEPTH_TEST);
			//super.renderParticles(e, ptick);
			this.doRenderParticles(e, ptick);
			for (EffectRenderer eff : delegateSet) {
				eff.renderParticles(e, ptick);
			}
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	private void doRenderParticles(Entity e, float ptick) {
		float f1 = ActiveRenderInfo.rotationX;
		float f2 = ActiveRenderInfo.rotationZ;
		float f3 = ActiveRenderInfo.rotationYZ;
		float f4 = ActiveRenderInfo.rotationXY;
		float f5 = ActiveRenderInfo.rotationXZ;
		EntityFX.interpPosX = e.lastTickPosX + (e.posX - e.lastTickPosX) * ptick;
		EntityFX.interpPosY = e.lastTickPosY + (e.posY - e.lastTickPosY) * ptick;
		EntityFX.interpPosZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * ptick;

		isRendering = true;

		for (int i = 0; i < 3; i++)  {
			if (!fxLayers[i].isEmpty())  {
				this.bindTexture(i);

				GL11.glColor4f(1F, 1F, 1F, 1F);
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
				Tessellator v5 = Tessellator.instance;
				v5.startDrawingQuads();

				for (EntityFX fx : ((Collection<EntityFX>)fxLayers[i]))  {
					if (fx != null && isParticleVisible(fx)) {
						//if (isEntityCloseEnough(fx, EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ)) {
						v5.setBrightness(fx.getBrightnessForRender(ptick));

						boolean draw = v5.isDrawing;
						try {
							fx.renderParticle(v5, ptick, f1, f5, f2, f3, f4);
						}
						catch (Throwable throwable) {
							this.throwCrash(i, fx, throwable);
						}
						if (v5.isDrawing != draw) {
							DragonAPICore.logError("Particle "+fx+" left the tessellator in a bad state!");
							if (draw) {
								v5.startDrawingQuads();
							}
							else {
								v5.draw();
							}
						}
						//}
					}
				}

				v5.draw();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}

		isRendering = false;
	}

	private void bindTexture(int i) {
		switch (i) {
			case 0:
			default:
				renderer.bindTexture(particleTextures);
				break;
			case 1:
				renderer.bindTexture(TextureMap.locationBlocksTexture);
				break;
			case 2:
				renderer.bindTexture(TextureMap.locationItemsTexture);
		}
	}

	private void throwCrash(final int i, final EntityFX fx, Throwable throwable) {
		CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
		CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
		crashreportcategory.addCrashSectionCallable("Particle", new Callable() {
			private static final String __OBFID = "CL_00000918";
			public String call() {
				return fx.toString();
			}
		});
		crashreportcategory.addCrashSectionCallable("Particle Type", new Callable() {
			private static final String __OBFID = "CL_00000919";
			public String call() {
				return i == 0 ? "MISC_TEXTURE" : (i == 1 ? "TERRAIN_TEXTURE" : (i == 2 ? "ITEM_TEXTURE" : (i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i)));
			}
		});
		throw new ReportedException(crashreport);
	}

	@Override
	public void renderLitParticles(Entity e, float ptick) {
		super.renderLitParticles(e, ptick);
		for (EffectRenderer eff : delegateSet) {
			eff.renderLitParticles(e, ptick);
		}
	}

	@Override
	public void clearEffects(World world) {
		super.clearEffects(world);
		for (EffectRenderer eff : delegateSet) {
			eff.clearEffects(world);
		}
	}

	@Override
	public String getStatistics() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getStatistics());
		for (EffectRenderer eff : delegateSet) {
			sb.append("; ");
			//sb.append(eff.getClass());
			sb.append(eff.getStatistics());
		}
		return sb.toString();
	}

	public int getParticleCount() {
		int base = 0;
		for (Collection<EntityFX> fx : fxLayers) {
			base += fx.size();
		}
		for (EffectRenderer eff : delegateSet) {
			if (eff instanceof CustomEffectRenderer)
				base += ((CustomEffectRenderer)eff).getParticleCount();
		}
		return base;
	}

	public static ThrottleableEffectRenderer getRegisteredInstance() {
		return (ThrottleableEffectRenderer)Minecraft.getMinecraft().effectRenderer;
	}

	public static boolean renderThroughWalls() {
		return Keyboard.isKeyDown(Keyboard.KEY_INSERT);
	}

	public static boolean isParticleVisible(EntityFX fx) {
		return ReikaRenderHelper.renderFrustrum.isBoundingBoxInFrustum(getBoundingBox(fx));
	}

	public static AxisAlignedBB getBoundingBox(EntityFX fx) {
		return particleBox.setBounds(fx.posX-fx.particleScale, fx.posY-fx.particleScale, fx.posZ-fx.particleScale, fx.posX+fx.particleScale, fx.posY+fx.particleScale, fx.posZ+fx.particleScale);
	}

	public static interface CustomEffectRenderer {

		public int getParticleCount();

	}

	public static interface ParticleSpawnHandler {

		public boolean cancel(EntityFX fx);

	}

	/*
	public static boolean isEntityCloseEnough(EntityFX fx, double x, double y, double z) {
		if (fx instanceof CustomRenderFX) {
			double dx = fx.posX-x;
			double dy = fx.posY-y;
			double dz = fx.posZ-z;
			return ((CustomRenderFX)fx).getRenderRange()*30 >= dx*dx+dy*dy+dz*dz;
		}
		return true;
	}*/

}
