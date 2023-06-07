package Reika.DragonAPI.Instantiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.BreadthFirstSearch;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.OpenPathFinder.PassRules;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;

public class ParticlePath {

	private static final PluralMap<ParticlePath> pathCache = new PluralMap(2);

	public final Coordinate source;
	public final Coordinate target;
	private final List<DecimalPosition> path;
	public final Spline spline;
	private final HashSet<Coordinate> coords;

	private ParticlePath(Coordinate c1, Coordinate c2, Spline s, HashSet<Coordinate> set) {
		source = c1;
		target = c2;
		spline = s;
		path = Collections.unmodifiableList(s.get(12, false));
		coords = set;
		for (DecimalPosition p : path) {
			coords.add(new Coordinate(p.xCoord, p.yCoord, p.zCoord));
		}
	}

	public boolean isValid(World world) {
		for (Coordinate c : coords) {
			if (c.equals(source) || c.equals(target))
				continue;
			if (!c.getBlock(world).isAir(world, c.xCoord, c.yCoord, c.zCoord))
				return false;
		}
		return true;
	}

	public List<DecimalPosition> getPath() {
		return Collections.unmodifiableList(path);
	}

	public static ParticlePath getPath(World world, BlockVector from, BlockVector to, double offset, double forceDirection) {
		ParticlePath p = pathCache.get(from, to);
		if (p == null || !p.isValid(world)) {
			p = calculateParticlePath(world, from, to, offset, forceDirection);
			pathCache.put(p, from, to);
		}
		return p;
	}

	private static ParticlePath calculateParticlePath(World world, BlockVector from, BlockVector to, double endOffset, double forceDirection) {
		LinkedList<Coordinate> li = BreadthFirstSearch.getOpenPathBetween(world, from.getCoord(), to.getCoord(), 24, EnumSet.of(PassRules.SOFT, PassRules.SMALLNONSOLID)).getPath();
		if (li != null) {
			HashSet<Coordinate> set = new HashSet();

			double sx = from.xCoord+0.5;
			double sy = from.yCoord+0.5;
			double sz = from.zCoord+0.5;
			double tx = to.xCoord+0.5;
			double ty = to.yCoord+0.5;
			double tz = to.zCoord+0.5;

			sx -= from.direction.offsetX*endOffset;
			sy -= from.direction.offsetY*endOffset;
			sz -= from.direction.offsetZ*endOffset;
			tx -= to.direction.offsetX*endOffset;
			ty -= to.direction.offsetY*endOffset;
			tz -= to.direction.offsetZ*endOffset;

			double sx2 = sx+from.direction.offsetX*forceDirection;
			double sy2 = sy+from.direction.offsetY*forceDirection;
			double sz2 = sz+from.direction.offsetZ*forceDirection;
			double tx2 = tx+to.direction.offsetX*forceDirection;
			double ty2 = ty+to.direction.offsetY*forceDirection;
			double tz2 = tz+to.direction.offsetZ*forceDirection;

			Spline s1 = new Spline(SplineType.CENTRIPETAL);
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(sx, sy, sz)));
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(sx2, sy2, sz2)));
			int i = -1;
			for (Coordinate c : li) {
				if (i%4 == 0 && c != li.getLast()) {
					if (!c.equals(to) && !c.equals(from))
						set.add(c);
					s1.addPoint(new BasicSplinePoint(new DecimalPosition(c)));
				}
				i++;
			}
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(tx2, ty2, tz2)));
			s1.addPoint(new BasicSplinePoint(new DecimalPosition(tx, ty, tz)));

			ParticlePath p = new ParticlePath(from.getCoord(), to.getCoord(), s1, set);
			return p;
		}
		return null;
	}

}
