package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class OpenPathFinder implements PropagationCondition {

	public final int searchRadius;
	public final EnumSet<PassRules> rules = EnumSet.noneOf(PassRules.class);
	private final Coordinate startLocation;
	private final Coordinate endLocation;

	public static final Collection<PassRules> defaultRules = Collections.unmodifiableSet(EnumSet.of(PassRules.SOFT));

	//private static final HashSet<Block> smallPassables = new HashSet();

	static {/*
		smallPassables.add(Blocks.torch);
		smallPassables.add(Blocks.ladder);
		smallPassables.add(Blocks.vine);
		smallPassables.add(Blocks.tripwire);
		smallPassables.add(Blocks.tripwire_hook);
		smallPassables.add(Blocks.red_flower);
		smallPassables.add(Blocks.yellow_flower);
		smallPassables.add(Blocks.deadbush);
		smallPassables.add(Blocks.double_plant);
		smallPassables.add(Blocks.brown_mushroom);
		smallPassables.add(Blocks.red_mushroom);
		smallPassables.add(Blocks.torch);
		smallPassables.add(Blocks.rail);
		smallPassables.add(Blocks.activator_rail);
		smallPassables.add(Blocks.golden_rail);
		smallPassables.add(Blocks.detector_rail);
		smallPassables.add(Blocks.flower_pot);
		smallPassables.add(Blocks.redstone_wire);
		smallPassables.add(Blocks.flower_pot);
		smallPassables.add(Blocks.flower_pot);
		smallPassables.add(Blocks.flower_pot);*/
	}

	public OpenPathFinder(Coordinate c1, Coordinate c2, int r) {
		startLocation = c1;
		endLocation = c2;
		searchRadius = Math.max(r, c2.getTaxicabDistanceTo(c1));
	}

	@Override
	public final boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
		if (startLocation.equals(x, y, z) || endLocation.equals(x, y, z))
			return true;
		if (!startLocation.isWithinDistOnAllCoords(x, y, z, searchRadius))
			return false;
		return this.isValidBlock(world, x, y, z);
	}

	protected boolean isValidBlock(World world, int x, int y, int z) {
		return isEmptyBlock(world, x, y, z, rules);
	}

	public static boolean isEmptyBlock(World world, int x, int y, int z, Collection<PassRules> rules) {
		Block b = world.getBlock(x, y, z);
		if (rules.contains(PassRules.SMALLNONSOLID) && isSmallPassable(world, x, y, z, b))
			return true;
		if (!rules.contains(PassRules.LIQUIDS) && ReikaBlockHelper.isLiquid(b))
			return false;
		if (rules.contains(PassRules.SOFT) && ReikaWorldHelper.softBlocks(world, x, y, z))
			return true;
		return b.isAir(world, x, y, z);
	}

	private static boolean isSmallPassable(World world, int x, int y, int z, Block b) {
		double vol = ReikaBlockHelper.getBlockVolume(world, x, y, z);
		double thresh = b.getCollisionBoundingBoxFromPool(world, x, y, z) == null ? 0.0625 : 0.0078125;
		return vol <= thresh;
	}

	public static enum PassRules {
		SOFT,
		LIQUIDS,
		SMALLNONSOLID;
	}
}
