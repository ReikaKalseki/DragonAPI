package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class AngleMap<V> {

	private final TreeMap<Double, V> data = new TreeMap();

	public AngleMap() {

	}

	public void put(double angle, V obj) {
		angle = (angle+360)%360;
		data.put(angle, obj);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public int size() {
		return data.size();
	}

	public Entry<Double, V> firstEntry() {
		return data.firstEntry();
	}

	public Entry<Double, V> floorEntry(double angle) {
		angle = (angle+360)%360;
		Entry<Double, V> e = data.floorEntry(angle);
		if (e == null)
			e = data.floorEntry(angle+360);
		return e;
	}

	public Entry<Double, V> ceilingEntry(double angle) {
		angle = (angle+360)%360;
		Entry<Double, V> e = data.ceilingEntry(angle);
		if (e == null)
			e = data.ceilingEntry(angle-360);
		//ReikaJavaLibrary.pConsole(e);
		return e;
	}

	public V get(double angle) {
		angle = (angle+360)%360;
		return data.get(angle);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public Set<Double> keySet() {
		return this.data.keySet();
	}

	public Collection<V> values() {
		return this.data.values();
	}

}
