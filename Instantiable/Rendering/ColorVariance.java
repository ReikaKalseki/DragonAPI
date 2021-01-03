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

import java.awt.Color;
import java.util.Random;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;


public class ColorVariance {

	private static final Random rand = new Random();

	public final int rootColor;

	public final float hueRoot;
	public final float satRoot;
	public final float lumRoot;

	public final float hueVariation;
	public final float satVariation;
	public final float lumVariation;

	public int alphaRoot = 255;
	public int alphaVariation = 0;

	public float whiteVariation = 0;

	public ColorVariance(int c, float var) {
		this(c, var, var, var);
	}

	public ColorVariance(int c, float hue, float sat, float lum) {
		rootColor = c;

		float[] hsb = Color.RGBtoHSB(ReikaColorAPI.getRed(c), ReikaColorAPI.getGreen(c), ReikaColorAPI.getBlue(c), null);
		hueRoot = hsb[0];
		satRoot = hsb[1];
		lumRoot = hsb[2];

		if (hue > 1) {
			hue /= 360F;
		}
		if (sat > 1) {
			sat /= 256F;
		}
		if (lum > 1) {
			lum /= 256F;
		}

		hueVariation = hue;
		satVariation = sat;
		lumVariation = lum;
	}

	public int[] generateColors(int n) {
		int[] ret = new int[n];
		for (int i = 0; i < n; i++) {
			int c = rootColor;
			float h = this.getHue();
			float s = this.getSat();
			float b = this.getLum();
			int a = this.getAlpha();
			int c2 = Color.HSBtoRGB(h, s, b);

			if (whiteVariation > 0) {
				c2 = ReikaColorAPI.mixColors(0xffffff, c2, rand.nextFloat()*whiteVariation);
			}

			c2 = (c2 & 0xFFFFFF) | (a << 24);

			ret[i] = c2;
		}
		return ret;
	}

	private float getHue() {
		return hueVariation > 0 ? Math.min(1F, (float)ReikaRandomHelper.getRandomPlusMinus(hueRoot, hueVariation)) : hueRoot;
	}

	private float getSat() {
		return satVariation > 0 ? Math.min(1F, (float)ReikaRandomHelper.getRandomPlusMinus(satRoot, satVariation)) : satRoot;
	}

	private float getLum() {
		return lumVariation > 0 ? Math.min(1F, (float)ReikaRandomHelper.getRandomPlusMinus(lumRoot, lumVariation)) : lumRoot;
	}

	private int getAlpha() {
		return alphaVariation > 0 ? Math.min(255, ReikaRandomHelper.getRandomPlusMinus(alphaRoot, alphaVariation)) : alphaRoot;
	}

	public ColorBlendList getBlends(int n, float speed) {
		int[] dat = this.generateColors(n);

		ColorBlendList cbl = new ColorBlendList(speed);

		for (int i = 0; i < n; i++) {
			//int pre = dat[i];
			//int post = i < n-1 ? dat[i+1] : dat[0];
			cbl.addAll(dat[i]);
		}

		return cbl;
	}

	@Override
	public String toString() {
		return String.format("%s = {%d,%.1f,%.1f,%.1f} > [%d,%.1f,%.1f,%.1f] / %.3f", Integer.toHexString(rootColor), alphaRoot, hueRoot*360, satRoot*256, lumRoot*256, alphaVariation, hueVariation*360, satVariation*256, lumVariation*256, whiteVariation);
	}

}
