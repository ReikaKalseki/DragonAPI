/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.awt.Color;

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
		int red = (int) (((argb >> 16) & 0xFF)*mult);
		int green = (int) (((argb >> 8) & 0xFF)*mult);
		int blue = (int) ((argb & 0xFF)*mult);
		int color = alpha;
		color = (color << 8) + red;
		color = (color << 8) + green;
		color = (color << 8) + blue;
		return color;
	}

	public static Color getHigherSat(Color color) {
		int r = (color.getRed());
		int g = (color.getGreen());
		int b = (color.getBlue());
		int a = color.getAlpha();
		if (r > g && r > b)
			r *= 1.2;
		if (g > r && g > b)
			g *= 1.2;
		if (b > g && b > r)
			b *= 1.2;
		if (r > 255)
			r = 255;
		if (g > 255)
			g = 255;
		if (b > 255)
			b = 255;
		Color color2 = new Color(r, g, b, a);
		return color2;
	}

	public static Color getLowerSat(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int a = color.getAlpha();
		Color color2 = new Color(r, g, b, a);
		return color2;
	}

	public static int getRedFromInteger(int color) {
		int r = (color >> 16) & 0xFF;
		return r;
	}

	public static int getGreenFromInteger(int color) {
		int g = (color >> 8) & 0xFF;
		return g;
	}

	public static int getBlueFromInteger(int color) {
		int b = (color >> 0) & 0xFF;
		return b;
	}

	public static int mixColors(int c1, int c2, float ratio) {
		int r1 = (c1 & 0xff0000) >> 16;
		int r2 = (c2 & 0xff0000) >> 16;
		int g1 = (c1 & 0xff00) >> 8;
		int g2 = (c2 & 0xff00) >> 8;
		int b1 = (c1 & 0xff);
		int b2 = (c2 & 0xff);

		int r = (int)(r1*ratio + r2*(1-ratio));
		int g = (int)(g1*ratio + g2*(1-ratio));
		int b = (int)(b1*ratio + b2*(1-ratio));
		return r << 16 | g << 8 | b;
	}
}
