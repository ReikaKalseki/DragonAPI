/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.immutable;

public final class ImmutableArray<V> {

	private final V[] data;
	public final int length;

	public ImmutableArray(V[] arr) {
		data = arr;
		this.length = arr.length;
	}

	public V get(int i) {
		return data[i];
	}

}
