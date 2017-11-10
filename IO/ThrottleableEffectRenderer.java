/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

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
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;


public class ThrottleableEffectRenderer extends EffectRenderer {

	public static boolean renderParticles = true;

	public final int limit;

	private final EffectRenderer original;

	private final HashMap<Class, EffectRenderer> delegates = new HashMap();
	private final HashSet<EffectRenderer> delegateSet = new HashSet();

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

	@Override
	public void addEffect(EntityFX fx) {
		if (fx == null)
			throw new IllegalArgumentException("You cannot spawn a null particle! This is a bug in the mod calling this code!");
		//AddParticleEvent evt = AddParticleEvent.getForParticle(fx);
		//if (MinecraftForge.EVENT_BUS.post(evt))
		//	return;
		EffectRenderer eff = delegates.get(fx.getClass());
		if (eff != null) {
			eff.addEffect(fx);
			return;
		}

		int i = fx.getFXLayer();
		if (fxLayers[i].size() >= limit) {
			fxLayers[i].remove(0);
		}

		fxLayers[i].add(fx);
	}

	@Override
	public void updateEffects() {
		super.updateEffects();
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
						v5.setBrightness(fx.getBrightnessForRender(ptick));

						try {
							fx.renderParticle(v5, ptick, f1, f5, f2, f3, f4);
						}
						catch (Throwable throwable) {
							this.throwCrash(i, fx, throwable);
						}
					}
				}

				v5.draw();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}
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

	public static ThrottleableEffectRenderer getRegisteredInstance() {
		return (ThrottleableEffectRenderer)Minecraft.getMinecraft().effectRenderer;
	}

	public static boolean renderThroughWalls() {
		return Keyboard.isKeyDown(Keyboard.KEY_INSERT);
	}

	public static boolean isParticleVisible(EntityFX fx) {
		return ReikaRenderHelper.renderFrustrum.isBoundingBoxInFrustum(getBoundingBox(fx));
	}

	private static AxisAlignedBB getBoundingBox(EntityFX fx) {
		return particleBox.setBounds(fx.posX-fx.particleScale, fx.posY-fx.particleScale, fx.posZ-fx.particleScale, fx.posX+fx.particleScale, fx.posY+fx.particleScale, fx.posZ+fx.particleScale);
	}

}
