/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import Reika.DragonAPI.Exception.MisuseException;

public final class CommutativePair<V> {

	public final V o1;
	public final V o2;

	public CommutativePair(V o1, V o2) {
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
