package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.LocationTerminus;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.DepthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
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

	public void addEntity(Entity e, PropagationCondition propagation) {
		if (this.isInRange(e))
			return;
		LocationTerminus t = new LocationTerminus(target.getCoordinate());
		DepthFirstSearch s = new DepthFirstSearch(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ));
		s.limit = new BlockBox(t.target.xCoord, t.target.yCoord, t.target.zCoord, s.root.xCoord, s.root.yCoord, s.root.zCoord);
		s.limit = s.limit.expand(24);
		while (!s.tick(e.worldObj, propagation, t)) {

		}
		LinkedList<Coordinate> li = s.getResult().isEmpty() ? null : s.getResult();
		if (li != null) {
			ArrayList<Coordinate> lic = new ArrayList(li);
			boolean flag = true;
			while (flag) {
				int cut1 = -1;
				int cut2 = -1;
				flag = false;
				for (int i = 0; i < lic.size() && cut1 == -1 && cut2 == -1; i++) {
					Coordinate c1 = lic.get(i);
					for (int i2 = 0; i2 < lic.size() && cut1 == -1 && cut2 == -1; i2++) {
						if (Math.abs(i-i2) <= 1)
							continue;
						Coordinate c2 = lic.get(i2);
						if (c2.getTaxicabDistanceTo(c1) == 1) {
							cut1 = i;
							cut2 = i2;
						}
					}
				}
				if (cut1 >= 0 && cut2 >= 0) {
					if (cut2 < cut1) {
						int temp = cut1;
						cut1 = cut2;
						cut2 = temp;
					}
					//ReikaJavaLibrary.pConsole("Cutting "+cut1+" -> "+cut2+" from list of size "+lic.size()+" due to neighbors "+lic.get(cut1)+", "+lic.get(cut2));
					flag = true;
					ArrayList<Coordinate> lic2 = new ArrayList();
					for (int i = 0; i < lic.size(); i++) {
						if (i > cut1 && i < cut2)
							continue;
						lic2.add(lic.get(i));
					}
					lic.clear();
					lic.addAll(lic2);
				}
			}
			//for (Coordinate c : lic) {
			//		TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new ScheduledBlockPlace(e.worldObj, c.xCoord, c.yCoord, c.zCoord, Blocks.stone)), 120+lic.indexOf(c)*4);
			//}
			Spline s1 = new Spline(SplineType.CENTRIPETAL);
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(e)));
			for (Coordinate c : lic) {
				s1.addPoint(new BasicSplinePoint(new DecimalPosition(c)));
			}
			s1.addPoint(new BasicSplinePoint(target));
			List<DecimalPosition> li2 = s1.get(12, false);
			Collections.reverse(li2);
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
