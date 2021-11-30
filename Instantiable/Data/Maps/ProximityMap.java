package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.HashMap;
import java.util.HashSet;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

public class ProximityMap {

	private final HashMap<Coordinate, Section> data = new HashMap();
	private final int maxPerSection;
	private final int sectionSize;

	public boolean checkYCoord = false;

	public ProximityMap(int step, int max) {
		sectionSize = step;
		maxPerSection = max;
	}

	public boolean add(Coordinate c) {
		Section s = this.getSection(c, true);
		return s.data.size() < maxPerSection && s.data.add(c);
	}

	public boolean remove(Coordinate c) {
		Section s = this.getSection(c, false);
		if (s == null)
			return false;
		if (s.data.remove(c)) {
			if (s.data.isEmpty()) {
				data.remove(s.key);
			}
			return true;
		}
		return false;
	}

	public boolean contains(Coordinate c) {
		Section s = this.getSection(c, false);
		return s != null && s.data.contains(c);
	}

	public HashSet<Coordinate> getLocations() {
		HashSet<Coordinate> set = new HashSet();
		for (Section s : data.values()) {
			set.addAll(s.data);
		}
		return set;
	}

	public int getCountInSection(Coordinate c) {
		Section s = this.getSection(c, false);
		return s != null ? s.data.size() : 0;
	}

	private Section getSection(Coordinate c, boolean create) {
		int x = ReikaMathLibrary.roundDownToX(sectionSize, c.xCoord);
		int z = ReikaMathLibrary.roundDownToX(sectionSize, c.zCoord);
		int y = checkYCoord ? ReikaMathLibrary.roundDownToX(sectionSize, c.yCoord) : 0;
		Coordinate p = new Coordinate(x, y, z);
		Section s = data.get(p);
		if (create && s == null) {
			s = new Section(p);
			data.put(p, s);
		}
		return s;
	}

	public void clear() {
		data.clear();
	}

	private static class Section {

		private final Coordinate key;
		private final HashSet<Coordinate> data = new HashSet();

		private Section(Coordinate p) {
			key = p;
		}
	}

}
