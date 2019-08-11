package Reika.DragonAPI.Libraries.MathSci;

import Reika.DragonAPI.Instantiable.Interpolation;

/** All temperatures are in KELVIN! */
public class GasPropertyCalculator {

	private static final double GAMMA_STANDARD = 1.4;
	public static final double Rair = 287;

	//these vary with T, and that is going into too much detail; assume calorically perfect
	//public double pressureCoefficient;
	//public double volumeCoefficient;

	private static final Interpolation gammaCurve = new Interpolation(false);
	public static final double AIR_CP = 1.007;
	public static final double AIR_CV = 0.7192;

	static {
		gammaCurve.addPoint(250, 1.401);
		gammaCurve.addPoint(300, 1.400);
		gammaCurve.addPoint(350, 1.398);
		gammaCurve.addPoint(400, 1.395);
		gammaCurve.addPoint(450, 1.391);
		gammaCurve.addPoint(500, 1.387);
		gammaCurve.addPoint(550, 1.381);
		gammaCurve.addPoint(600, 1.376);
		gammaCurve.addPoint(650, 1.37);
		gammaCurve.addPoint(700, 1.364);
		gammaCurve.addPoint(750, 1.359);
		gammaCurve.addPoint(800, 1.354);
		gammaCurve.addPoint(900, 1.344);
		gammaCurve.addPoint(1000, 1.336);
		gammaCurve.addPoint(1100, 1.331);
		gammaCurve.addPoint(1200, 1.324);
		gammaCurve.addPoint(1300, 1.318);
		gammaCurve.addPoint(1400, 1.313);
		gammaCurve.addPoint(1500, 1.309);
	}

	public static double getPressure(double rho, double t) { //ideal gas law, p = rho * R * T
		return Rair*rho*t;
	}

	public static double getDensity(double p, double t) { //ideal gas law, rho = p/RT
		return p/(Rair*t);
	}

	public static double getGamma(double temp) {
		/*
		if (pressureCoefficient != 0 && volumeCoefficient != 0) {

		}
		else {
			return GAMMA_STANDARD;
		}*/
		return gammaCurve.getValue(temp);
	}

	public static double getGammaMinusOneOverTwo(double temp) {
		return (getGamma(temp)-1)/2;
	}

	public static double getGammaMinusOneOverGamma(double temp) {
		return (getGamma(temp)-1)/getGamma(temp);
	}

	public static double getGammaOverGammaMinusOne(double temp) {
		return getGamma(temp)/(getGamma(temp)-1);
	}

}
