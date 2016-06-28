/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.io;

import java.awt.Color;

import net.minecraft.util.MathHelper;

public class ReikaColorAPI {

	/** Converts an RGB array into a color multiplier. Args: RGB[], bit */
	public static float RGBtoColorMultiplier(int[] RGB, int bit) {
		float color = 1F;
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;
		return color;
	}

	/** Converts a hex color code to a color multiplier. Args: Hex, bit */
	public static float HextoColorMultiplier(int hex, int bit) {
		float color = 1F;
		int[] RGB = ReikaColorAPI.HexToRGB(hex);
		if (bit < 0 || bit > 2)
			return 1F;
		color = RGB[bit]/255F;
		return color;
	}

	public static int RGBtoHex(int R, int G, int B, int A) {
		int color = (B | G << 8 | R << 16 | A << 24);
		return color;
	}

	public static int RGBtoHex(int R, int G, int B) {
		return RGBtoHex(R, G, B, 255);
	}

	public static int GStoHex(int GS) {
		return RGBtoHex(GS, GS, GS);
	}

	public static int[] HexToRGB (int hex) {
		int[] color = new int[4];
		color[0] = hex >> 16 & 0xFF;
		color[1] = hex >> 8 & 0xFF;
		color[2] = hex & 0xFF;
		color[3] = hex >> 24 & 0xFF;
		return color;
	}

	public static int getColorWithBrightnessMultiplier(int argb, float mult) {
		int alpha = ((argb >> 24) & 0xFF);
		int red = (int) (((argb >> 16) & 0xFF)*mult) & 0xFF;
		int green = (int) (((argb >> 8) & 0xFF)*mult) & 0xFF;
		int blue = (int) ((argb & 0xFF)*mult) & 0xFF;
		int color = alpha;
		color = (color << 8) + red;
		color = (color << 8) + green;
		color = (color << 8) + blue;
		return color;
	}

	private static float[] RGBtoHSB(int rgb) {
		return Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), null);
	}

	public static Color getModifiedSat(Color color, float factor) {
		return new Color(getModifiedSat(color.getRGB(), factor));
	}

	public static int getModifiedSat(int rgb, float factor) {
		float[] hsb = RGBtoHSB(rgb);
		hsb[1] *= factor;
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}

	public static Color getModifiedHue(Color color, int hue) {
		return new Color(getModifiedHue(color.getRGB(), hue));
	}

	public static int getModifiedHue(int rgb, int hue) {
		float[] hsb = RGBtoHSB(rgb);
		hsb[0] = hue/360F;
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}

	public static int getRed(int color) {
		int r = (color >> 16) & 0xFF;
		return r;
	}

	public static int getGreen(int color) {
		int g = (color >> 8) & 0xFF;
		return g;
	}

	public static int getBlue(int color) {
		int b = (color >> 0) & 0xFF;
		return b;
	}

	public static int getAlpha(int color) {
		int a = (color >> 24) & 0xFF;
		return a;
	}

	public static boolean isRGBNonZero(int color) {
		return (color & 0xffffff) != 0;
	}

	public static boolean isAlphaNonZero(int color) {
		return (color & 0xff000000) > 0;
	}

	/** If ratio is < 0.5, c2 is dominant */
	public static int mixColors(int c1, int c2, float ratio) {
		int a1 = (c1 & 0xff000000) >> 24;
		int a2 = (c2 & 0xff000000) >> 24;
		int r1 = (c1 & 0xff0000) >> 16;
		int r2 = (c2 & 0xff0000) >> 16;
		int g1 = (c1 & 0xff00) >> 8;
		int g2 = (c2 & 0xff00) >> 8;
		int b1 = (c1 & 0xff);
		int b2 = (c2 & 0xff);

		int r = (int)(r1*ratio + r2*(1-ratio));
		int g = (int)(g1*ratio + g2*(1-ratio));
		int b = (int)(b1*ratio + b2*(1-ratio));
		int a = (int)(a1*ratio + a2*(1-ratio));

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static int additiveBlend(int color) {
		int rgb = color&0xFFFFFF;
		int r = getRed(color);
		int g = getGreen(color);
		int b = getBlue(color);
		int alpha = (r+g+b)/3;
		return rgb | (alpha << 24);
	}

	public static int getPackedIntForColoredLight(int color, int lightval) {
		return getBlue(color) << 15 | getGreen(color) << 10 | getRed(color) << 5 | lightval;
	}

	public static int invertColor(int rgb) {
		return RGBtoHex(255-getRed(rgb), 255-getGreen(rgb), 255-getBlue(rgb));
	}

	public static int HexToGS(int rgb) {
		return (getRed(rgb)+getGreen(rgb)+getBlue(rgb))/3;
	}

	public static int getHue(int rgb) {
		return (int)(360*Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), null)[0]);
	}

	public static int getShiftedDelta(int color, int base, int newbase) {
		int red = getRed(color);
		int green = getGreen(color);
		int blue = getBlue(color);
		float[] hsv = Color.RGBtoHSB(red, green, blue, null);

		red = getRed(base);
		green = getGreen(base);
		blue = getBlue(base);

		float[] hsvb = Color.RGBtoHSB(red, green, blue, null);

		red = getRed(newbase);
		green = getGreen(newbase);
		blue = getBlue(newbase);

		float[] hsvn = Color.RGBtoHSB(red, green, blue, null);

		float[] ret = new float[3];
		for (int i = 0; i < 3; i++) {
			ret[i] = hsv[i]-hsvb[i]+hsvn[i];
			if (i > 0)
				ret[i] = MathHelper.clamp_float(ret[i], 0, 1);
		}
		return Color.HSBtoRGB(ret[0], ret[1], ret[2]);
	}

	public static int multiplyChannels(int c, float r, float g, float b) {
		int r2 = (int)Math.min(255, r*getRed(c));
		int g2 = (int)Math.min(255, g*getGreen(c));
		int b2 = (int)Math.min(255, b*getBlue(c));
		return RGBtoHex(r2, g2, b2);
	}
}
