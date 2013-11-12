package Reika.DragonAPI.Instantiable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemHashmap extends HashMap {

	public Object put(ItemStack key, Object value) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		Object ret = this.get(li);
		this.put(li, value);
		return ret;
	}

	public Object get(ItemStack key) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		Object ret = this.get(li);
		return ret;
	}

	public boolean containsKey(ItemStack key) {
		List li = Arrays.asList(key.itemID, key.getItemDamage());
		return this.containsKey(li);
	}

}
