package Reika.DragonAPI.Instantiable.Formula;

import java.util.TreeMap;

public abstract class PiecewiseExpression extends MathExpression {

	private final TreeMap<Range, MathExpression> data = new TreeMap();

	protected final void addComponent(double min, boolean inclmin, double max, boolean inclmax, MathExpression exp) {
		data.put(new Range(min, max, inclmin, inclmax), exp);
	}

	@Override
	public double getBaseValue() {
		return Double.NaN;
	}

	@Override
	public final double evaluate(double arg) {
		Range r = this.getRangeFor(arg);
		MathExpression exp = r != null ? data.get(r) : null;
		return exp != null ? exp.evaluate(arg) : Double.NaN;
	}

	private Range getRangeFor(double arg) {
		for (Range r : data.keySet()) {
			if (r.contains(arg))
				return r;
		}
		return null;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Range r : data.keySet()) {
			sb.append(r.toString());
			sb.append("=");
			sb.append(data.get(r).toString());
			sb.append(";");
		}
		sb.append("}");
		return sb.toString();
	}

	private static class Range implements Comparable<Range> {

		private final double lowerLimit;
		private final double upperLimit;
		private final boolean inclLower;
		private final boolean inclUpper;

		private Range(double l, double h, boolean il, boolean iu) {
			lowerLimit = l;
			upperLimit = h;

			inclLower = il;
			inclUpper = iu;
		}

		public boolean contains(double arg) {
			if (arg > lowerLimit && arg < upperLimit)
				return true;
			if (arg == lowerLimit)
				return inclLower;
			if (arg == upperLimit)
				return inclUpper;
			return false;
		}

		@Override
		public int compareTo(Range o) {
			return (int)(lowerLimit*100);
		}

		@Override
		public int hashCode() {
			return (int)(lowerLimit*100)^(int)(upperLimit*100)+(inclLower ? 50000 : 0)+(inclUpper ? 1000000 : 0);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Range) {
				Range r = (Range)o;
				return r.lowerLimit == lowerLimit && r.upperLimit == upperLimit && r.inclLower == inclLower && r.inclUpper == inclUpper;
			}
			return false;
		}

		@Override
		public final String toString() {
			return (inclLower ? "[" : "(")+lowerLimit+","+upperLimit+(inclUpper ? "]" : ")");
		}

	}

}
