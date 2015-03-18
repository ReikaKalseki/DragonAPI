/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;

public class MESystemReader {

	private final IGridNode node;
	private final BaseActionSource actionSource;

	public MESystemReader(IGridNode ign, SourceType src) {
		node = ign;
		actionSource = new ActionSource(src);
	}

	private IMEMonitor<IAEItemStack> getStorage() {
		return ((IStorageGrid)node.getGrid().getCache(IStorageGrid.class)).getItemInventory();
	}

	public ItemHashMap<Long> getMESystemContents() {
		ItemHashMap<Long> map = new ItemHashMap();
		if (node == null || node.getGrid() == null || this.getStorage() == null) {
			/*
			StringBuilder sb = new StringBuilder();
			sb.append(node+"/");
			if (node != null) {
				sb.append(node.getGrid());
				if (node.getGrid() != null) {
					sb.append(node.getGrid().getCache(IStorageGrid.class)+"/");
					if (node.getGrid().getCache(IStorageGrid.class) != null) {
						sb.append(((IStorageGrid)node.getGrid().getCache(IStorageGrid.class)).getItemInventory());
					}
					else {
						sb.append("/[No storage grid cache]");
					}
				}
				else {
					sb.append("/[No grid]");
				}
			}
			else {
				sb.append("/[No node]");
			}
			ReikaJavaLibrary.pConsole("No grid: "+sb.toString());*/
			return map;
		}
		for (IAEItemStack iae : this.getStorage().getStorageList()) {
			if (iae.isItem() && iae.isMeaningful()) {
				map.put(iae.getItemStack(), iae.getStackSize());
			}
		}
		return map;
	}

	/** Returns how many items removed */
	public long removeItem(ItemStack is, boolean simulate) {
		IAEItemStack ret = this.getStorage().extractItems(AEItemStack.create(is), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
		return ret != null ? ret.getStackSize() : 0;
	}

	/** Returns how many items NOT added */
	public long addItem(ItemStack is, boolean simulate) {
		IAEItemStack ret = this.getStorage().injectItems(AEItemStack.create(is), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
		return ret != null ? ret.getStackSize() : 0;
	}

	private class ActionSource extends BaseActionSource {

		private final SourceType type;

		private ActionSource(SourceType s) {
			type = s;
		}

		@Override
		public boolean isPlayer()
		{
			return type == SourceType.PLAYER;
		}

		@Override
		public boolean isMachine()
		{
			return type == SourceType.MACHINE;
		}

	}

	public static enum SourceType {

		MACHINE(),
		PLAYER();
	}
	/*
	private static class AEStack implements IAEItemStack, Comparable<AEStack> {

		private final ItemStack item;
		private long size;

		private AEStack(ItemStack is) {
			this(is, is.stackSize);
		}

		private AEStack(ItemStack is, long s) {
			item = is;
			size = s;
		}

		@Override
		public long getStackSize() {
			return size;
		}

		@Override
		public IAEItemStack setStackSize(long stackSize) {
			size = stackSize;
			return this;
		}

		@Override
		public long getCountRequestable() {
			return 0;
		}

		@Override
		public IAEItemStack setCountRequestable(long countRequestable) {
			return null;
		}

		@Override
		public boolean isCraftable() {
			return false;
		}

		@Override
		public IAEItemStack setCraftable(boolean isCraftable) {
			return this;
		}

		@Override
		public IAEItemStack reset() {
			return this;
		}

		@Override
		public boolean isMeaningful() {
			return true;
		}

		@Override
		public void incStackSize(long i) {
			size += i;
		}

		@Override
		public void decStackSize(long i) {
			size -= i;
		}

		@Override
		public void incCountRequestable(long i) {

		}

		@Override
		public void decCountRequestable(long i) {

		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			item.writeToNBT(tag);
			tag.setLong("fullsize", size);
		}

		@Override
		public boolean fuzzyComparison(Object st, FuzzyMode mode) {
			return false;
		}

		@Override
		public void writeToPacket(ByteBuf data) throws IOException {
			data.writeInt(Item.getIdFromItem(this.getItem()));
			data.writeInt(item.getItemDamage());
			data.writeLong(size);
			if (item.stackTagCompound != null) {
				data.writeBytes(CompressedStreamTools.compress(item.stackTagCompound));
			}
		}

		@Override
		public IAEItemStack empty() {
			return this;
		}

		@Override
		public IAETagCompound getTagCompound() {
			return null;
		}

		@Override
		public boolean isItem() {
			return true;
		}

		@Override
		public boolean isFluid() {
			return false;
		}

		@Override
		public StorageChannel getChannel() {
			return StorageChannel.ITEMS;
		}

		@Override
		public int compareTo(AEStack o) {
			return Item.getIdFromItem(this.getItem())-Item.getIdFromItem(o.getItem());
		}

		@Override
		public ItemStack getItemStack() {
			return item.copy();
		}

		@Override
		public IAEItemStack copy() {
			return new AEStack(item, size);
		}

		@Override
		public boolean hasTagCompound() {
			return item.stackTagCompound != null;
		}

		@Override
		public void add(IAEItemStack option) {
			size += option.getStackSize();
		}

		@Override
		public Item getItem() {
			return item.getItem();
		}

		@Override
		public int getItemDamage() {
			return item.getItemDamage();
		}

		@Override
		public boolean sameOre(IAEItemStack is) {
			return OreDictionary.getOreID(item) == OreDictionary.getOreID(is.getItemStack());
		}

		@Override
		public boolean isSameType(IAEItemStack otherStack) {
			return ReikaItemHelper.matchStacks(otherStack.getItemStack(), item);
		}

		@Override
		public boolean isSameType(ItemStack stored) {
			return ReikaItemHelper.matchStacks(stored, item);
		}

	}*/
}
