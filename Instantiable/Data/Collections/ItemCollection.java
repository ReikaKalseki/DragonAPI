/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.InventorySlot;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.ModRegistry.InterfaceCache;

public class ItemCollection {

	private final ItemHashMap<Collection<InventorySlot>> data = new ItemHashMap().enableNBT();
	private final Collection<IInventory> inventories = new HashSet();

	private final ItemHashMap<Collection<IDeepStorageUnit>> dsus = new ItemHashMap().enableNBT();

	public ItemCollection() {

	}

	public ItemCollection addInventory(IInventory ii) {
		if (InterfaceCache.DSU.instanceOf(ii)) {
			this.addDSU((IDeepStorageUnit)ii);
		}
		else {
			for (int i = 0; i < ii.getSizeInventory(); i++) {
				this.addSlot(new InventorySlot(i, ii));
			}
		}
		inventories.add(ii);
		return this;
	}

	@ClassDependent("powercrystals.minefactoryreloaded.api.IDeepStorageUnit")
	private void addDSU(IDeepStorageUnit dsu) {
		ItemStack is = dsu.getStoredItemType();
		if (is != null) { //ignore empty DSUs, and ItemHashMap cannot take null key
			Collection<IDeepStorageUnit> c = dsus.get(is);
			if (c == null) {
				c = new ArrayList();
				dsus.put(is, c);
			}
			c.add(dsu);
		}
	}

	public ItemCollection addSlot(InventorySlot slot) {
		ItemStack is = slot.getStack();
		if (is != null) {
			this.addItemToData(is, slot);
		}
		inventories.add(slot.inventory);
		return this;
	}

	private void addItemToData(ItemStack is, InventorySlot slot) {
		int has = this.getItemCount(is);
		Collection<InventorySlot> li = data.get(is);
		if (li == null) {
			li = new ArrayList();
			data.put(is, li);
		}
		li.add(slot);
	}

	public boolean hasItem(ItemStack is) {
		return data.containsKey(is) || dsus.containsKey(is);
	}

	/** Returns how many items left over. */
	public int addItemsToUnderlyingInventories(ItemStack is, boolean simulate) {
		this.validateInventories();
		int left = is.stackSize;
		for (IInventory ii : inventories) {
			left = ReikaInventoryHelper.addToInventoryWithLeftover(is, ii, simulate);
			if (!simulate)
				is.stackSize = left;
			if (left <= 0)
				return 0;
		}
		return left;
	}

	public int getItemCount(ItemStack is) {
		int count = 0;
		Collection<InventorySlot> li = data.get(is);
		if (li != null) {
			for (InventorySlot slot : li) {
				if (is.stackTagCompound == null || is.stackTagCompound.equals(slot.getStack().stackTagCompound))
					count += slot.getStackSize();
			}
		}
		Collection<IDeepStorageUnit> li2 = dsus.get(is);
		if (li2 != null) {
			for (IDeepStorageUnit dsu : li2) {
				if (is.stackTagCompound == null || is.stackTagCompound.equals(dsu.getStoredItemType().stackTagCompound))
					count += dsu.getStoredItemType().stackSize;
			}
		}
		return count;
	}
	/*
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
	 */
	public int removeXItems(ItemStack is, int amt) {
		this.validateInventories();
		Collection<InventorySlot> li = data.get(is);
		int rem = 0;
		if (li != null) {
			Iterator<InventorySlot> it = li.iterator();
			while (it.hasNext()) {
				InventorySlot slot = it.next();
				if (is.stackTagCompound == null || is.stackTagCompound.equals(slot.getStack().stackTagCompound)) {
					int dec = slot.decrement(amt);
					rem += dec;
					amt -= dec;
					if (slot.isEmpty())
						it.remove();
					if (amt <= 0)
						return rem;
				}
			}
		}
		Collection<IDeepStorageUnit> li2 = dsus.get(is);
		if (li2 != null) {
			Iterator<IDeepStorageUnit> it = li2.iterator();
			while (it.hasNext()) {
				IDeepStorageUnit dsu = it.next();
				if (is.stackTagCompound == null || is.stackTagCompound.equals(dsu.getStoredItemType().stackTagCompound)) {
					int has = dsu.getStoredItemType().stackSize;
					int dec = Math.min(amt, has);
					rem += dec;
					amt -= dec;
					dsu.setStoredItemCount(has-dec);
					if (dsu.getStoredItemType() == null || dsu.getStoredItemType().stackSize == 0)
						it.remove();
					if (amt <= 0)
						return rem;
				}
			}
		}
		return rem;
	}

	private void validateInventories() {
		Iterator<IInventory> it = inventories.iterator();
		while (it.hasNext()) {
			IInventory ii = it.next();
			if (ii instanceof TileEntity) {
				if (((TileEntity)ii).isInvalid()) {
					it.remove();
					for (Collection<InventorySlot> c2 : data.values()) {
						Iterator<InventorySlot> it2 = c2.iterator();
						while (it2.hasNext()) {
							InventorySlot s = it2.next();
							if (s.inventory == ii) {
								it2.remove();
							}
						}
					}
				}
			}
		}
	}

	public void clear() {
		data.clear();
		inventories.clear();
		dsus.clear();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	private static abstract class InventoryInterface {

		private final IInventory inventory;

		private InventoryInterface(IInventory ii) {
			inventory = ii;
		}

		protected abstract void removeItem(ItemStack is, int amt);
		protected abstract void addItem(ItemStack is, int amt);
		protected abstract int countItem(ItemStack is);

	}

}
