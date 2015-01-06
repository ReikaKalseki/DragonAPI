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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class ItemHashMap<V> {

	private final HashMap<ItemKey, V> data = new HashMap();
	private ArrayList<ItemStack> sorted = null;
	private Collection<ItemStack> keyset = null;
	private boolean modifiedKeys = true;

	public ItemHashMap() {

	}

	private void updateKeysets() {
		this.modifiedKeys = false;
		this.keyset = this.createKeySet();
		sorted = new ArrayList(this.keySet());
		ReikaItemHelper.sortItems(sorted);
	}

	private V put(ItemKey is, V value) {
		V ret = data.put(is, value);
		this.modifiedKeys = true;
		return ret;
	}

	private V get(ItemKey is) {
		return data.get(is);
	}

	private boolean containsKey(ItemKey is) {
		return data.containsKey(is);
	}

	public V put(ItemStack is, V value) {
		return this.put(new ItemKey(is), value);
	}

	public V get(ItemStack is) {
		return this.get(new ItemKey(is));
	}

	public boolean containsKey(ItemStack is) {
		return this.containsKey(new ItemKey(is));
	}

	public V put(Item i, int meta, V value) {
		return this.put(new ItemStack(i, meta), value);
	}

	public V get(Item i, int meta) {
		return this.get(new ItemStack(i, meta));
	}

	public boolean containsKey(Item i, int meta) {
		return this.containsKey(new ItemStack(i, meta));
	}

	public V put(Block b, int meta, V value) {
		return this.put(new ItemStack(b, meta), value);
	}

	public V get(Block b, int meta) {
		return this.get(new ItemStack(b, meta));
	}

	public boolean containsKey(Block b, int meta) {
		return this.containsKey(new ItemStack(b, meta));
	}

	public int size() {
		return data.size();
	}

	public Collection<ItemStack> keySet() {
		if (this.modifiedKeys || keyset == null) {
			this.updateKeysets();
		}
		return Collections.unmodifiableCollection(keyset);
	}

	private Collection<ItemStack> createKeySet() {
		ArrayList li = new ArrayList();
		for (ItemKey key : data.keySet()) {
			li.add(key.asItemStack());
		}
		return li;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public V remove(ItemStack is) {
		return this.remove(new ItemKey(is));
	}

	private V remove(ItemKey is) {
		V ret = data.remove(is);
		this.modifiedKeys = true;
		return ret;
	}

	public void clear() {
		data.clear();
		this.modifiedKeys = true;
	}

	public List<ItemStack> sortedKeyset() {
		if (this.modifiedKeys || this.sorted == null) {
			this.updateKeysets();
		}
		return Collections.unmodifiableList(sorted);
	}

	private static final class ItemKey implements Comparable<ItemKey> {

		public final Item itemID;
		private final int metadata;

		private ItemKey(ItemStack is) {
			this.itemID = is.getItem();
			this.metadata = is.getItemDamage();
		}

		@Override
		public int hashCode() {
			return itemID.hashCode()/* + metadata << 24*/;
		}

		@Override
		public boolean equals(Object o) {
			//ReikaJavaLibrary.pConsole(this+" & "+o);
			if (o instanceof ItemKey) {
				ItemKey i = (ItemKey)o;
				return i.itemID == itemID && (!this.hasMetadata() || !i.hasMetadata() || i.metadata == metadata);
			}
			return false;
		}

		@Override
		public String toString() {
			return itemID.getUnlocalizedName()+":"+metadata;
		}

		public boolean hasMetadata() {
			return metadata >= 0 && metadata != OreDictionary.WILDCARD_VALUE;
		}

		public ItemStack asItemStack() {
			return new ItemStack(itemID, 1, metadata);
		}

		@Override
		public int compareTo(ItemKey o) {
			return Item.getIdFromItem(itemID)-Item.getIdFromItem(o.itemID);
		}

	}

	public static ItemHashMap<Integer> getFromInventory(IInventory ii) {
		ItemHashMap<Integer> map = new ItemHashMap();
		int s = ii.getSizeInventory();
		for (int i = 0; i < s; i++) {
			ItemStack in = ii.getStackInSlot(i);
			Integer has = map.get(in);
			int amt = has != null ? has.intValue() : 0;
			map.put(in, has+1);
		}
		return map;
	}

}
