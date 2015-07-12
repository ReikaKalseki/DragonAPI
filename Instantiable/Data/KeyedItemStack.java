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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Exception.MisuseException;

public final class KeyedItemStack {

	private final ItemStack item;
	private final boolean[] enabledCriteria = new boolean[Criteria.list.length];
	private boolean lock = false;
	private boolean simpleHash = false;

	public KeyedItemStack(Block b) {
		this(Item.getItemFromBlock(b));
	}

	public KeyedItemStack(Item i) {
		this(new ItemStack(i));
		this.setIgnoreMetadata(true);
		this.setSized(false);
		this.setIgnoreNBT(true);
		this.setSimpleHash(true);
	}

	public KeyedItemStack(ItemStack is) {
		if (is == null || is.getItem() == null)
			throw new MisuseException("You cannot key a null itemstack!");
		item = is.copy();
		for (int i = 0; i < enabledCriteria.length; i++)
			enabledCriteria[i] = Criteria.list[i].defaultState;
	}

	public KeyedItemStack setSized(boolean size) {
		if (!lock)
			enabledCriteria[Criteria.SIZE.ordinal()] = size;
		return this;
	}

	public KeyedItemStack setIgnoreMetadata(boolean ignore) {
		if (!lock)
			enabledCriteria[Criteria.METADATA.ordinal()] = !ignore;
		return this;
	}

	public KeyedItemStack setIgnoreNBT(boolean ignore) {
		if (!lock)
			enabledCriteria[Criteria.NBT.ordinal()] = !ignore;
		return this;
	}

	public KeyedItemStack setSimpleHash(boolean flag) {
		if (!lock)
			simpleHash = flag;
		return this;
	}

	public KeyedItemStack lock() {
		lock = true;
		return this;
	}

	@Override
	public final int hashCode() {
		if (simpleHash)
			return item.getItem().hashCode();
		int hash = 0;
		for (int i = 0; i < Criteria.list.length; i++) {
			Criteria c = Criteria.list[i];
			if (enabledCriteria[i])
				hash += c.hash(this) << i;
		}
		return hash;
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof KeyedItemStack) {
			KeyedItemStack ks = (KeyedItemStack)o;
			return this.match(ks, false);
		}
		return false;
	}

	public boolean exactMatch(KeyedItemStack ks) {
		return this.match(ks, true);
	}

	private boolean match(KeyedItemStack ks, boolean force) {
		for (int i = 0; i < Criteria.list.length; i++) {
			Criteria c = Criteria.list[i];
			if ((force || (enabledCriteria[i] && ks.enabledCriteria[i])) && !c.match(this, ks))
				return false;
		}
		return true;
	}

	public boolean match(ItemStack is) {
		KeyedItemStack ks = new KeyedItemStack(is);
		ks.setSimpleHash(simpleHash);
		for (int i = 0; i < Criteria.list.length; i++) {
			ks.enabledCriteria[i] = enabledCriteria[i];
		}
		return this.equals(ks);
	}

	public ItemStack getItemStack() {
		return item.copy();
	}

	@Override
	public String toString() {
		return item.toString()+"|"+this.getCriteriaFlags();
	}

	private String getCriteriaFlags() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < enabledCriteria.length; i++) {
			sb.append(enabledCriteria[i] ? "1" : "0");
		}
		return sb.toString();
	}

	public KeyedItemStack copy() {
		KeyedItemStack ks = new KeyedItemStack(item.copy());
		ks.setSimpleHash(simpleHash);
		for (int i = 0; i < Criteria.list.length; i++) {
			ks.enabledCriteria[i] = enabledCriteria[i];
		}
		ks.lock = lock;
		return ks;
	}

	private static enum Criteria {
		ID(true),
		METADATA(true),
		SIZE(false),
		NBT(true);

		private final boolean defaultState;

		private static final Criteria[] list = values();

		private Criteria(boolean b) {
			defaultState = b;
		}

		public int hash(KeyedItemStack ks) {
			switch(this) {
			case ID:
				return ks.item.getItem().hashCode();
			case METADATA:
				return ks.item.getItemDamage();
			case SIZE:
				return ks.item.stackSize;
			case NBT:
				return ks.item.stackTagCompound != null ? ks.item.stackTagCompound.hashCode() : -1;
			default:
				return 0;
			}
		}

		private boolean match(KeyedItemStack k1, KeyedItemStack k2) {
			switch(this) {
			case ID:
				return k1.item.getItem() == k2.item.getItem();
			case METADATA:
				if (k1.item.getItem().getHasSubtypes() || k2.item.getItem().getHasSubtypes()) {
					int m1 = k1.item.getItemDamage();
					int m2 = k2.item.getItemDamage();
					return m1 == m2 || m1 == OreDictionary.WILDCARD_VALUE || m2 == OreDictionary.WILDCARD_VALUE;
				}
				else
					return true;
			case SIZE:
				return k1.item.stackSize == k2.item.stackSize;
			case NBT:
				return ItemStack.areItemStackTagsEqual(k1.item, k2.item);
			}
			return false;
		}
	}

}
