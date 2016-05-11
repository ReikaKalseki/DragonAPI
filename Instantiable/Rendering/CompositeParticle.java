/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/** A compilation of several EntityFX objects, intended for rendering in the TESR to reduce the number of individual particles that need to be spawned. */
@SideOnly(Side.CLIENT)
public class CompositeParticle {

	public final World world;
	public final int x;
	public final int y;
	public final int z;

	public final int frameLength;
	private int frame;

	private final FXState[] data;

	public CompositeParticle(World world, int x, int y, int z, int n, ParticleSpawner p) {
		frameLength = n;
		data = new FXState[n];

		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;

		Collection<CacheableFX> overEnd = new ArrayList();

		for (int i = 0; i < data.length; i++) {
			data[i] = new FXState();
			Collection<CacheableFX> newFX = new ArrayList();
			p.createNewFX(newFX, i);
			for (CacheableFX fx : newFX) {
				data[i].particles.add(fx);
			}
			if (i > 0) {
				for (CacheableFX fx : data[i-1].particles) {
					CacheableFX next = fx.nextFrame();
					if (next != null)
						data[i].particles.add(next);
				}
			}
			if (i == data.length-1) {
				for (CacheableFX fx : data[i].particles) {
					CacheableFX next = fx.nextFrame();
					if (next != null)
						overEnd.add(next);
				}
			}
		}

		int i = 0;
		while (!overEnd.isEmpty()) {
			Collection<CacheableFX> cp = new ArrayList(overEnd);
			overEnd.clear();
			for (CacheableFX fx : cp) {
				data[i].particles.add(fx);
				CacheableFX next = fx.nextFrame();
				if (next != null)
					overEnd.add(next);
			}
			i++;
		}
	}

	public void render(float ptick) {

		float yaw = ActiveRenderInfo.rotationX;
		float pitch = ActiveRenderInfo.rotationZ;

		float f3 = ActiveRenderInfo.rotationYZ;
		float f4 = ActiveRenderInfo.rotationXY;
		float f5 = ActiveRenderInfo.rotationXZ;
		data[frame].render(ptick, yaw, f5, pitch, f3, f4);

		frame = (frame == frameLength-1 ? 0 : frame+1);
	}

	private static class FXState {

		private final ArrayList<CacheableFX> particles = new ArrayList();

		private void render(float ptick, float yaw, float f5, float pitch, float f3, float f4) {
			for (CacheableFX fx : particles) {
				((EntityFX)fx).renderParticle(Tessellator.instance, ptick, yaw, f5, pitch, f3, f4);
			}
		}

	}

	public static interface ParticleSpawner {

		public void createNewFX(Collection<CacheableFX> li, int frame);

	}

	public static interface CacheableFX {

		/** Return null if it despawns. */
		public CacheableFX nextFrame();

	}

}
