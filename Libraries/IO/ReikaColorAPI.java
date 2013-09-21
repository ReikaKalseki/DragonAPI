/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.awt.Color;

public class ReikaColorAPI {

	public static int RGBtoHex(int R, int G, int B) {
		int color = (B | G << 8 | R << 16);
		color += 0xff000000;
		return color;
	}

	public static int RGBtoHex(int GS) {
		if (GS == -1) //technical
			return 0xffD47EFF;
		int color = (GS | GS << 8 | GS << 16);
		color += 0xff000000;
		return color;
	}

	public static int[] HexToRGB (int hex) {
		int[] color = new int[3];
		color[0] = Color.decode(String.valueOf(hex)).getRed();
		color[1] = Color.decode(String.valueOf(hex)).getGreen();
		color[2] = Color.decode(String.valueOf(hex)).getBlue();
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
}
