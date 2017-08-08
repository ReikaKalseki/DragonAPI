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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class PlayerTimer {

	private final PlayerMap<Integer> data = new PlayerMap();

	public final void tick(World world) {
		if (!data.isEmpty()) {
			Iterator<Entry<UUID, Integer>> it = data.iterator();
			while (it.hasNext()) {
				Entry<UUID, Integer> e = it.next();
				UUID uid = e.getKey();
				EntityPlayer ep = world.func_152378_a(uid);
				if (ep != null && this.shouldTickPlayer(ep)) {
					int time = e.getValue();
					if (time > 1) {
						e.setValue(time-1);
					}
					else {
						it.remove();
					}
				}
			}
		}
	}

	protected boolean shouldTickPlayer(EntityPlayer ep) {
		return true;
	}

	public final void clear() {
		data.clear();
	}

	public final boolean containsKey(EntityPlayer ep) {
		return data.containsKey(ep);
	}

	public final int get(EntityPlayer ep) {
		Integer get = data.get(ep);
		return get != null ? get.intValue() : 0;
	}

	public final void put(EntityPlayer ep, int time) {
		data.put(ep, time);
	}

}
