package Reika.DragonAPI.Instantiable.Data.Immutable;

import Reika.DragonAPI.Exception.MisuseException;

public final class CommutativePair {

	public final Object o1;
	public final Object o2;

	public CommutativePair(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			throw new MisuseException("You cannot create a pair with null!");
		this.o1 = o1;
		this.o2 = o2;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CommutativePair) {
			CommutativePair b = (CommutativePair)o;
			if (b.o1.equals(o1) && b.o2.equals(o2))
				return true;
			if (b.o1.equals(o2) && b.o2.equals(o1)) //reverse
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return o1.hashCode()+o2.hashCode(); //commutative
	}

}
