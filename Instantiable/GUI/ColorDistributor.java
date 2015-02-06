package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;
import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.Immutable.RGB;

public class ColorDistributor {

	private static final Random rand = new Random();

	private final ArrayList<RGB> colors = new ArrayList();

	public ColorDistributor() {

	}

	public int generateNewColor() {
		return this.generateNewColor(Bias.NONE);
	}

	public int generateRedColor() {
		return this.generateNewColor(Bias.RED);
	}

	public int generateGreenColor() {
		return this.generateNewColor(Bias.GREEN);
	}

	public int generateBlueColor() {
		return this.generateNewColor(Bias.BLUE);
	}

	private int generateNewColor(Bias b) {
		colors.add(this.getNewColor(b));
		return colors.size()-1;
	}

	private RGB getNewColor(Bias b) {
		RGB color = this.genColor(b);
		while (this.isTooSimilar(color)) {
			color = this.genColor(b);
		}
		return color;
	}

	private RGB genColor(Bias b) {
		return new RGB(b.rBias >= 0 ? b.rBias : rand.nextInt(255), b.gBias >= 0 ? b.gBias : rand.nextInt(255), b.bBias >= 0 ? b.bBias : rand.nextInt(255));
	}

	private boolean isTooSimilar(RGB color) {
		for (RGB c : colors) {
			if (c.getDistance(color) < 4)
				return true;
		}
		return false;
	}

	public int getColor(int index) {
		return colors.get(index).getInt();
	}

	private static enum Bias {
		RED(255, -1, -1),
		GREEN(-1, 255, -1),
		BLUE(-1, -1, 255),
		NONE(-1, -1, -1);

		private int rBias;
		private int gBias;
		private int bBias;

		private Bias(int r, int g, int b) {
			rBias = r;
			gBias = g;
			bBias = b;
		}
	}

}
