package Reika.DragonAPI;


public class ReikaEngLibrary {
	
	public static final double e = Math.E;				// s/e
	public static final double pi = Math.PI;		// s/e
	public static final double G = 6.67*0.00000000001;	// Grav Constant
	public static final double patm = 101300;			// Atmosphere Sealevel pressure
	public static final double rhog = 19300;			// Gold Density
	public static final double rhofe = 8200;			// Iron Density
	public static final double rhow = 1000;				// Water density
	public static final double rholava = 2700;			// Lava density
	

	/** Calculates an exponential decay. Args: Rate, initial value, time */
	public static double decay(double rate, double ivp, double time) {
		double pow = -rate*time;
		return (ivp*Math.pow(e, pow));
	}
	
	/** Calculates the amount remaining after n halflives.
	 * Args: Initial value,halflife, time */
	public static double halflife(double init, double hlife, double time) {
		return (init*Math.pow(2,time/hlife));
	}
	
	/** Returns true if the material would fail due to torsional loading.
	 * Assumes a circular shaft.
	 * Args: Torque, radius of shaft, max shear stress */
	public boolean mat_twistfailure(int torque, double radius, double taumax) {
		double J = (pi/2)*ReikaMathLibrary.intpow(radius,4);
		return ((torque*radius)/(J) > taumax);		//Currently an angle twist formula; NEEDS CORRECTION!!!
	}
	
	/** Returns true if the material would fail due to centripetal tensile forces.
	 * Assumes a circular disc.
	 * Args: disc mass, rpm, radius, cross-sectional area, max tensile stress */
	public boolean mat_rot_tensfailure(double m, int rpm, double radius, double A, double sigmamax) {
		double I = m*(pi/4)*ReikaMathLibrary.intpow(radius, 4);
		double omega = rpm*120*pi;
		double Ftens = m*radius*ReikaMathLibrary.intpow(radius, 2);
		return ((Ftens/A) > sigmamax);
	}
	
	/** Calculates the rotational kinetic energy of a circular mass. Args: mass, rpm, radius */
	public double rotenergy(double m, int rpm, double radius) {
		double omega = 120*pi*rpm;
		double I = m*(pi/4)*ReikaMathLibrary.intpow(radius, 4);
		return 0.5*I*ReikaMathLibrary.intpow(omega, 2);
	}
}
