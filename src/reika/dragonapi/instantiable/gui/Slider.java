/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui;

public class Slider {

	public final int posX;
	public final int posY;
	public final int width;
	public final int height;

	private int position;

	public Slider(int x, int y, int w, int h) {
		posX = x;
		posY = y;
		width = w;
		height = h;
	}

	public float getFraction() {
		return position/(float)height;
	}

	public Slider setPosition(int pos) {
		position = pos;
		return this;
	}

}
