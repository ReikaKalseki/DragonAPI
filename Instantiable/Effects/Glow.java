/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Effects;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A render effect. */
@SideOnly(Side.CLIENT)
public class Glow {

	private int red;
	private int green;
	private int blue;
	private int alpha;

	private double posX;
	private double posY;
	private double posZ;

	private double scale;

	public Glow(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Glow(int r, int g, int b, int a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}

	public Glow(int r, int g, int b, int a, double x, double y, double z) {
		this(r, g, b, a);
		this.setPosition(x, y, z);
	}

	public Glow setPosition(double x, double y, double z) {
		posX = x;
		posY = y;
		posZ = z;
		return this;
	}

	public Glow setColor(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
		return this;
	}

	public Glow setColor(int r, int g, int b, int a) {
		return this.setColor(r, g, b, 255);
	}

	public Glow setScale(double s) {
		scale = s;
		return this;
	}

	public void hueShift() {

	}

	public void brightness() {

	}

	public void move(double movX, double movY, double movZ) {
		posX += movX;
		posY += movY;
		posZ += movZ;
	}

	//(double)par1TileEntity.xCoord - staticPlayerX, (double)par1TileEntity.yCoord - staticPlayerY, (double)par1TileEntity.zCoord - staticPlayerZ, par2);
	public void render() {
		double par2 = posX-TileEntityRendererDispatcher.staticPlayerX;
		double par4 = posY-TileEntityRendererDispatcher.staticPlayerY;
		double par6 = posZ-TileEntityRendererDispatcher.staticPlayerZ;

		ReikaRenderHelper.prepareGeoDraw(alpha < 255);
		GL11.glTranslated(par2, par4, par6);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		Tessellator v5 = Tessellator.instance;
		float d = 7.5F;
		RenderManager renderManager = RenderManager.instance;
		GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScaled(scale, scale, scale);
		for (float i = 0; i < 90; i += d) {
			GL11.glRotated(i, 0, 0, 1);
			GL11.glTranslated(0, 0, 0.001);
			v5.startDrawingQuads();
			v5.setColorRGBA(red, green, blue, 26);
			v5.addVertex(-0.5, -0.5, 0);
			v5.addVertex(0.5, -0.5, 0);
			v5.addVertex(0.5, 0.5, 0);
			v5.addVertex(-0.5, 0.5, 0);
			v5.draw();
			GL11.glRotated(-i, 0, 0, 1);
		}
		GL11.glScaled(1D/scale, 1D/scale, 1D/scale);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-180.0F + renderManager.playerViewY, 0.0F, 1.0F, 0.0F);

		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(-par2, -par4, -par6);
		ReikaRenderHelper.exitGeoDraw();
	}

}
