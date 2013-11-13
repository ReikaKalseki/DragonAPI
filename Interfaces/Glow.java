/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

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
	public void render(double par2, double par4, double par6) {
		ReikaRenderHelper.prepareGeoDraw(alpha < 255);
		GL11.glTranslated(par2, par4, par6);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);



		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(-par2, -par4, -par6);
		ReikaRenderHelper.exitGeoDraw();
	}

}
