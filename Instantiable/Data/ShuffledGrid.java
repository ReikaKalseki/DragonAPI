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
		for (int x = 0; x < gridSize; x += averageSeparation) {
			for (int z = 0; z < gridSize; z += averageSeparation) {
				int x2 = ReikaRandomHelper.getRandomPlusMinus(x, maxDeviation, rand);
				int z2 = ReikaRandomHelper.getRandomPlusMinus(z, maxDeviation, rand);
				x2 = (x2+gridSize)%gridSize;
				z2 = (z2+gridSize)%gridSize;
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
