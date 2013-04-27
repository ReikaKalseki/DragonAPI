package Reika.DragonAPI;


public abstract class ReikaEngLibrary {
	
	public static final double e = Math.E;				// s/e
	public static final double pi = Math.PI;		// s/e
	public static final double G = 6.67*0.00000000001;	// Grav Constant
	
	/** Densities in kg/m^3 */
	public static final double patm = 101300;			// Atmosphere Sealevel pressure
	public static final double rhogold = 19300;			// Gold Density
	public static final double rhoiron = 8200;			// Iron Density
	public static final double rhowater = 1000;			// Water density
	public static final double rholava = 2700;			// Lava density
	public static final double rhowood = 800;			// Wood density
	public static final double rhorock = 3000;			// Rock density
	public static final double rhodiamond = 3500;		// Diamond density
	
	/** Shear moduli */
	public static final double Gsteel = 79.3*1000000000;
	public static final double Giron = 82*1000000000;
	public static final double Gglass = 26.2*1000000000;
	public static final double Gdiamond = 478*1000000000;
	public static final double Galuminum = 25.5*1000000000;
	public static final double Grubber = 0.6*1000000;
	public static final double Gwood = 620*1000000;
	public static final double Gconcrete = 79.3*1000000000;
	public static final double Gstone = 8*1000000000; //varies widely
	public static final double Ggold = 27*1000000000;
	
	/** Elastic Moduli */
	public static final double Esteel = 210*1000000000;
	public static final double Eiron = 211*1000000000;
	public static final double Eglass = 70*1000000000;
	public static final double Ediamond = 1.220*1000000000000D; //1.22 TPa... O_O
	public static final double Ealuminum = 69*1000000000;
	public static final double Erubber = 50*1000000;
	public static final double Ewood = 11*1000000;
	public static final double Econcrete = 30*1000000000;
	public static final double Estone = 50*1000000000; //varies widely
	public static final double Egold = 79*1000000000;
	
	/** Ultimate Tensile Strengths */
	public static final double Tsteel = 400*1000000;
	public static final double Tiron = 200*1000000;
	public static final double Tglass = 33*1000000;
	public static final double Tdiamond = 5*1000000000;
	public static final double Taluminum = 110*1000000;
	public static final double Trubber = 5*1000000;
	public static final double Twood = 20*1000000;
	public static final double Tconcrete = 3*1000000;
	public static final double Tstone = 100*10000000; //varies widely
	public static final double Tgold = 108*10000000;

	/** Ultimate Shear Strengths */
	public static final double Ssteel = 232*1000000;
	public static final double Siron = 116*1000000;
	public static final double Sglass = 19.1*1000000;
	public static final double Sdiamond = 2.9*1000000000;
	public static final double Saluminum = 63.8*1000000;
	public static final double Srubber = 2.9*1000000;
	public static final double Swood = 11.6*1000000;
	public static final double Sconcrete = 1.74*1000000; //tensile; 30x stronger for compression
	public static final double Sstone = 40*10000000; //varies widely
	public static final double Sgold = 62.6*10000000;

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
	public static boolean mat_twistfailure(int torque, double radius, double taumax) {
		double J = (pi/2)*ReikaMathLibrary.intpow(radius,4);
		//ReikaGuiAPI.write((torque*radius)/(J));
		return ((torque*radius)/(J) > taumax);
	}
	
	/** Returns true if the material would fail due to centripetal tensile forces.
	 * Assumes a circular disc.
	 * Args: disc mass, rpm, radius, cross-sectional area, max tensile stress */
	public static boolean mat_rot_tensfailure(double m, int rpm, double radius, double A, double sigmamax) {
	//	double I = m*(pi/4)*ReikaMathLibrary.intpow(radius, 4);
		double omega = rpm*120*pi;
		double Ftens = omega*m*radius/ReikaMathLibrary.intpow(radius, 2);
		return ((Ftens/A) > sigmamax);
	}
	
	/** Returns true if the material would fail due to centripetal tensile forces. Assumes a solid circular shaft.
	 * Args: Density, radius, omega, Ultimate Tensile Strength */
	public static boolean mat_rotfailure(double rho, double radius, double omega, double sigmamax) {
		double sigma = rho*omega*omega*radius*radius/2;
		//ReikaGuiAPI.write(sigma);
		return (sigma > sigmamax);
	}
	
	/** Calculates the rotational kinetic energy of a circular mass. Args: density, thickness, omega, radius */
	public static double rotenergy(double rho, double t, double omega, double radius) {
		double V = pi*radius*radius*t;
		double mass = rho*V;
		double I = mass*(pi/4)*ReikaMathLibrary.intpow(radius, 4);
		return 0.5*I*ReikaMathLibrary.intpow(omega, 2);
	}
}
