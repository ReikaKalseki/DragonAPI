package Reika.DragonAPI.Instantiable.Math;

import java.util.Collection;
import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.WeightedRandom;

public class NonUniformDistribution<V> {

	private final WeightedRandom<V> data = new WeightedRandom();

	public void setRNG(Random rand) {
		data.setRNG(rand);
	}

	public void calculate(WeightCalculator<V> callback, Collection<V> values) {
		data.clear();
		for (V v : values) {
			data.addEntry(v, callback.getWeightForValue(v));
		}
	}

	public V getRandomValue() {
		return data.getRandomEntry();
	}

	public static interface WeightCalculator<V> {

		double getWeightForValue(V v);

	}

	public static class TriangularDistribution extends NonUniformDistribution {

		public void calculate() {

		}

	}

}
