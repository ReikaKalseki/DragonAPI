package Reika.DragonAPI;

import net.minecraft.util.Vec3;

public class ReikaVectorHelper {
	
	/** Returns a standard vec3d between two specified points, rather than from the origin.
	 * Args: start x,y,z, end x,y,z */
	public static Vec3 getVec2Pt(double x1, double y1, double z1, double x2, double y2, double z2) {
		Vec3 p1 = Vec3.vec3dPool.getVecFromPool(x1, y1, z1);
		Vec3 p2 = Vec3.vec3dPool.getVecFromPool(x2, y2, z2);
		return p2.subtract(p1);
	}
	
	/** Breaks a vector into a size-3 array of its components. Args: Vector */
	public static double[] components(Vec3 vec) {
		double[] xyz = new double[3];
		xyz[0] = vec.xCoord;
		xyz[1] = vec.yCoord;
		xyz[2] = vec.zCoord;
		return xyz;
	}
	
	/** Extends two vectors to infinity and finds their intersection point. If they are
	 * parallel (and thus never cross), it returns +infinity. If they are not parallel 
	 * but still never cross - one-axis parallel and displaced - it returns
	 * -infinity. Args: Vec1, Vec2 */
	public static double[] findIntersection(Vec3 v1, Vec3 v2) {
		double[] xyz = new double[3];
		if (areParallel(v1, v2))
			return ReikaArrayHelper.fillArray(xyz, Double.POSITIVE_INFINITY);
		if (areNonParallelNonIntersecting(v1, v2))
			return ReikaArrayHelper.fillArray(xyz, Double.NEGATIVE_INFINITY);
		//TODO This code is still being written
		return xyz;
	}
	
	/** Returns the slope of a vector as da/dl, where a is the specified axis.
	 * Returns +infinity if invalid axis. Args: Vector, 0/1/2 for x/y/z*/
	public static double getSlope(Vec3 vec, int axis) {
		switch(axis) {
		case 0:
			return (vec.xCoord/vec.lengthVector());
		case 1:
			return (vec.yCoord/vec.lengthVector());
		case 2:
			return (vec.zCoord/vec.lengthVector());
		default:
			return Double.POSITIVE_INFINITY;
		}
	}
	
	/** Returns true if two vectors are parallel. Args: Vec1, Vec2 */
	public static boolean areParallel(Vec3 vec1, Vec3 vec2) {
		for (int i = 0; i < 3; i++)
			if (getSlope(vec1, i) != getSlope(vec2, i))
				return false;
		return true;
	}
	
	/** Returns true if the two vectors are not parallel but will never intersect due to
	 * Being parallel in one axis and displaced. */
	public static boolean areNonParallelNonIntersecting(Vec3 vec1, Vec3 vec2) {
		if (areParallel(vec1, vec2))
			return false;
		if (getSlope(vec1, 0) == getSlope(vec2, 0)) {
			
		}
		//TODO This code is still being written
		return false;
	}
	
	public static double[] cartesianToSpherical(Vec3 vec) {
		double[] xyz = new double[3];
		//TODO This code is still being written
		return xyz;
	}
	
}
