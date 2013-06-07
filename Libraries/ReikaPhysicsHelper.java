/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import Reika.DragonAPI.DragonAPICore;

public final class ReikaPhysicsHelper extends DragonAPICore {

	public static final double TNTenergy = 12420000000D;
	public static final double g = 9.81D;

	/** Converts 3D polar coordinates into cartesian ones. Use angles in degrees. Args: magnitude, theta, phi */
	public static double[] polarToCartesian(double mag, double theta, double phi) {
		double[] coords = new double[3];
		theta = degToRad(theta);
		phi = degToRad(phi);
		coords[0] = mag*Math.cos(theta)*Math.cos(phi);
		coords[1] = mag*Math.sin(theta);
		coords[2] = mag*Math.cos(theta)*Math.sin(phi);
		return coords;
	}

	/** Converts 3D cartesian coordinates into polar ones. Returns angles in degrees, mapped 0-360. Args: x, y, z */
	public static double[] cartesianToPolar(double x, double y, double z) {
		double[] coords = new double[3];
		boolean is90to270 = false;
		coords[0] = ReikaMathLibrary.py3d(x, y, z); //length
		coords[1] = Math.acos(y/coords[0]);
		coords[2] = Math.atan2(x, z);
		coords[1] = radToDeg(coords[1]);
		coords[2] = 180+radToDeg(coords[2]);
		if (is90to270) {
			coords[2] *= -1;
		}
		return coords;
	}

	/** Converts a degree angle to a radian one. Args: Angle */
	public static double degToRad(double ang) {
		return (ang*Math.PI/180);
	}

	/** Converts a degree angle to a radian one. Args: Angle */
	public static double radToDeg(double ang) {
		return (ang*180/Math.PI);
	}

	/** Calculates the required velocity (in xyz cartesian coordinates) required to travel in
	 * projectile motion from point A to point B. Args: start x,y,z end x,y,z, double g */
	public static double[] targetPosn(double x, double y, double z, double x2, double y2, double z2, double ag) {
		double[] v = new double[3];
		//TODO This is still being written, as it depends on a reliable value for g
		return v;
	}

	/** Returns a modified value for the inverse-square law, based on the distance and initial magnitude.
	 * Args: Distance x,y,z, initial magnitude */
	public static double inverseSquare(double dx, double dy, double dz, double mag) {
		return mag/(dx*dx+dy*dy+dz*dz);
	}

	/** Returns a float value for MC-scaled explosion power, based off the input energy in joules. Recall TNT has
	 * a float power of 4F, corresponding to a real-energy value of 12.4 Gigajoules. Args: Energy */
	public static float getExplosionFromEnergy(double energy) {
		double ratio = energy/TNTenergy;
		return (float)(4*ratio);
	}
}
