/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaPhysicsHelper extends DragonAPICore {

	public static final double TNTenergy = 12420000000D;

	public static final double g = 9.81D;

	public static final double LIGHT_SPEED = 299792458D;

	public static final double ELECTRON_CHARGE = 1.602/ReikaMathLibrary.doubpow(10, 19);
	public static final double ELECTRON_MASS = 9.11/ReikaMathLibrary.doubpow(10, 31);
	public static final double NEUTRON_MASS = 1.674/ReikaMathLibrary.doubpow(10, 27);
	public static final double PROTON_MASS = 1.672/ReikaMathLibrary.doubpow(10, 27);

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

	/** Converts 3D cartesian coordinates into polar ones. Returns angles in degrees, mapped 0-360. Args: x, y, z; Returns: Dist, Theta, Phi */
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
		double[] target = {x2,y2,z2};
		double velocity;
		int theta;
		int phi;
		double dx = target[0]-x-0.5;
		double dy = target[1]-y-1;
		double dz = target[2]-z-0.5;
		double dl = ReikaMathLibrary.py3d(dx, 0, dz); //Horiz distance
		double g = 8.4695*ReikaMathLibrary.doubpow(dl, 0.2701);
		if (dy > 0)
			g *= (0.8951*ReikaMathLibrary.doubpow(dy, 0.0601));
		velocity = 10;
		theta = 0;
		phi = (int)Math.toDegrees(Math.atan2(dz, dx));
		while (theta <= 0) {
			velocity++;
			double s = ReikaMathLibrary.intpow(velocity, 4)-g*(g*dl*dl+2*dy*velocity*velocity);
			double a = velocity*velocity+Math.sqrt(s);
			theta = (int)Math.toDegrees(Math.atan(a/(g*dl)));
			phi = (int)Math.toDegrees(Math.atan2(dz, dx));
		}
		v = polarToCartesian(velocity, theta, phi);
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

	public static float getEnergyFromExplosion(float ex) {
		return ex/4F*(float)TNTenergy;
	}

	public static double getBlockDensity(Block b) {
		if (b == Blocks.air)
			return 1;
		if (b == Blocks.gold_block)
			return ReikaEngLibrary.rhogold;
		if (b == Blocks.iron_block)
			return ReikaEngLibrary.rhoiron;
		if (b == Blocks.diamond_block)
			return ReikaEngLibrary.rhodiamond;
		if (b == Blocks.emerald_block)
			return 2740;
		if (b == Blocks.lapis_block)
			return 2800;
		if (b == Blocks.gravel)
			return 1680;
		if (b.getMaterial() == Material.rock)
			return ReikaEngLibrary.rhorock;
		if (b.getMaterial() == Material.glass)
			return ReikaEngLibrary.rhorock;
		if (b.getMaterial() == Material.grass)
			return 1250;
		if (b.getMaterial() == Material.ground)
			return 1220;
		if (b.getMaterial() == Material.clay)
			return 1650;
		if (b.getMaterial() == Material.sand)
			return 1555;
		if (b.getMaterial() == Material.wood)
			return ReikaEngLibrary.rhowood;
		if (b.getMaterial() == Material.leaves)
			return 100;
		if (b.getMaterial() == Material.sponge)
			return 280;
		if (b.getMaterial() == Material.plants)
			return 100;
		if (b.getMaterial() == Material.coral)
			return 100;
		if (b.getMaterial() == Material.cloth)
			return 1314;
		if (b.getMaterial() == Material.iron)
			return ReikaEngLibrary.rhoiron;
		if (b.getMaterial() == Material.water)
			return ReikaEngLibrary.rhowater;
		if (b.getMaterial() == Material.lava)
			return ReikaEngLibrary.rholava;
		if (b.getMaterial() == Material.ice)
			return 917;
		return 2200;
	}

	public static double getProjectileVelocity(double dist, double ang, double dy, double gravity) {
		ang = Math.toRadians(ang);
		double denom = dist*Math.tan(ang)+dy;
		double root = Math.sqrt(0.5*gravity*dist*dist/denom);
		return root/Math.cos(ang);
	}

	public static double getProjectileRange(double vel, double ang, double dy, double gravity) {
		ang = Math.toRadians(ang);
		double root = Math.pow(vel*Math.sin(ang), 2)+2*g*dy;
		double term = vel*Math.sin(ang)+Math.sqrt(root);
		return vel*Math.cos(ang)/gravity*term;
	}

	public static void reflectEntitySpherical(double x, double y, double z, Entity e) {
		double dx = e.posX-x;
		double dy = e.posY-y;
		double dz = e.posZ-z;
		Vec3 vec = Vec3.createVectorHelper(dx, dy, dz);
		double l = vec.lengthVector();
		vec.xCoord /= l;
		vec.yCoord /= l;
		vec.zCoord /= l;
		double vel = vec.dotProduct(Vec3.createVectorHelper(e.motionX, e.motionY, e.motionZ));
		e.motionX += -2*vel*vec.xCoord;
		e.motionY += -2*vel*vec.yCoord;
		e.motionZ += -2*vel*vec.zCoord;
		e.velocityChanged = true;
	}
}
