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
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class IterativeRecurser {

	private final HashSet<Coordinate> searchedCoords = new HashSet();
	private Collection<Coordinate> activeSearches = new ArrayList();

	public BlockBox limit = BlockBox.infinity();

	public IterativeRecurser(int x, int y, int z) {
		activeSearches.add(new Coordinate(x, y, z));
	}

	public void run(World world, PropagationCondition propagation) {
		while (!this.tick(world, propagation));
	}

	public boolean tick(World world, PropagationCondition propagation) {
		Collection<Coordinate> current = new ArrayList(activeSearches);
		activeSearches.clear();
		for (Coordinate s : current) {
			Collection<Coordinate> li = s.getAdjacentCoordinates();
			Collection<Coordinate> li2 = new ArrayList();
			for (Coordinate c : li) {
				if (c.yCoord >= 0 && c.yCoord < 256 && !searchedCoords.contains(c) && propagation.isValidLocation(world, c.xCoord, c.yCoord, c.zCoord) && limit.isBlockInside(c.xCoord, c.yCoord, c.zCoord)) {
					searchedCoords.add(c);
					activeSearches.add(c);
				}
			}
		}
		return activeSearches.isEmpty();
	}

	public Collection<Coordinate> getResult() {
		return Collections.unmodifiableCollection(searchedCoords);
	}
}
