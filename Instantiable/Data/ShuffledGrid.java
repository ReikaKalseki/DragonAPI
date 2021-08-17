package Reika.DragonAPI.Instantiable.Data;

import java.util.Random;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class ShuffledGrid {

	public final int gridSize;
	public final int maxDeviation;
	public final int averageSeparation;

	private final boolean[][] data;

	public ShuffledGrid(int size, int dev, int sep) {
		this(size, dev, sep, false);
	}

	public ShuffledGrid(int size, int dev, int sep, boolean allowOverlap) {
		gridSize = size;
		maxDeviation = dev;
		averageSeparation = sep;

		if (dev >= sep/2 && !allowOverlap) {
			throw new IllegalArgumentException("Shuffled grid may have row overlap: "+sep+" +/- "+dev+"!");
		}

		data = new boolean[size][size];
	}

	public void calculate(long seed) {
		this.calculate(new Random(seed));
	}

	public void calculate(Random rand) {
		rand.nextBoolean();
		rand.nextBoolean();
		for (int x = maxDeviation; x < gridSize-maxDeviation; x += averageSeparation) {
			for (int z = maxDeviation; z < gridSize-maxDeviation; z += averageSeparation) {
				int x2 = ReikaRandomHelper.getRandomPlusMinus(x, maxDeviation, rand);
				int z2 = ReikaRandomHelper.getRandomPlusMinus(z, maxDeviation, rand);
				data[x2][z2] = true;
			}
		}
	}

	public boolean isValid(int x, int z) {
		/*
		while (x < 0)
			x += gridSize;
		while (z < 0)
			z += gridSize;
		 */
		x = ((x%gridSize)+gridSize)%gridSize;
		z = ((z%gridSize)+gridSize)%gridSize;
		return data[x][z];
	}
}
