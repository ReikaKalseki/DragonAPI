package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.CompoundPropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.EntityPathfinder;

public class RoutingWeb implements EntityPathfinder {

	private static final PropagationCondition passableBlocks = Search.PassablePropagation.instance;
	private static final PropagationCondition walkableBlocks = Search.WalkablePropagation.instance;

	private final Coordinate root;
	private final Search pathfinder;
	private final PropagationCondition propagation;

	private final HashMap<Coordinate, Coordinate> pathMap = new HashMap();

	public RoutingWeb(TileEntity te, int rx, int ry, int rz, boolean canFly) {
		this(te.xCoord, te.yCoord, te.zCoord, rx, ry, rz, canFly);
	}

	public RoutingWeb(int x, int y, int z, int rx, int ry, int rz, boolean canFly) {
		pathfinder = new Search(x, y, z);
		root = new Coordinate(x, y, z);
		pathfinder.limit = BlockBox.block(x, y, z).expand(rx, ry, rz);
		propagation = new CompoundPropagationCondition().addCondition().addCondition(canFly ? passableBlocks : walkableBlocks);
	}

	/** Returns true when done. */
	public boolean runCalc(World world) {
		return pathfinder.tick(world, propagation, null);
	}

	public void buildPaths() {
		for (ArrayList<Coordinate> li : pathfinder.getPathsTried()) {
			for (int i = 0; i < li.size(); i++) {
				Coordinate c = li.get(i);
				Coordinate c2 = i == 0 ? root : li.get(i-1);
				pathMap.put(c, c2);
			}
		}
	}

	public Coordinate getNextCoordinateAlongPath(Coordinate c) {
		return pathMap.get(c);
	}

	@Override
	public Coordinate getNextWaypoint(Entity e) {
		return this.getNextCoordinateAlongPath(new Coordinate(e));
	}

	public boolean isDone() {
		return pathfinder.isDone();
	}

}
