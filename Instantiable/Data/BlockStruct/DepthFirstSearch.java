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
import java.util.LinkedList;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class DepthFirstSearch extends AbstractSearch {

	private final LinkedList<Coordinate> currentPath = new LinkedList();
	private Comparator stepValue;
	private boolean isDone;

	public DepthFirstSearch(int x, int y, int z) {
		super(x, y, z);
		currentPath.add(root);
	}

	@Override
	public boolean tick(World world, PropagationCondition propagation, TerminationCondition terminate) {
		if (isDone)
			return true;
		if (stepValue == null && terminate instanceof FixedPositionTarget)
			stepValue = new Coordinate.DistanceComparator(((FixedPositionTarget)terminate).getTarget(), false);
		Coordinate c = currentPath.getLast();
		ArrayList<Coordinate> li = this.getNextSearchCoordsFor(world, c);
		if (stepValue != null)
			Collections.sort(li, stepValue);
		for (Coordinate c2 : li) {
			if (c2.yCoord < 0 || c2.yCoord >= 256)
				continue;
			if (searchedCoords.contains(c2))
				continue;
			if (currentPath.size() > depthLimit || !limit.isBlockInside(c.xCoord, c.yCoord, c.zCoord))
				continue;
			if (!this.isValidLocation(world, c2.xCoord, c2.yCoord, c2.zCoord, currentPath.getLast(), propagation, terminate))
				continue;
			currentPath.add(c2);
			if (terminate.isValidTerminus(world, c2.xCoord, c2.yCoord, c2.zCoord)) {
				this.getResult().addAll(currentPath);
				isDone = true;
				return true;
			}
			else {
				searchedCoords.add(c2);
				return false;
			}
		}
		currentPath.removeLast();
		if (currentPath.isEmpty()) {
			isDone = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

	@Override
	public void clear() {
		searchedCoords.clear();
		currentPath.clear();
		this.getResult().clear();
		System.gc();
	}

	public static LinkedList<Coordinate> getPath(World world, double x, double y, double z, TerminationCondition t, PropagationCondition c) {
		DepthFirstSearch s = new DepthFirstSearch(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
		while (!s.tick(world, c, t)) {

		}
		return s.getResult().isEmpty() ? null : s.getResult();
	}

}
