package Reika.DragonAPI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class ReikaVectorHelper {
	
	private ReikaVectorHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}
	
	/** Returns a standard fake between two specified points, rather than from the origin.
	 * Args: start x,y,z, end x,y,z */
	public static Vec3 getVec2Pt(double x1, double y1, double z1, double x2, double y2, double z2) {
		Vec3 p1 = Vec3.fakePool.getVecFromPool(x1, y1, z1);
		Vec3 p2 = Vec3.fakePool.getVecFromPool(x2, y2, z2);
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
	
	public static double[] getPlayerLookCoords(EntityPlayer ep, double scale) {
		Vec3 look = ep.getLookVec();
		double dx = ep.posX;
		double dy = ep.posY+ep.getEyeHeight();
		double dz = ep.posZ;
		look.xCoord *= scale;
		look.yCoord *= scale;
		look.zCoord *= scale;
		double[] xyz = {dx+look.xCoord, dy+look.yCoord, dz+look.zCoord};
		return xyz;
	}
	
	public static int[] getPlayerLookBlock(World world, EntityPlayer ep, double range, boolean passthru) {
		int[] xyz = new int[3];
		for (float i = 0; i <= range; i += 0.5) {
			double[] look = getPlayerLookCoords(ep, i);
			int x = (int)look[0];
			int y = (int)look[1];
			int z = (int)look[2];
			int id = world.getBlockId(x, y, z);
			if (id != 0 && !(passthru && ReikaWorldHelper.softBlocks(id))) {
				xyz[0] = x;
				xyz[1] = y;
				xyz[2] = z;
				return xyz;
			}
		}
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
