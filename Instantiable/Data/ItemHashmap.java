/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemHashMap<V> {

	private final HashMap<ItemKey, V> data = new HashMap();

	public ItemHashMap() {

	}

	private V put(ItemKey is, V value) {
		return data.put(is, value);
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
		ArrayList li = new ArrayList();
		for (ItemKey key : data.keySet()) {
			li.add(key.asItemStack());
		}
		return li;
	}

	private static final class ItemKey {

		public final Item itemID;
		private final int metadata;

		private ItemKey(ItemStack is) {
			this.itemID = is.getItem();
			this.metadata = is.getItemDamage();
		}

		@Override
		public int hashCode() {
			return itemID.hashCode() + metadata << 24;
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
			return metadata >= 0;
		}

		public ItemStack asItemStack() {
			return new ItemStack(itemID, 1, metadata);
		}

	}

}
