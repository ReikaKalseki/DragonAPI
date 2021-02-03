package Reika.DragonAPI.Instantiable.Math;

import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class NonUniformDistribution {

	private final WeightedRandom<Integer> data = new WeightedRandom();

	public void setRNG(Random rand) {
		data.setRNG(rand);
	}

	public void calculate(int from, int to, MathExpression formula) {
		data.clear();
		if (to < from) {
			int s = to;
			to = from;
			from = s;
		}
		for (int i = from; i <= to; i++) {
			data.addEntry(i, formula.evaluate(i));
		}
	}

	public void calculate(Distribution exp) {
		this.calculate(exp.minX, exp.maxX, exp);
	}

	public int getRandomValue() {
		return data.getRandomEntry();
	}

	public double getProbability(int val) {
		return data.getProbability(val);
	}

	public static abstract class Distribution extends MathExpression {

		public final int minX;
		public final int maxX;

		public Distribution(int x1, int x2) {
			minX = x1;
			maxX = x2;
		}

	}

	public static class CosineDistribution extends Distribution {

		public final double baseValue;
		public final int peakX;
		public final double peakY;

		public CosineDistribution(int x1, int x2, double base, int peakX, double peakY) {
			super(x1, x2);
			this.peakX = peakX;
			this.peakY = peakY;
			baseValue = base;
		}

		@Override
		public double evaluate(double arg) throws ArithmeticException {
			if (arg == peakX) {
				return peakY;
			}
			else if (arg > peakX) {
				return ReikaMathLibrary.cosInterpolation2(peakX, maxX, arg, peakY, baseValue);
			}
			else if (arg < peakX) {
				return ReikaMathLibrary.cosInterpolation2(minX, peakX, arg, baseValue, peakY);
			}
			else {
				return 0;
			}
		}

		@Override
		public double getBaseValue() {
			return baseValue;
		}

		@Override
		public String toString() {
			return "Cosine distribution ["+minX+", "+maxX+"] base "+baseValue+" peaking at "+peakX+","+peakY;
		}

	}

	public static class TriangularDistribution extends Distribution {

		public final double baseValue;
		public final int peakX;
		public final double peakY;

		public TriangularDistribution(int x1, int x2, double base, int peakX, double peakY) {
			super(x1, x2);
			this.peakX = peakX;
			this.peakY = peakY;
			baseValue = base;
		}

		@Override
		public double evaluate(double arg) throws ArithmeticException {
			if (arg == peakX) {
				return peakY;
			}
			else if (arg > peakX) {
				return ReikaMathLibrary.linterpolate(arg, peakX, maxX, peakY, baseValue);
			}
			else if (arg < peakX) {
				return ReikaMathLibrary.linterpolate(arg, minX, peakX, baseValue, peakY);
			}
			else {
				return 0;
			}
		}

		@Override
		public double getBaseValue() {
			return baseValue;
		}

		@Override
		public String toString() {
			return "Pyramid distribution ["+minX+", "+maxX+"] base "+baseValue+" peaking at "+peakX+","+peakY;
		}

	}

}