/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.maps;


public final class PairMap<V> {

	private final MixMap<V, Boolean> data = new MixMap();

	public PairMap() {

	}

	public void add(V obj, V obj2) {
		data.addMix(obj, obj2, true);
	}

	public boolean contains(V obj, V obj2) {
		return data.containsKey(obj) && Boolean.TRUE.equals(data.getMix(obj, obj2));
	}

}
