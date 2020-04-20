package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.Entity;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public interface EntityPathfinder {

	/** Return null to indicate completion or no valid path. */
	public Coordinate getNextWaypoint(Entity e);

	public boolean isInRange(Entity e);

}
