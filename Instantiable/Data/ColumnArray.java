/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class ColumnArray {

	private final List<int[]> coords = new ArrayList<int[]>();

	public void add(int x, int z) {
		if (this.hasColumn(x, z))
			return;
		coords.add(new int[]{x,z});
	}

	public boolean hasColumn(int x, int z) {
		for (int i = 0; i < coords.size(); i++) {
			int[] e = coords.get(i);
			if (e[0] == x && e[1] == z)
				return true;
		}
		return false;
	}

	public void remove(int index) {
		coords.remove(index);
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return "Empty[]";
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < this.getSize(); i++) {
			list.append(Arrays.toString(coords.get(i)));
			if (i != this.getSize()-1)
				list.append(";");
		}
		return list.toString();
	}

	public int getSize() {
		return coords.size();
	}

	public boolean isEmpty() {
		return coords.isEmpty();
	}

	public void clear() {
		coords.clear();
	}

	public int[] getNthColumn(int n) {
		if (this.isEmpty())
			return null;
		return coords.get(n);
	}

	public int[] getNextColumn() {
		if (this.isEmpty())
			return null;
		return coords.get(0);
	}

	public int[] getNextAndMoveOn() {
		if (this.isEmpty())
			return null;
		int[] next = this.getNextColumn();
		coords.remove(0);
		return next;
	}

	public int[] getRandomColumn() {
		Random r = new Random();
		int s = this.getSize();
		return this.getNthColumn(r.nextInt(s));
	}

}
