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
import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;


public class Search {

	private final HashSet<Coordinate> searchedCoords = new HashSet();
	private final Collection<SearchHead> activeSearches = new ArrayList();
	private final Collection<SearchHead> exhaustedSearches = new ArrayList();
	private final ArrayList<SearchHead> currentlyCalculating = new ArrayList();

	private LinkedList<Coordinate> result = new LinkedList();

	public BlockBox limit = BlockBox.infinity();
	public int depthLimit = Integer.MAX_VALUE;
	public int perCycleCalcLimit = Integer.MAX_VALUE;

	public Search(int x, int y, int z) {
		activeSearches.add(new SearchHead(new Coordinate(x, y, z)));
	}

	private boolean calculateCurrentQueue(World world, PropagationCondition propagation, TerminationCondition terminate) {
		int cycles = 0;
		while (!currentlyCalculating.isEmpty() && cycles < perCycleCalcLimit) {
			SearchHead s = currentlyCalculating.remove(0);
			s.isExhausted = true;
			Collection<Coordinate> li = s.headLocation.getAdjacentCoordinates();
			Collection<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (c.yCoord >= 0 && c.yCoord < 256 && !searchedCoords.contains(c) && propagation.isValidLocation(world, c.xCoord, c.yCoord, c.zCoord, s.headLocation) && s.length() < depthLimit && limit.isBlockInside(c.xCoord, c.yCoord, c.zCoord)) {
					s.isExhausted = false;
					if (terminate != null && terminate.isValidTerminus(world, c.xCoord, c.yCoord, c.zCoord)) {
						activeSearches.clear();
						result.addAll(s.path);
						result.add(c);
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

	public boolean isDone() {
		return activeSearches.isEmpty();
	}

	public void clear() {
		searchedCoords.clear();
		activeSearches.clear();
		exhaustedSearches.clear();
		currentlyCalculating.clear();
		result.clear();
		System.gc();
	}

	public LinkedList<Coordinate> getResult() {
		return result;
	}

	public Collection<ArrayList<Coordinate>> getPathsTried() {
		Collection<ArrayList<Coordinate>> ret = new ArrayList();
		for (SearchHead s : exhaustedSearches) {
			ret.add(new ArrayList(s.path));
		}
		return ret;
	}

	public void complete(World world, PropagationCondition propagation, TerminationCondition terminate) {
		while (!this.tick(world, propagation, terminate)) {

		}
	}

	public static LinkedList<Coordinate> getPath(World world, double x, double y, double z, TerminationCondition t, PropagationCondition c) {
		Search s = new Search(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
		while (!s.tick(world, c, t)) {

		}
		return s.result.isEmpty() ? null : s.result;
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

	public static interface TerminationCondition {

		public boolean isValidTerminus(World world, int x, int y, int z);

	}

	public static final class LocationTerminus implements TerminationCondition {

		public final Coordinate target;

		public LocationTerminus(Coordinate c) {
			target = c;
		}

		@Override
		public boolean isValidTerminus(World world, int x, int y, int z) {
			return target.equals(x, y, z);
		}

	}

	public static interface PropagationCondition {

		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from);

	}

	public static final class CompoundPropagationCondition implements PropagationCondition {

		private final ArrayList<PropagationCondition> conditions = new ArrayList();

		public CompoundPropagationCondition() {

		}

		public CompoundPropagationCondition addCondition(PropagationCondition pc) {
			conditions.add(pc);
			return this;
		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
			for (PropagationCondition pc : conditions) {
				if (!pc.isValidLocation(world, x, y, z, from))
					return false;
			}
			return true;
		}

	}

	public static final class AirPropagation implements PropagationCondition {

		public static final AirPropagation instance = new AirPropagation();

		private AirPropagation() {

		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
			return world.getBlock(x, y, z).isAir(world, x, y, z);
		}

	}

	public static final class DirectionalPropagation implements PropagationCondition {

		public final Coordinate location;
		public final boolean requireCloser;

		public DirectionalPropagation(Coordinate c, boolean cl) {
			location = c;
			requireCloser = cl;
		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
			int d0 = from.getTaxicabDistanceTo(location);
			int d1 = new Coordinate(x, y, z).getTaxicabDistanceTo(location);
			return requireCloser ? d1 < d0 : d0 < d1;
		}

	}

	public static final class WalkablePropagation implements PropagationCondition {

		public static final WalkablePropagation instance = new WalkablePropagation();

		private WalkablePropagation() {

		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
			return PassablePropagation.instance.isValidLocation(world, x, y, z, from) && (!PassablePropagation.instance.isValidLocation(world, x, y-1, z, from) || !PassablePropagation.instance.isValidLocation(world, x, y-2, z, from));
		}

	}

	public static final class PassablePropagation implements PropagationCondition {

		public static final PassablePropagation instance = new PassablePropagation();

		private PassablePropagation() {

		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
			return !ReikaBlockHelper.isCollideable(world, x, y, z);
		}

	}

}
