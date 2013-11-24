/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemHashmap<V> extends HashMap {

	public V put(ItemStack key, V value) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		V ret = (V) this.get(li);
		this.put(li, value);
		return ret;
	}

	public V get(ItemStack key) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		V ret = (V) this.get(li);
		return ret;
	}

	public boolean containsKey(ItemStack key) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		return this.containsKey(li);
	}

}
