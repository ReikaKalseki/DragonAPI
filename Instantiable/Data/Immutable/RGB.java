/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public final class RGB {

	public final int red;
	public final int green;
	public final int blue;
	public final int alpha;

	public RGB(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public RGB(int r, int g, int b, int a) {
		red = Math.min(255, r);
		green = Math.min(255, g);
		blue = Math.min(255, b);
		alpha = Math.min(255, a);
	}

	/** ARGB */
	public RGB(int color) {
		this(ReikaColorAPI.getRed(color), ReikaColorAPI.getGreen(color), ReikaColorAPI.getBlue(color), ReikaColorAPI.getAlpha(color));
	}

	public int getInt() {
		return ReikaColorAPI.RGBtoHex(red, green, blue, alpha);
	}

	public double getDistance(RGB rgb) {
		return ReikaMathLibrary.py3d(red-rgb.red, green-rgb.green, blue-rgb.blue);
	}

	public RGB permute(int dr, int dg, int db, int da) {
		return new RGB(red+dr, green+dg, blue+db, alpha+da);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RGB) {
			RGB c = (RGB)o;
			return c.red == red && c.green == green && c.blue == blue && c.alpha == alpha;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getInt();
	}

}
