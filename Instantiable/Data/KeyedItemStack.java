/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class KeyedItemStack implements Comparable<KeyedItemStack> {

	private final ItemStack item;
	private final boolean[] enabledCriteria = new boolean[Criteria.list.length];
	private boolean lock = false;
	private boolean simpleHash = false;

	public KeyedItemStack(Block b) {
		this(Item.getItemFromBlock(b));
	}

	public KeyedItemStack(Item i) {
		this(new ItemStack(i, 1, OreDictionary.WILDCARD_VALUE));
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

	public String getCriteriaAsChatFormatting() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < enabledCriteria.length; i++) {
			if (enabledCriteria[i])
				sb.append(Criteria.list[i].chatChar.toString());
		}
		return sb.toString();
	}

	public boolean hasMeta() {
		return enabledCriteria[Criteria.METADATA.ordinal()];
	}

	public boolean isSized() {
		return enabledCriteria[Criteria.SIZE.ordinal()];
	}

	public boolean hasNBT() {
		return enabledCriteria[Criteria.NBT.ordinal()];
	}

	private static enum Criteria {
		ID(true, EnumChatFormatting.RESET), //none
		METADATA(true, EnumChatFormatting.LIGHT_PURPLE),
		SIZE(false, EnumChatFormatting.BOLD),
		NBT(true, EnumChatFormatting.UNDERLINE);

		private final boolean defaultState;
		private final EnumChatFormatting chatChar;

		private static final Criteria[] list = values();

		private Criteria(boolean b, EnumChatFormatting f) {
			defaultState = b;
			chatChar = f;
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

	public static KeyedItemStack readFromNBT(NBTTagCompound nbt) {
		return new KeyedItemStack(ItemStack.loadItemStackFromNBT(nbt)).setIgnoreMetadata(nbt.getBoolean("ignoremeta")).setIgnoreNBT(nbt.getBoolean("ignorenbt")).setSized(nbt.getBoolean("sized")).setSimpleHash(nbt.getBoolean("simplehash"));
	}

	public void writeToNBT(NBTTagCompound nbt) {
		item.writeToNBT(nbt);
		nbt.setBoolean("sized", enabledCriteria[Criteria.SIZE.ordinal()]);
		nbt.setBoolean("ignorenbt", !enabledCriteria[Criteria.NBT.ordinal()]);
		nbt.setBoolean("ignoremeta", !enabledCriteria[Criteria.METADATA.ordinal()]);
		nbt.setBoolean("useID", enabledCriteria[Criteria.ID.ordinal()]);
		nbt.setBoolean("simplehash", simpleHash);
	}

	public boolean contains(KeyedItemStack ks) {
		if (!this.exactMatch(ks) && this.equals(ks)) {
			boolean flag = true;
			for (int i = 0; i < Criteria.list.length; i++) {
				Criteria c = Criteria.list[i];
				if (!enabledCriteria[i] && ks.enabledCriteria[i]) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(KeyedItemStack o) {
		return ReikaItemHelper.comparator.compare(item, o.item);
	}

	public String getDisplayName() {
		String base = item.getDisplayName();
		if (item.getItem() instanceof ItemEnchantedBook)
			base = base+": "+ReikaEnchantmentHelper.getEnchantmentsDisplay(item);
		return base;
	}

}
