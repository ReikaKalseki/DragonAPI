/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import reika.dragonapi.libraries.io.ReikaColorAPI;

public class BumpMap {

	private int[][] data;

	private final Class reference;
	private final String path;

	private final int referenceOffset;

	public BumpMap(Class root, String name) {
		this(root, name, 127);
	}

	public BumpMap(Class root, String name, int offset) {
		reference = root;
		path = name;
		referenceOffset = offset;
		this.load();
	}

	private void load() {
		try {
			BufferedImage img = ImageIO.read(reference.getResourceAsStream(path));
			data = new int[img.getWidth()][img.getHeight()];
			for (int i = 0; i < img.getWidth(); i++) {
				for (int k = 0; k < img.getHeight(); k++) {
					data[i][k] = ReikaColorAPI.HexToGS(img.getRGB(i, k))-referenceOffset;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getBump(int x, int y) {
		return data != null ? data[x][y] : 0;
	}

	public int getSizeX() {
		return data.length;
	}

	public int getSizeY() {
		return data[0].length;
	}

}
