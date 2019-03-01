/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXCollection {

	private final ArrayList<BasicFX> data = new ArrayList();

	public FXCollection() {

	}

	public void addEffect(double x, double y, double z, IIcon ico, int life, float size, int color) {
		this.addEffect(x, y, z, ico, life, size, color, false);
	}

	public void addEffect(double x, double y, double z, IIcon ico, int life, float size, int color, boolean rapidExpand) {
		data.add(new BasicFX(x, y, z, ico, life, size, color, rapidExpand));
	}

	public void addEffectWithVelocity(double x, double y, double z, double vx, double vy, double vz, IIcon ico, int life, float size, int color, boolean rapidExpand) {
		data.add(new MovingBasicFX(x, y, z, ico, life, size, color, rapidExpand, vx, vy, vz));
	}

	public void update() {
		Iterator<BasicFX> it = data.iterator();
		while (it.hasNext()) {
			BasicFX fx = it.next();
			if (fx.update()) {
				it.remove();
			}
		}
	}

	public void render(boolean additive) {
		if (data.isEmpty())
			return;
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		ReikaTextureHelper.bindTerrainTexture();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		ReikaRenderHelper.disableEntityLighting();
		if (additive)
			BlendMode.ADDITIVEDARK.apply();
		else
			BlendMode.DEFAULT.apply();
		Tessellator v5 = Tessellator.instance;
		v5.startDrawingQuads();
		v5.setBrightness(240);
		for (BasicFX fx : data) {
			fx.render(v5);
		}
		v5.draw();
		GL11.glPopAttrib();
	}

	private static class BasicFX {

		protected double posX;
		protected double posY;
		protected double posZ;

		private final int lifespan;
		private final int renderColor;
		private final float size;

		private final boolean rapidExpand;

		private final IIcon icon;

		private int ticks;

		private BasicFX(double x, double y, double z, IIcon ico, int life, float size, int color, boolean rapid) {
			posX = x;
			posY = y;
			posZ = z;
			lifespan = life;
			renderColor = color;
			this.size = size;
			icon = ico;
			rapidExpand = rapid;
		}

		public boolean update() {
			ticks++;
			return ticks >= lifespan;
		}

		private void render(Tessellator v5) {
			v5.setColorOpaque_I(renderColor);

			float f1 = ActiveRenderInfo.rotationX;
			float f5 = ActiveRenderInfo.rotationXZ;
			float f2 = ActiveRenderInfo.rotationZ;
			float f3 = ActiveRenderInfo.rotationYZ;
			float f4 = ActiveRenderInfo.rotationXY;

			double fs = rapidExpand ? 0.1*size*(lifespan/(ticks+1) >= 12 ? (ticks+1)*12D/lifespan : 1-(ticks+1)/(double)lifespan) : 0.1*size*Math.sin(Math.toRadians(180D*ticks/lifespan));

			float u = icon.getMinU();
			float v = icon.getMinV();
			float du = icon.getMaxU();
			float dv = icon.getMaxV();

			v5.addVertexWithUV(posX - f1 * fs - f3 * fs, posY - f5 * fs, posZ - f2 * fs - f4 * fs, du, dv);
			v5.addVertexWithUV(posX - f1 * fs + f3 * fs, posY + f5 * fs, posZ - f2 * fs + f4 * fs, du, v);
			v5.addVertexWithUV(posX + f1 * fs + f3 * fs, posY + f5 * fs, posZ + f2 * fs + f4 * fs, u, v);
			v5.addVertexWithUV(posX + f1 * fs - f3 * fs, posY - f5 * fs, posZ + f2 * fs - f4 * fs, u, dv);

		}

	}

	private static class MovingBasicFX extends BasicFX {

		private final double motionX;
		private final double motionY;
		private final double motionZ;

		private MovingBasicFX(double x, double y, double z, IIcon ico, int life, float size, int color, boolean rapid, double vx, double vy, double vz) {
			super(x, y, z, ico, life, size, color, rapid);

			motionX = vx;
			motionY = vy;
			motionZ = vz;
		}

		@Override
		public boolean update() {
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			return super.update();
		}

	}

}
