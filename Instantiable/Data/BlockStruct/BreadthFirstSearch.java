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
import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder.PassRules;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class BreadthFirstSearch extends AbstractSearch {

	private final Collection<SearchHead> activeSearches = new ArrayList();
	private final Collection<SearchHead> exhaustedSearches = new ArrayList();
	private final ArrayList<SearchHead> currentlyCalculating = new ArrayList();

	public int perCycleCalcLimit = Integer.MAX_VALUE;

	public BreadthFirstSearch(int x, int y, int z) {
		super(x, y, z);
		activeSearches.add(new SearchHead(root));
	}

	private boolean calculateCurrentQueue(World world, PropagationCondition propagation, TerminationCondition terminate) {
		int cycles = 0;
		while (!currentlyCalculating.isEmpty() && cycles < perCycleCalcLimit) {
			SearchHead s = currentlyCalculating.remove(0);
			s.isExhausted = true;
			Collection<Coordinate> li = this.getNextSearchCoordsFor(world, s.headLocation);
			Collection<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (c.yCoord >= 0 && c.yCoord < 256 && !searchedCoords.contains(c) && this.isValidLocation(world, c.xCoord, c.yCoord, c.zCoord, s.headLocation, propagation, terminate) && s.length() < depthLimit && limit.isBlockInside(c.xCoord, c.yCoord, c.zCoord)) {
					s.isExhausted = false;
					if (terminate != null && terminate.isValidTerminus(world, c.xCoord, c.yCoord, c.zCoord)) {
						activeSearches.clear();
						this.getResult().addAll(s.path);
						this.getResult().add(c);
						return true;
					}
					else {
						searchedCoords.add(c);
						activeSearches.add(s.extendTo(c));
					}
				}
			}
			if (s.isExhausted) {
				exhaustedSearches.add(s);
			}
			cycles++;
		}
		return false;
	}

	/** Note that the propagation condition must include the termination condition, or it will never be moved into! */
	@Override
	public boolean tick(World world, PropagationCondition propagation, TerminationCondition terminate) {
		if (currentlyCalculating.isEmpty()) {
			currentlyCalculating.addAll(activeSearches);
			activeSearches.clear();
		}
		if (this.calculateCurrentQueue(world, propagation, terminate)) {
			return true;
		}
		return this.isDone();
	}

	@Override
	public boolean isDone() {
		return activeSearches.isEmpty();
	}

	@Override
	public void clear() {
		searchedCoords.clear();
		activeSearches.clear();
		exhaustedSearches.clear();
		currentlyCalculating.clear();
		this.getResult().clear();
		System.gc();
	}

	public Collection<ArrayList<Coordinate>> getPathsTried() {
		Collection<ArrayList<Coordinate>> ret = new ArrayList();
		for (SearchHead s : exhaustedSearches) {
			ret.add(new ArrayList(s.path));
		}
		return ret;
	}

	@Override
	public void complete(World world, PropagationCondition propagation, TerminationCondition terminate) {
		while (!this.tick(world, propagation, terminate)) {

		}
	}

	public static LinkedList<Coordinate> getPath(World world, double x, double y, double z, TerminationCondition t, PropagationCondition c) {
		return getPath(world, x, y, z, t, c, null);
	}

	public static LinkedList<Coordinate> getPath(World world, double x, double y, double z, TerminationCondition t, PropagationCondition c, BlockBox bounds) {
		BreadthFirstSearch s = new BreadthFirstSearch(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
		if (bounds != null) {
			s.limit = bounds;
		}
		while (!s.tick(world, c, t)) {

		}
		return s.getResult().isEmpty() ? null : s.getResult();
	}

	public static LinkedList<Coordinate> getOpenPathBetween(World world, double x1, double y1, double z1, double x2, double y2, double z2, int r, Collection<PassRules> rules) {
		Coordinate start = new Coordinate(x1, y1, z1);
		Coordinate end = new Coordinate(x2, y2, z2);
		return getOpenPathBetween(world, start, end, r, rules);
	}

	public static LinkedList<Coordinate> getOpenPathBetween(World world, Coordinate start, Coordinate end, int r, Collection<PassRules> rules) {
		return getOpenPathBetween(world, start, end, r, null, rules);
	}

	public static LinkedList<Coordinate> getOpenPathBetween(World world, Coordinate start, Coordinate end, int r, BlockBox bounds, Collection<PassRules> rules) {
		OpenPathFinder f = new OpenPathFinder(start, end, r);
		f.rules.addAll(rules);
		TerminationCondition t = new LocationTerminus(end);
		return getPath(world, start.xCoord, start.yCoord, start.zCoord, t, f, bounds);
	}

	private static class SearchHead {

		private LinkedList<Coordinate> path = new LinkedList();
		private Coordinate headLocation;
		private boolean isExhausted = false;

		private SearchHead(Coordinate c) {
			headLocation = c;
			path.add(c);
		}

		public int length() {
			return path.size();
		}

		public SearchHead extendTo(Coordinate c) {
			SearchHead s = new SearchHead(headLocation);
			s.path = new LinkedList(path);
			s.path.add(c);
			s.headLocation = c;
			return s;
		}

	}

}
