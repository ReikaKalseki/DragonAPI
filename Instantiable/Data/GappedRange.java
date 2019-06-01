package Reika.DragonAPI.Instantiable.Data;

import java.util.HashSet;

public class GappedRange {

	private final HashSet<Integer> flips = new HashSet();
	private int minValue = Integer.MAX_VALUE;
	private int maxValue = Integer.MIN_VALUE;
	private boolean startsSolid;

	public GappedRange() {

	}

	public void addEndpoint(int val, boolean solid) {
		if (val < minValue) {
			minValue = val;
			startsSolid = solid;
		}
		if (val > maxValue) {
			maxValue = val;
		}
	}

	public void addSolidRange(int from, int to) {
		this.addTransition(from);
		this.addTransition(to);
		if (this.setMinimum(from)) {
			startsSolid = true;
		}
		maxValue = Math.max(maxValue, to);
	}

	public void addGap(int from, int to) {
		this.addTransition(from);
		this.addTransition(to);
		if (this.setMinimum(from)) {
			startsSolid = false;
		}
		maxValue = Math.max(maxValue, to);
	}

	private boolean setMinimum(int min) {
		if (min < minValue) {
			minValue = min;
			return true;
		}
		return false;
	}

	public void addTransition(int at) {
		if (at != minValue && at != maxValue)
			flips.add(at);
		if (at == minValue)
			startsSolid = !startsSolid;
	}

	public boolean isInGap(int at) {
		boolean solid = startsSolid;
		for (int flip : flips) {
			if (flip <= at) {
				solid = !solid;
			}
		}
		return !solid;
	}

	public boolean isInRelativeGap(int at) {
		return this.isInGap(at-minValue);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean solid = startsSolid;
		for (int i = minValue; i <= maxValue; i++) {
			if (flips.contains(i))
				solid = !solid;
			sb.append(solid ? "#" : "O");
		}
		return sb.toString();
	}

}
