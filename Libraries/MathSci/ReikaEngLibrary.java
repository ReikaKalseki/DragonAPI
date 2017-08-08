/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

import net.minecraft.util.MathHelper;
import Reika.DragonAPI.DragonAPICore;


public final class ReikaEngLibrary extends DragonAPICore {

	/** Various constants */
	public static final double e = Math.E;		// s/e
	public static final double pi = Math.PI;	// s/e
	public static final double G = 6.67e-11;	// Grav Constant
	public static final double c = 2.998e8;		// Speed of light
	public static final double k = 9e9;			// electrostatic constant
	public static final double sigma = 5.67e-8;	// heat radiation constant

	/** Densities in kg/m^3 */
	public static final double patm = 101300;			// Atmosphere Sealevel pressure
	public static final double rhogold = 19300;			// Gold Density
	public static final double rhoiron = 7800;			// Iron Density
	public static final double rhowater = 1000;			// Water density
	public static final double rholava = 2700;			// Lava density
	public static final double rhowood = 800;			// Wood density
	public static final double rhorock = 3000;			// Rock density
	public static final double rhodiamond = 3500;		// Diamond density
	public static final double rhographite = 2150;		// Graphite density

	/** Shear moduli */
	public static final double Gsteel = 79.3e9;
	public static final double Giron = 82e9;
	public static final double Gglass = 26.2e9;
	public static final double Gdiamond = 478e9;
	public static final double Galuminum = 25.5e9;
	public static final double Grubber = 0.6e6;
	public static final double Gwood = 620e6;
	public static final double Gconcrete = 79.3e9;
	public static final double Gstone = 8e9; //varies widely
	public static final double Ggold = 27e9;

	/** Elastic Moduli */
	public static final double Esteel = 210e9;
	public static final double Eiron = 211e9;
	public static final double Eglass = 70e9;
	public static final double Ediamond = 1.220e12; //1.22 TPa... O_O
	public static final double Ealuminum = 69e9;
	public static final double Erubber = 50e6;
	public static final double Ewood = 11e6;
	public static final double Econcrete = 30e9;
	public static final double Estone = 50e9; //varies widely
	public static final double Egold = 79e9;

	/** Ultimate Tensile Strengths */
	public static final double Tsteel = 400e6;
	public static final double Tiron = 200e6;
	public static final double Tglass = 33e6;
	public static final double Tdiamond = 5e9;
	public static final double Taluminum = 110e6;
	public static final double Trubber = 5e6;
	public static final double Twood = 20e6;
	public static final double Tconcrete_tensile = 3e6;
	public static final double Tconcrete_compressive = 9e7;
	public static final double Tstone = 100e6; //varies widely
	public static final double Tgold = 108e6;

	/** Ultimate Shear Strengths */
	public static final double Ssteel = 280e6;
	public static final double Siron = 116e6;
	public static final double Sglass = 19.1e6;
	public static final double Sdiamond = 2.9e9;
	public static final double Saluminum = 63.8e6;
	public static final double Srubber = 2.9e6;
	public static final double Swood = 11.6e6;
	public static final double Sconcrete_tensile = 1.74e6;
	public static final double Sconcrete_compressive = 5.1e7;
	public static final double Sstone = 40e6; //varies widely
	public static final double Sgold = 62.6e6;

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

	public static double getHeatFromFriction(double Fnormal, double mu, double vSlip, double eta) {
		return Fnormal*mu*vSlip*eta;
	}

	public static String getSIPrefix(double val) {
		if (val == 0 || (val < 10 && val >= 1))
			return "";
		int log = MathHelper.floor_double(ReikaMathLibrary.logbase(val, 1000));
		switch(log) {
		case 1:
			return "k";
		case 2:
			return "M";
		case 3:
			return "G";
		case 4:
			return "T";
		case 5:
			return "P";
		case 6:
			return "E";

		case -1:
			return "m";
		case -2:
			return "micro";
		case -3:
			return "n";
		case -4:
			return "p";
		case -5:
			return "f";

		default:
			return "";
		}
	}
}
