/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.ArrayList;
import java.util.Random;

public class OreDistributor {

	public final int blockID;
	public final int blockMetadata;
	private static final Random r = new Random();

	private ArrayList<int[]> ranges = new ArrayList<int[]>();

	public OreDistributor(int id, int meta) {
		blockID = id;
		blockMetadata = meta;
	}

	public OreDistributor(int id, int meta, int[]... ranges) {
		blockID = id;
		blockMetadata = meta;
		for (int i = 0; i < ranges.length; i++) {
			int[] arr = ranges[i];
			this.addRange(arr);
		}
	}

	private void addRange(int[] arr) {
		if (!this.hasRange(arr[0], arr[1])) {
			ranges.add(arr);
		}
	}

	public OreDistributor addRange(int lo, int hi) {
		if (!this.hasRange(lo, hi)) {
			int[] arr = new int[]{lo, hi};
			ranges.add(arr);
		}
		return this;
	}

	public boolean hasRange(int lo, int hi) {
		return ReikaJavaLibrary.listContainsArray(ranges, new int[]{lo, hi});
	}

	private int[] getRandomizedPair() {
		int index = r.nextInt(ranges.size());
		return ranges.get(index);
	}

	public int getRandomizedY() {
		int[] dy = this.getRandomizedPair();
		return dy[0]+r.nextInt(dy[1]-dy[0]+1);
	}

}