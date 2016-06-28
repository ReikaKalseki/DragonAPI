/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.mathsci;

import java.awt.Point;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.instantiable.DoubleMatrix;
import reika.dragonapi.instantiable.LineClipper;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.libraries.java.ReikaArrayHelper;
import reika.dragonapi.libraries.world.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaVectorHelper extends DragonAPICore {

	/** Returns a standard Vec3 between two specified points, rather than from the origin.
	 * Args: start x,y,z, end x,y,z */
	public static Vec3 getVec2Pt(double x1, double y1, double z1, double x2, double y2, double z2) {
		Vec3 p1 = Vec3.createVectorHelper(x1, y1, z1);
		Vec3 p2 = Vec3.createVectorHelper(x2, y2, z2);
		return subtract(p2, p1);
	}

	public static Vec3 subtract(Vec3 p1, Vec3 p2) {
		return Vec3.createVectorHelper(p2.xCoord - p1.xCoord, p2.yCoord - p1.yCoord, p2.zCoord - p1.zCoord);
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

	public static int[] getPlayerLookBlockCoords(EntityPlayer ep, double scale) {
		Vec3 look = ep.getLookVec();
		double dx = ep.posX;
		double dy = ep.posY+ep.getEyeHeight();
		double dz = ep.posZ;
		look.xCoord *= scale;
		look.yCoord *= scale;
		look.zCoord *= scale;
		double[] xyz = {dx+look.xCoord, dy+look.yCoord, dz+look.zCoord};
		int x = (int)Math.floor(xyz[0]);
		int y = (int)Math.floor(xyz[1]);
		int z = (int)Math.floor(xyz[2]);
		return new int[]{x, y, z};
	}

	public static int[] getPlayerLookBlock(World world, EntityPlayer ep, double range, boolean passthru) {
		int[] xyz = new int[3];
		for (float i = 0; i <= range; i += 0.5) {
			double[] look = getPlayerLookCoords(ep, i);
			int x = MathHelper.floor_double(look[0]);
			int y = MathHelper.floor_double(look[1]);
			int z = MathHelper.floor_double(look[2]);
			Block id = world.getBlock(x, y, z);
			if (id != Blocks.air && !(passthru && ReikaWorldHelper.softBlocks(world, x, y, z))) {
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

	public static double getDistFromPointToLine(double x1, double y1, double z1, double x2, double y2, double z2, double x, double y, double z) {
		Vec3 v01 = getVec2Pt(x, y, z, x1, y1, z1);
		Vec3 v02 = getVec2Pt(x, y, z, x2, y2, z2);
		Vec3 v21 = getVec2Pt(x2, y2, z2, x1, y1, z1);
		return Math.abs(v01.crossProduct(v02).lengthVector()/v21.lengthVector());
	}

	public static Vec3 scaleVector(Vec3 vec, double len) {
		Vec3 ret = vec.normalize();
		ret.xCoord *= len;
		ret.yCoord *= len;
		ret.zCoord *= len;
		return ret;
	}

	public static Vector3f multiplyVectorByMatrix(Vector3f vector, Matrix4f matrix) {
		float newX = matrix.m00*vector.x+matrix.m01*vector.y+matrix.m02*vector.z+matrix.m03;
		float newY = matrix.m10*vector.x+matrix.m11*vector.y+matrix.m12*vector.z+matrix.m13;
		float newZ = matrix.m20*vector.x+matrix.m21*vector.y+matrix.m22*vector.z+matrix.m23;
		return new Vector3f(newX, newY, newZ);
	}

	public static void euler321Sequence(Matrix4f mat, double rx, double ry, double rz) {
		float z = (float)Math.toRadians(rz);
		float y = (float)Math.toRadians(ry);
		float x = (float)Math.toRadians(rx);
		mat.rotate(z, new Vector3f(0, 0, 1)).rotate(y, new Vector3f(0, 1, 0)).rotate(x, new Vector3f(1, 0, 0));
	}

	public static void euler213Sequence(Matrix4f mat, double rx, double ry, double rz) {
		float z = (float)Math.toRadians(rz);
		float y = (float)Math.toRadians(ry);
		float x = (float)Math.toRadians(rx);
		mat.rotate(y, new Vector3f(0, 1, 0)).rotate(x, new Vector3f(1, 0, 0)).rotate(z, new Vector3f(0, 0, 1));
	}

	public static Vec3 multiplyVectorByMatrix(Vec3 vector, DoubleMatrix matrix) {
		double newX = matrix.m00*vector.xCoord+matrix.m01*vector.yCoord+matrix.m02*vector.zCoord+matrix.m03;
		double newY = matrix.m10*vector.xCoord+matrix.m11*vector.yCoord+matrix.m12*vector.zCoord+matrix.m13;
		double newZ = matrix.m20*vector.xCoord+matrix.m21*vector.yCoord+matrix.m22*vector.zCoord+matrix.m23;
		return Vec3.createVectorHelper(newX, newY, newZ);
	}

	public static void euler321Sequence(DoubleMatrix mat, double rx, double ry, double rz) {
		double z = Math.toRadians(rz);
		double y = Math.toRadians(ry);
		double x = Math.toRadians(rx);
		mat.rotate(z, Vec3.createVectorHelper(0, 0, 1)).rotate(y, Vec3.createVectorHelper(0, 1, 0)).rotate(x, Vec3.createVectorHelper(1, 0, 0));
	}

	public static void euler213Sequence(DoubleMatrix mat, double rx, double ry, double rz) {
		double z = Math.toRadians(rz);
		double y = Math.toRadians(ry);
		double x = Math.toRadians(rx);
		mat.rotate(y, Vec3.createVectorHelper(0, 1, 0)).rotate(x, Vec3.createVectorHelper(1, 0, 0)).rotate(z, Vec3.createVectorHelper(0, 0, 1));
	}

	public static Vec3 getXYProjection(Vec3 vec) {
		return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, 0);
	}

	public static Vec3 getYZProjection(Vec3 vec) {
		return Vec3.createVectorHelper(0, vec.yCoord, vec.zCoord);
	}

	public static Vec3 getXZProjection(Vec3 vec) {
		return Vec3.createVectorHelper(vec.xCoord, 0, vec.zCoord);
	}

	public static Vec3 getInverseVector(Vec3 vec) {
		return Vec3.createVectorHelper(-vec.xCoord, -vec.yCoord, -vec.zCoord);
	}

	@SideOnly(Side.CLIENT)
	public static Vector3f rotateVector(Vector3f vec, double rx, double ry, double rz) {
		Matrix4f mat = new Matrix4f();
		euler321Sequence(mat, rx, ry, rz);
		return multiplyVectorByMatrix(new Vector3f(vec.x, vec.y, vec.z), mat);
	}

	public static Vec3 rotateVector(Vec3 vec, double rx, double ry, double rz) {
		DoubleMatrix mat = new DoubleMatrix();
		euler321Sequence(mat, rx, ry, rz);
		return multiplyVectorByMatrix(vec, mat);
	}

	public static HashSet<Coordinate> getCoordsAlongVector(double x1, double y1, double z1, double x2, double y2, double z2) {
		HashSet<Coordinate> set = new HashSet();
		double dd = ReikaMathLibrary.py3d(x2-x1, y2-y1, z2-z1);
		for (double d = 0; d <= dd; d += 0.25) {
			double f = d/dd;
			double dx = x1+f*(x2-x1);
			double dy = y1+f*(y2-y1);
			double dz = z1+f*(z2-z1);
			Coordinate c = new Coordinate(dx, dy, dz);
			set.add(c);
		}
		return set;
	}

	/** Returns null if no part of the line falls within the clipping box. Uses the Cohen Sutherland Method. */
	public static ImmutablePair<Point, Point> clipLine(int x0, int x1, int y0, int y1, int minX, int minY, int maxX, int maxY) {
		return new LineClipper(minX, minY, maxX, maxY).clip(x0, y0, x1, y1);
	}

}
