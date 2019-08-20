package Reika.DragonAPI.Instantiable;

import java.util.Random;

public class GridDistortion {

	private final int gridSize;

	private final double[][] offsetsA;
	private final double[][] offsetsB;

	public double maxDeviation;
	public boolean snapToEdges = true;

	public GridDistortion(int steps) {
		gridSize = steps;
		offsetsA = new double[steps+1][steps+1];
		offsetsB = new double[steps+1][steps+1];
		maxDeviation = 0.5D/steps;
	}

	public void randomize(Random rand) {
		for (int i = 0; i < offsetsA.length; i++) {
			for (int k = 0; k < offsetsA.length; k++) {
				offsetsA[i][k] = -maxDeviation+maxDeviation*2*rand.nextDouble();
				offsetsB[i][k] = -maxDeviation+maxDeviation*2*rand.nextDouble();
			}
		}
	}

	public OffsetGroup getOffset(int a, int b) {
		double amm = offsetsA[a][b];
		double apm = offsetsA[a+1][b];
		double amp = offsetsA[a][b+1];
		double app = offsetsA[a+1][b+1];

		double bmm = offsetsB[a][b];
		double bpm = offsetsB[a+1][b];
		double bmp = offsetsB[a][b+1];
		double bpp = offsetsB[a+1][b+1];

		if (snapToEdges) {
			if (a == 0) {
				amm = 0;
				amp = 0;
			}
			if (a == gridSize) {
				apm = 0;
				app = 0;
			}
			if (b == 0) {
				bmm = 0;
				bmp = 0;
			}
			if (b == gridSize) {
				bpm = 0;
				bpp = 0;
			}
		}

		return new OffsetGroup(amm, apm, amp, app, bmm, bpm, bmp, bpp);
	}

	public static class OffsetGroup {

		public final double offsetAMM;
		public final double offsetAPM;
		public final double offsetAMP;
		public final double offsetAPP;

		public final double offsetBMM;
		public final double offsetBPM;
		public final double offsetBMP;
		public final double offsetBPP;

		private OffsetGroup(double amm, double apm, double amp, double app, double bmm, double bpm, double bmp, double bpp) {
			offsetAMM = amm;
			offsetAMP = amp;
			offsetAPM = apm;
			offsetAPP = app;

			offsetBMM = bmm;
			offsetBMP = bmp;
			offsetBPM = bpm;
			offsetBPP = bpp;
		}

	}

}
