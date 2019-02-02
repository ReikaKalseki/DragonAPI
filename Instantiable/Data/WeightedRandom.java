/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class WeightedRandom<V> {

	private final Random rand = new Random();

	private final HashMap<V, Double> data = new HashMap();
	private double maxWeight = 0;
	private double weightSum;

	public double addEntry(V obj, double weight) {
		if (weight < 0)
			throw new MisuseException("You cannot have an entry with a negative weight!");
		data.put(obj, weight);
		this.weightSum += weight;
		this.maxWeight = Math.max(this.maxWeight, weight);
		return this.weightSum;
	}

	public double remove(V val) {
		double ret = data.remove(val);
		this.weightSum -= ret;
		return ret;
	}

	public V getRandomEntry() {
		double d = rand.nextDouble()*this.weightSum;
		double p = 0;
		for (V obj : data.keySet()) {
			p += data.get(obj);
			if (d <= p) {
				return obj;
			}
		}
		return null;
	}

	public V getRandomEntry(V fallback, double wt) {
		double sum = this.weightSum+wt;
		double d = rand.nextDouble()*sum;
		double p = 0;
		for (V obj : data.keySet()) {
			p += data.get(obj);
			if (d <= p) {
				return obj;
			}
		}
		return fallback;
	}

	public double getWeight(V obj) {
		Double get = data.get(obj);
		return get != null ? get.doubleValue() : 0;
	}

	public double getMaxWeight() {
		return this.maxWeight;
	}

	public double getTotalWeight() {
		return this.weightSum;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public int size() {
		return data.size();
	}

	public boolean hasEntry(V obj) {
		return data.containsKey(obj);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void setSeed(long seed) {
		rand.setSeed(seed);
	}

	public void clear() {
		this.data.clear();
		this.maxWeight = 0;
		this.weightSum = 0;
	}

	public static class InvertedWeightedRandom<V> {

		private final Random rand = new Random();

		private final NavigableMap<Double, V> data = new TreeMap<Double, V>();
		private double weightSum;

		public void addEntry(double weight, V result) {
			weightSum += weight;
			data.put(weightSum, result);
		}

		public V getRandomEntry() {
			double value = rand.nextDouble()*this.weightSum;
			//ReikaJavaLibrary.pConsole(value+" of "+this.data.toString());
			return data.ceilingEntry(value).getValue();
		}

		public boolean isEmpty() {
			return data.isEmpty();
		}

		public int size() {
			return data.size();
		}

		@Override
		public String toString() {
			return data.toString();
		}
	}

	public static WeightedRandom<Coordinate> fromArray(int[][] arr) {
		WeightedRandom<Coordinate> w = new WeightedRandom();
		int dx = arr.length/2;
		for (int i = 0; i < arr.length; i++) {
			int dz = arr[i].length/2;
			for (int k = 0; k < arr[i].length; k++) {
				if (arr[i][k] > 0) {
					Coordinate c = new Coordinate(i-dx, 0, k-dz);
					w.addEntry(c, arr[i][k]);
				}
			}
		}
		return w;
	}

	public static WeightedRandom<Coordinate> fromArray(double[][] arr) {
		WeightedRandom<Coordinate> w = new WeightedRandom();
		int dx = arr.length/2;
		for (int i = 0; i < arr.length; i++) {
			int dz = arr[i].length/2;
			for (int k = 0; k < arr[i].length; k++) {
				if (arr[i][k] > 0) {
					Coordinate c = new Coordinate(i-dx, 0, k-dz);
					w.addEntry(c, arr[i][k]);
				}
			}
		}
		return w;
	}

	public Set<V> getValues() {
		return Collections.unmodifiableSet(data.keySet());
	}

}
