/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.rendering;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import reika.dragonapi.libraries.io.ReikaRenderHelper;
import reika.dragonapi.libraries.io.ReikaTextureHelper;
import reika.dragonapi.libraries.java.ReikaGLHelper.BlendMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXCollection {

	private final ArrayList<BasicFX> data = new ArrayList();

	public FXCollection() {

	}

	public void addEffect(double x, double y, double z, IIcon ico, int life, float size, int color) {
		data.add(new BasicFX(x, y, z, ico, life, size, color));
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

		private final double posX;
		private final double posY;
		private final double posZ;

		private final int lifespan;
		private final int renderColor;
		private final float size;

		private final IIcon icon;

		private int ticks;

		private BasicFX(double x, double y, double z, IIcon ico, int life, float size, int color) {
			posX = x;
			posY = y;
			posZ = z;
			lifespan = life;
			renderColor = color;
			this.size = size;
			icon = ico;
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

			double fs = 0.1*size*Math.sin(Math.toRadians(180D*ticks/lifespan));

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

}
