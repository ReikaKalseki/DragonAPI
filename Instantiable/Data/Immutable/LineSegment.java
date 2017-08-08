/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;


public final class LineSegment {

	public final Coordinate origin;
	public final Coordinate target;

	public LineSegment(int x, int y, int z, int x2, int y2, int z2) {
		origin = new Coordinate(x, y, z);
		target = new Coordinate(x2, y2, z2);
	}

	public LineSegment(Coordinate c1, Coordinate c2) {
		this(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
	}

	public static final LineSegment getFromXYZDir(int x1, int y1, int z1, ForgeDirection dir, int len) {
		return new LineSegment(x1, y1, z1,x1+len*dir.offsetX, y1+len*dir.offsetY, z1+len*dir.offsetZ);
	}

	public static final LineSegment getFromXYZDir(int x1, int y1, int z1, CubeDirections dir, double len) {
		return new LineSegment(x1, y1, z1, MathHelper.floor_double(x1+len*dir.offsetX), y1, MathHelper.floor_double(z1+len*dir.offsetZ));
	}

	public double getLength() {
		return target.getDistanceTo(origin);
	}

	@Override
	public String toString() {
		return origin.toString()+" >> "+target.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LineSegment) {
			LineSegment ls = (LineSegment)o;
			return ls.origin.equals(origin) && ls.target.equals(target);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return origin.hashCode() ^ target.hashCode();
	}

	public DecimalLineSegment asDecimalSegment() {
		return new DecimalLineSegment(new DecimalPosition(origin), new DecimalPosition(target));
	}

}
