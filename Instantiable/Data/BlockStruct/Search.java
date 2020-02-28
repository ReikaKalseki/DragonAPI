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

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class Search {

	private final HashSet<Coordinate> searchedCoords = new HashSet();
	private Collection<SearchHead> activeSearches = new ArrayList();

	private LinkedList<Coordinate> result = new LinkedList();

	public BlockBox limit = BlockBox.infinity();
	public int depthLimit = Integer.MAX_VALUE;

	public Search(int x, int y, int z) {
		activeSearches.add(new SearchHead(new Coordinate(x, y, z)));
	}

	/** Note that the propagation condition must include the termination condition, or it will never be moved into! */
	public boolean tick(World world, PropagationCondition propagation, TerminationCondition terminate) {
		Collection<SearchHead> current = new ArrayList(activeSearches);
		activeSearches.clear();
		for (SearchHead s : current) {
			Collection<Coordinate> li = s.headLocation.getAdjacentCoordinates();
			Collection<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (c.yCoord >= 0 && c.yCoord < 256 && !searchedCoords.contains(c) && propagation.isValidLocation(world, c.xCoord, c.yCoord, c.zCoord) && s.length() < depthLimit && limit.isBlockInside(c.xCoord, c.yCoord, c.zCoord)) {
					if (terminate.isValidTerminus(world, c.xCoord, c.yCoord, c.zCoord)) {
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
		}
		return activeSearches.isEmpty();
	}

	public LinkedList<Coordinate> getResult() {
		return result;
	}

	public void complete(World world, PropagationCondition propagation, TerminationCondition terminate) {
		while (!this.tick(world, propagation, terminate)) {

		}
	}

	public static LinkedList<Coordinate> getPath(World world, int x, int y, int z, TerminationCondition t, PropagationCondition c) {
		Search s = new Search(x, y, z);
		while (!s.tick(world, c, t)) {

		}
		return s.result.isEmpty() ? null : s.result;
	}

	private static class SearchHead {

		private LinkedList<Coordinate> path = new LinkedList();
		private Coordinate headLocation;

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

		public boolean isValidLocation(World world, int x, int y, int z);

	}

	public static final class AirPropagation implements PropagationCondition {

		public static final AirPropagation instance = new AirPropagation();

		private AirPropagation() {

		}

		@Override
		public boolean isValidLocation(World world, int x, int y, int z) {
			return world.getBlock(x, y, z).isAir(world, x, y, z);
		}

	}

}
