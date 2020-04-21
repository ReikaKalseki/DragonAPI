package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class OpenPathFinder implements PropagationCondition {

	public final int searchRadius;
	private final Coordinate startLocation;
	private final Coordinate endLocation;

	public OpenPathFinder(Coordinate c1, Coordinate c2, int r) {
		startLocation = c1;
		endLocation = c2;
		searchRadius = Math.max(r, c2.getTaxicabDistanceTo(c1));
	}

	@Override
	public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
		if (startLocation.equals(x, y, z) || endLocation.equals(x, y, z))
			return true;
		if (!startLocation.isWithinDistOnAllCoords(x, y, z, searchRadius))
			return false;
		return isValidBlock(world, x, y, z);
	}

	public static boolean isValidBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		return b.isAir(world, x, y, z) || ReikaWorldHelper.softBlocks(world, x, y, z);
	}
}
