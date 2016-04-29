/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Event.Client.AddParticleEvent;


public class ThrottleableEffectRenderer extends EffectRenderer {

	public final int limit;

	private final EffectRenderer original;

	private final HashMap<Class, EffectRenderer> delegates = new HashMap();
	private final HashSet<EffectRenderer> delegateSet = new HashSet();

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
		AddParticleEvent evt = new AddParticleEvent(fx);
		if (MinecraftForge.EVENT_BUS.post(evt))
			return;
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
		super.renderParticles(e, ptick);
		for (EffectRenderer eff : delegateSet) {
			eff.renderParticles(e, ptick);
		}
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
			sb.append(";\t");
			sb.append(eff.getClass());
			sb.append(eff.getStatistics());
		}
		return sb.toString();
	}

	public static ThrottleableEffectRenderer getRegisteredInstance() {
		return (ThrottleableEffectRenderer)Minecraft.getMinecraft().effectRenderer;
	}

}
