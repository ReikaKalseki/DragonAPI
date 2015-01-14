/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerMap<V> {

	private final HashMap<String, V> data = new HashMap();

	private static String getKey(EntityPlayer ep) {
		return ep.getCommandSenderName(); //switch to uuid in 1.8
	}

	public V get(EntityPlayer ep) {
		return data.get(getKey(ep));
	}

	public V put(EntityPlayer ep, V obj) {
		return data.put(getKey(ep), obj);
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

	public Collection<String> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public V get(String s) {
		return data.get(s);
	}

}
