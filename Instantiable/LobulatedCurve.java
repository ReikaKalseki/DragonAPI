package Reika.DragonAPI.Instantiable;

import java.util.Random;

import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class LobulatedCurve {

	public final double amplitudeVariation;
	public final int degree;
	public final double angleStep;
	public final double minRadius;

	private static final Random delegateRand = new Random();

	private final double[] radii;

	public LobulatedCurve(double r, double a, int d) {
		this(r, a, d, 0.25);
	}

	public LobulatedCurve(double r, double a, int d, double da) {
		minRadius = r;
		amplitudeVariation = a;
		degree = d;
		angleStep = da;

		if (degree*amplitudeVariation >= minRadius)
			throw new IllegalArgumentException("Radius variation larger than base radius!");

		radii = new double[(int)(360/da)];
	}

	public LobulatedCurve generate() {
		return this.generate(delegateRand);
	}

	public LobulatedCurve generate(Random rand) {
		double[] amps = new double[degree];
		for (int i = 0; i < degree; i++) {
			amps[i] = rand.nextDouble()*amplitudeVariation;
		}
		double phase = rand.nextDouble()*360;
		for (int i = 0; i < radii.length; i++) {
			double theta = i*angleStep;
			double r = minRadius;
			for (int k = 0; k < degree; k++) {
				r += amps[k]*Math.sin(Math.toRadians(phase+k*theta));
			}
			radii[i] = r;
		}
		return this;
	}

	public double getRadius(double ang) {
		double didx = ((ang%360)+360)%360/angleStep;
		int idx = (int)didx;
		return ReikaMathLibrary.linterpolate(didx, idx, idx+1, radii[idx], radii[(idx+1)%radii.length]);
	}

}
