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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerMap<V> {

	private final HashMap<UUID, V> data = new HashMap();

	private static UUID getKey(EntityPlayer ep) {
		return ep.getUniqueID();
	}

	public V get(EntityPlayer ep) {
		return data.get(getKey(ep));
	}

	public V put(EntityPlayer ep, V obj) {
		return data.put(getKey(ep), obj);
	}

	public V directPut(UUID uid, V obj) {
		return data.put(uid, obj);
	}

	public V directGet(UUID uid) {
		return data.get(uid);
	}

	public V directRemove(UUID uid) {
		return data.remove(uid);
	}

	public V remove(EntityPlayer ep) {
		return data.remove(getKey(ep));
	}

	public void clear() {
		data.clear();
	}

	public boolean containsKey(EntityPlayer ep) {
		return data.containsKey(getKey(ep));
	}

	public Collection<UUID> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public V get(String s) {
		return data.get(s);
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
