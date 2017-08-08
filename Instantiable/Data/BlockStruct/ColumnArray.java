/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public final class ColumnArray {

	private static final Random rand = new Random();

	private final ArrayList<Coordinate> coords = new ArrayList();
	private final HashSet<Coordinate> set = new HashSet();

	public boolean add(int x, int z) {
		if (this.hasColumn(x, z))
			return false;
		coords.add(new Coordinate(x, 0, z));
		return true;
	}

	public boolean hasColumn(int x, int z) {
		return set.contains(new Coordinate(x, 0, z));
	}

	public void remove(int index) {
		coords.remove(index);
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return "Empty[]";
		StringBuilder list = new StringBuilder();
		for (Coordinate c : coords) {
			list.append(c);
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

	public Coordinate getNthColumn(int n) {
		if (this.isEmpty())
			return null;
		return coords.get(n);
	}

	public Coordinate getNextColumn() {
		if (this.isEmpty())
			return null;
		return coords.get(0);
	}

	public Coordinate getNextAndMoveOn() {
		if (this.isEmpty())
			return null;
		Coordinate next = this.getNextColumn();
		coords.remove(0);
		return next;
	}

	public Coordinate getRandomColumn() {
		int s = this.getSize();
		return this.getNthColumn(rand.nextInt(s));
	}

	public void sort(Comparator<Coordinate> c) {
		Collections.sort(coords, c);
	}

}
