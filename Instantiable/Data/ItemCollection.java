package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCollection {

	private final ItemHashMap<ArrayList<InventorySlot>> data = new ItemHashMap();

	public ItemCollection() {

	}

	public void addInventory(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			this.addSlot(new InventorySlot(i, ii));
		}
	}

	public void addSlot(InventorySlot slot) {
		ItemStack is = slot.getStack();
		if (is != null) {
			this.addItemToData(is, slot);
		}
	}

	private void addItemToData(ItemStack is, InventorySlot slot) {
		int has = this.getItemCount(is);
		ArrayList<InventorySlot> li = data.get(is);
		if (li == null) {
			li = new ArrayList();
			data.put(is, li);
		}
		li.add(slot);
	}

	public boolean hasItem(ItemStack is) {
		return data.containsKey(is);
	}

	public int getItemCount(ItemStack is) {
		ArrayList<InventorySlot> li = data.get(is);
		if (li != null) {
			int count = 0;
			for (InventorySlot slot : li) {
				count += slot.getStackSize();
			}
			return count;
		}
		else {
			return 0;
		}
	}

	public int getItemCountWithOreEquivalence(ItemStack is) {
		int[] ids = OreDictionary.getOreIDs(is);
		if (ids.length == 0) {
			return this.getItemCount(is);
		}
		else {
			Collection<ItemStack> valid = new ArrayList();
			String name = OreDictionary.getOreName(ids[0]);
			List<ItemStack> li = OreDictionary.getOres(name);
			for (ItemStack ore : li) {
				int[] ids2 = OreDictionary.getOreIDs(ore);
				if (Arrays.equals(ids, ids2))
					valid.add(ore);
			}
			int count = 0;
			for (ItemStack c : valid) {
				count += this.getItemCount(c);
			}
			//ReikaJavaLibrary.pConsole(count+"/"+data.toString());
			return count;
		}
	}

	public boolean removeXItems(ItemStack is, int amt) {
		ArrayList<InventorySlot> li = data.get(is);
		if (li != null) {
			Iterator<InventorySlot> it = li.iterator();
			while (it.hasNext()) {
				InventorySlot slot = it.next();
				amt -= slot.decrement(amt);
				if (slot.isEmpty())
					it.remove();
				if (amt <= 0)
					return true;
			}
		}
		return false;
	}

	public void clear() {
		data.clear();
	}

}
