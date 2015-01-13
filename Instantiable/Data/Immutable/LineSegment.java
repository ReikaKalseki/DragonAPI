/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public final class LineSegment {

	public final int originX;
	public final int originY;
	public final int originZ;

	public final ForgeDirection direction;

	public final int length;

	public LineSegment(int x, int y, int z, ForgeDirection dir, int len) {
		originX = x;
		originY = y;
		originZ = z;
		direction = dir;
		length = len;
	}

	public static final LineSegment getFromDXYZ(int x1, int x2, int y1, int y2, int z1, int z2) {
		ForgeDirection dir = ReikaDirectionHelper.getDirectionBetween(x1, y1, z1, x2, y2, z2);
		if (dir == null) {
			ReikaJavaLibrary.pConsole("Invalid coordinates!");
			return null;
		}
		int len = Math.abs(x2-x1+y2-y1+z2-z1);
		return new LineSegment(x1, y1, z1, dir, len);
	}

	@Override
	public String toString() {
		return String.format("%d, %d, %d >> %d %s", originX, originY, originZ, length, direction.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LineSegment) {
			LineSegment ls = (LineSegment)o;
			return this.matchOrigin(ls) && ls.direction == direction && ls.length == length;
		}
		return false;
	}

	public boolean matchOrigin(LineSegment ls) {
		return ls.originX == originX && ls.originY == originY && ls.originZ == originZ;
	}

	@Override
	public int hashCode() {
		return originX+originY+originZ+(direction.ordinal() << 24)+(length << 16);
	}

}
