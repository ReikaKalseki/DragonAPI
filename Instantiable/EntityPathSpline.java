package Reika.DragonAPI.Instantiable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.CompoundPropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.DirectionalPropagation;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.LocationTerminus;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.TerminationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Interfaces.EntityPathfinder;


public class EntityPathSpline implements EntityPathfinder {

	private final HashMap<Integer, List<DecimalPosition>> paths = new HashMap();

	private final DecimalPosition target;

	public EntityPathSpline(TileEntity te) {
		this(new DecimalPosition(te));
	}

	public EntityPathSpline(double x, double y, double z) {
		this(new DecimalPosition(x, y, z));
	}

	public EntityPathSpline(DecimalPosition c) {
		target = c;
	}

	public void addEntity(Entity e) {
		if (this.isInRange(e))
			return;
		TerminationCondition t = new LocationTerminus(target.getCoordinate());
		LinkedList<Coordinate> li = Search.getPath(e.worldObj, e.posX, e.posY, e.posZ, t, new CompoundPropagationCondition().addCondition(new DirectionalPropagation(target.getCoordinate(), true)).addCondition(Search.PassablePropagation.instance));
		if (li != null) {
			Spline s1 = new Spline(SplineType.CENTRIPETAL);
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(e)));
			for (Coordinate c : li) {
				s1.addPoint(new BasicSplinePoint(new DecimalPosition(c)));
			}
			s1.addPoint(new BasicSplinePoint(target));
			List<DecimalPosition> li2 = s1.get(12, false);
			paths.put(e.getEntityId(), li2);
		}
	}

	public void clear() {
		paths.clear();
	}

	@Override
	public DecimalPosition getNextWaypoint(Entity e) {
		List<DecimalPosition> li = paths.get(e.getEntityId());
		return li != null && !li.isEmpty() ? li.remove(li.size()-1) : null;
	}

	@Override
	public boolean isInRange(Entity e) {
		List<DecimalPosition> li = paths.get(e.getEntityId());
		return li != null && !li.isEmpty();
	}

	public void removeDeadEntities(World world) {
		Iterator<Integer> it = paths.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			Entity e = world.getEntityByID(key);
			if (e == null || e.isDead)
				it.remove();
		}
	}

}
