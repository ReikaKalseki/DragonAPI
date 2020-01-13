/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Iterator<Entry<UUID, V>> iterator() {
		return this.data.entrySet().iterator();
	}

	public Collection<Entry<UUID, V>> entrySet() {
		return Collections.unmodifiableCollection(data.entrySet());
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(data.values());
	}

}
