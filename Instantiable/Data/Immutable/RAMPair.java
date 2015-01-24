package Reika.DragonAPI.Instantiable.Data.Immutable;

import java.util.ArrayList;
import java.util.Collection;

public final class RAMPair {

	private final Collection<Object> objects = new ArrayList();
	private int hash = 0;

	public RAMPair(Object... o) {
		for (int i = 0; i < o.length; i++) {
			this.addObject(o[i]);
		}
	}

	public RAMPair(Collection c) {
		for (Object o : c) {
			this.addObject(o);
		}
	}

	private void addObject(Object o) {
		if (!objects.contains(o)) {
			objects.add(o);
			hash += o.hashCode();
		}
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RAMPair) {
			RAMPair r = (RAMPair)o;
			if (objects.size() != r.objects.size())
				return false;
			for (Object obj : objects) {
				if (!r.objects.contains(obj))
					return false;
			}
			return true;
		}
		return false;
	}

}
