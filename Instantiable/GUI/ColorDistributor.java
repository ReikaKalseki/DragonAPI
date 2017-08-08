/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;
import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.Immutable.RGB;

public class ColorDistributor {

	private static final Random rand = new Random();

	private final ArrayList<RGB> colors = new ArrayList();

	public ColorDistributor() {

	}

	public int forceColor(int color) {
		return this.addColor(color);
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
		return this.addColor(this.getNewColor(b));
	}

	private int addColor(int color) {
		return this.addColor(new RGB(color));
	}

	private int addColor(RGB color) {
		colors.add(color);
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
