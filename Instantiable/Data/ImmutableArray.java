/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

public class ImmutableArray<V> {

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
