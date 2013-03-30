package Reika.DragonAPI;

public class ReikaPhysicsHelper {
	
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
	
	/** Converts 3D cartesian coordinates into polar ones. Returns angles in degrees. Args: x, y, z */
	public static double[] cartesianToPolar(double x, double y, double z) {
		double[] coords = new double[3];
		coords[0] = ReikaMathLibrary.py3d(x, y, z); //length
		coords[1] = Math.acos(y/coords[0]);
		coords[2] = Math.acos(x/z);
		coords[1] = radToDeg(coords[1]);
		coords[2] = radToDeg(coords[2]);
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
	public static double[] targetPosn(double x, double y, double z, double x2, double y2, double z2, double g) {
		double[] v = new double[3];
		//TODO This is still being written, as it depends on a reliable value for g
		return v;
	}
	
}
