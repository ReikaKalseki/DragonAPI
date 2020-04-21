package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.Entity;

import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;

public interface EntityPathfinder {

	/** Return null to indicate completion or no valid path. */
	public DecimalPosition getNextWaypoint(Entity e);

	public boolean isInRange(Entity e);

}
