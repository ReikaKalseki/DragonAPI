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


public final class DecimalLineSegment {

	public final DecimalPosition origin;
	public final DecimalPosition target;

	public DecimalLineSegment(double x, double y, double z, double x2, double y2, double z2) {
		origin = new DecimalPosition(x, y, z);
		target = new DecimalPosition(x2, y2, z2);
	}

	public DecimalLineSegment(DecimalPosition c1, DecimalPosition c2) {
		this(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
	}

	public static final DecimalLineSegment getFromXYZDir(double x1, double y1, double z1, ForgeDirection dir, double len) {
		return new DecimalLineSegment(x1, y1, z1,x1+len*dir.offsetX, y1+len*dir.offsetY, z1+len*dir.offsetZ);
	}

	public static final DecimalLineSegment getFromXYZDir(double x1, double y1, double z1, CubeDirections dir, double len) {
		return new DecimalLineSegment(x1, y1, z1, MathHelper.floor_double(x1+len*dir.offsetX), y1, MathHelper.floor_double(z1+len*dir.offsetZ));
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
		if (o instanceof DecimalLineSegment) {
			DecimalLineSegment ls = (DecimalLineSegment)o;
			return ls.origin.equals(origin) && ls.target.equals(target);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return origin.hashCode() ^ target.hashCode();
	}

	public DecimalPosition findIntersection(LineSegment l) {
		return this.findIntersection(l.asDecimalSegment());
	}

	public DecimalPosition findIntersection(DecimalLineSegment l) {
		//Matrix3D mat = new Matrix3D();
		//TODO Incomplete abandoned method
		return null;
	}

	public DecimalPosition findIntersection2D(DecimalLineSegment l) {
		double x1 = origin.xCoord;
		double x2 = target.xCoord;
		double x3 = l.origin.xCoord;
		double x4 = l.target.xCoord;
		double y1 = origin.zCoord;
		double y2 = target.zCoord;
		double y3 = l.origin.zCoord;
		double y4 = l.target.zCoord;
		double denom = (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4);
		if (denom == 0) //parallel
			return null;
		double px = ((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/denom;
		double pz = ((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/denom;
		if ((px < origin.xCoord && px < target.xCoord) || (px > origin.xCoord && px > target.xCoord)) //intersection is outside lines
			return null;
		if ((pz < origin.zCoord && pz < target.zCoord) || (pz > origin.zCoord && pz > target.zCoord))
			return null;
		if ((px < l.origin.xCoord && px < l.target.xCoord) || (px > l.origin.xCoord && px > l.target.xCoord))
			return null;
		if ((pz < l.origin.zCoord && pz < l.target.zCoord) || (pz > l.origin.zCoord && pz > l.target.zCoord))
			return null;
		return new DecimalPosition(px, 0, pz);
	}

}
