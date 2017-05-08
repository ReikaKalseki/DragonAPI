package Reika.DragonAPI.Instantiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.Ray;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;


public class Lattice {

	private final HashSet<Coordinate> originPoints = new HashSet();
	private final MultiMap<Coordinate, Ray> rays = new MultiMap(new HashSetFactory());

	private final BlockBox bounds;

	public int pointCount;
	public int rayCount;

	public Lattice(int x1, int y1, int z1, int x2, int y2, int z2) {
		bounds = new BlockBox(x1, y1, z1, x2, y2, z2);
	}

	public Lattice addPoint(Coordinate c) {
		originPoints.add(c);
		return this;
	}

	public Lattice generatePoints(Random rand) {
		if (pointCount > bounds.getVolume())
			throw new IllegalArgumentException("Volume is too small for "+pointCount+" points!");
		for (int i = 0; i < pointCount; i++) {
			Coordinate c = bounds.getRandomContainedCoordinate(rand);
			while (originPoints.contains(c)) {
				c = bounds.getRandomContainedCoordinate(rand);
			}
			originPoints.add(c);
		}
		return this;
	}

	public Lattice generateRays(Random rand) {
		for (Coordinate c : originPoints) {
			for (int i = 0; i < rayCount; i++) {
				Ray r = Reika.DragonAPI.Instantiable.Data.Immutable.Ray.fromPolar(new DecimalPosition(c), rand.nextDouble()*360, rand.nextDouble()*360);
				rays.addValue(c, r);
			}
		}
		return this;
	}

	public Collection<Ray> getAllRays() {
		return Collections.unmodifiableCollection(rays.allValues(false));
	}

	public static class LatticeCache {

		private final HashSet<Coordinate> locations = new HashSet();

		public LatticeCache(Lattice c, int th) {
			for (Ray r : c.getAllRays()) {
				this.generateFromRay(c, r, th);
			}
		}

		private void generateFromRay(Lattice c, Ray r, int th) {
			boolean flag = true;
			double d = 0;
			while (flag) {
				flag = false;
				Coordinate p = r.getScaledPosition(d).getCoordinate();
				if (c.bounds.isBlockInside(p)) {
					this.generateAt(p, th);
					d += 0.25;
					flag = true;
				}
			}
			flag = true;
			d = 0;
			while (flag) {
				flag = false;
				Coordinate p = r.getScaledPosition(d).getCoordinate();
				if (c.bounds.isBlockInside(p)) {
					this.generateAt(p, th);
					d -= 0.25;
					flag = true;
				}
			}
		}

		private void generateAt(Coordinate p, int th) {
			locations.add(p);
			th = th-1;
			if (th == 1) {
				for (int i = 0; i < 6; i++)
					locations.add(p.offset(ForgeDirection.VALID_DIRECTIONS[i], 1));
			}
			else {
				th = th-1;
				for (int i = -th; i <= th; i++) {
					for (int j = -th; j <= th; j++) {
						for (int k = -th; k <= th; k++) {
							locations.add(p.offset(i, j, k));
						}
					}
				}
			}
		}

		public HashSet<Coordinate> getLocations() {
			return new HashSet(locations);
		}

	}

}
