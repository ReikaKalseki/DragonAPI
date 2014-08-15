/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Objects;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public enum ArrowCharacters {

	RIGHT(8250, 0, 44),
	UPRIGHT(8989, 45, 89),
	UP(6356, 90, 134),
	UPLEFT(8988, 135, 179),
	LEFT(8249, 180, 224),
	DOWNLEFT(8990, 225, 269),
	DOWN(9013, 270, 314),
	DOWNRIGHT(8991, 315, 359);

	private static final ArrowCharacters[] arrowList = ArrowCharacters.values();

	private char[] ch;
	private int min;
	private int max;

	private ArrowCharacters(int c, int lo, int hi) {
		ch = Character.toChars(c);
		min = lo;
		max = hi;
	}

	public static ArrowCharacters getFromAngle(double ang) {
		ang = ang%360;
		for (int i = 0; i < arrowList.length; i++) {
			if (ReikaMathLibrary.isValueInsideBoundsIncl(arrowList[i].getMinAngle(), arrowList[i].getMaxAngle(), ang)) {
				return arrowList[i];
			}
		}
		return null;
	}

	public int getMinAngle() {
		return min;
	}

	public int getMaxAngle() {
		return max;
	}

	public String getStringValue() {
		return new String(ch);
	}

}