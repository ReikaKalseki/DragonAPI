/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Formula;

import java.util.ArrayList;
import java.util.Collection;


public class PeriodicExpression extends MathExpression {

	private final Collection<Wave> waves = new ArrayList();

	public PeriodicExpression() {

	}

	public PeriodicExpression addWave(double amplitude, double freq, double phase) {
		waves.add(new Wave(amplitude, freq, phase));
		return this;
	}

	public PeriodicExpression normalize() {
		double f = 1D/this.getTotalAmplitude();
		for (Wave w : waves) {
			w.amplitude *= f;
		}
		return this;
	}

	public double getTotalAmplitude() {
		double tot = 0;
		for (Wave w : waves) {
			tot += w.amplitude;
		}
		return tot;
	}

	@Override
	public double evaluate(double arg) throws ArithmeticException {
		double val = 0;
		for (Wave w : waves) {
			val += w.evaluate(arg);
		}
		return val;
	}

	@Override
	public double getBaseValue() {
		return 0;
	}

	@Override
	public String toString() {
		return "E sin (N x)";
	}

	private static class Wave {

		private double amplitude;
		private double frequency;
		private double phase;

		public Wave(double a, double f, double p) {
			amplitude = a;
			frequency = f;
			phase = p;
		}

		public double evaluate(double arg) {
			return amplitude*Math.sin(frequency*arg+phase);
		}

	}

}
