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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;


public abstract class AbstractSearch {

	protected final HashSet<Coordinate> searchedCoords = new HashSet();

	public final Coordinate root;

	public BlockBox limit = BlockBox.infinity();
	public int depthLimit = Integer.MAX_VALUE;

	private LinkedList<Coordinate> result = new LinkedList();

	public AbstractSearch(int x, int y, int z) {
		root = new Coordinate(x, y, z);
		searchedCoords.add(root);
	}

	/** Note that the propagation condition must include the termination condition, or it will never be moved into! */
	public abstract boolean tick(World world, PropagationCondition propagation, TerminationCondition terminate);

	/** Whether a path is found or no valid paths exist. */
	public abstract boolean isDone();

	public abstract void clear();

	public final LinkedList<Coordinate> getResult() {
		return result;
	}

	public final Set<Coordinate> getTotalSearchedCoords() {
		return Collections.unmodifiableSet(searchedCoords);
	}

	public void complete(World world, PropagationCondition propagation, TerminationCondition terminate) {
		while (!this.tick(world, propagation, terminate)) {

		}
	}

	protected final boolean isValidLocation(World world, int x, int y, int z, Coordinate from, PropagationCondition p, TerminationCondition c) {
		return p.isValidLocation(world, x, y, z, from) || c.isValidTerminus(world, x, y, z);
	}

	protected ArrayList<Coordinate> getNextSearchCoordsFor(World world, Coordinate c) {
		return (ArrayList)c.getAdjacentCoordinates();
	}

	public static interface TerminationCondition {

		public boolean isValidTerminus(World world, int x, int y, int z);

	}

	public static interface FixedPositionTarget {

		public Coordinate getTarget();

	}

	public static final class LocationTerminus implements TerminationCondition, FixedPositionTarget {

		public final Coordinate target;

		public LocationTerminus(Coordinate c) {
			target = c;
		}

		@Override
		public boolean isValidTerminus(World world, int x, int y, int z) {
			return target.equals(x, y, z);
		}

		@Override
		public Coordinate getTarget() {
			return target;
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
