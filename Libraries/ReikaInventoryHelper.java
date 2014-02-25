/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class ReikaInventoryHelper extends DragonAPICore {

	/** Checks an itemstack array (eg an inventory) for an item of a specific id.
	 * Returns true if found. Args: Item ID, Inventory */
	public static boolean checkForItem(int id, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id)
					return true;
			}
		}
		return false;
	}

	public static boolean checkForItem(int id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null) {
				if (ii.getStackInSlot(i).itemID == id)
					return true;
			}
		}
		return false;
	}

	/** Checks an itemstack array (eg an inventory) for an itemstack,
	 * defined by id, size, metadata. Returns true if found.
	 * Args: Item ID, Metadata, StackSize, Inventory */
	public static boolean checkForItemStack(int id, int dmg, int num, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id && inv[i].getItemDamage() == dmg && inv[i].stackSize == num)
					return true;
			}
		}
		return false;
	}

	/** Checks an itemstack array (eg an inventory) for an itemstack,
	 * defined by id, and metadata. Returns true if found.
	 * Args: Item ID, Metadata, Inventory */
	public static boolean checkForItemStack(int id, int dmg, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id && inv[i].getItemDamage() == dmg)
					return true;
			}
		}
		return false;
	}

	/** Checks an itemstack array (eg an inventory) for a given itemstack.
	 * Args: Check-for itemstack, Inventory, Match size T/F */
	public static boolean checkForItemStack(ItemStack is, ItemStack[] inv, boolean matchsize) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (matchsize) {
					return ItemStack.areItemStacksEqual(is, in);
				}
				else {
					return ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in);
				}
			}
		}
		return false;
	}

	/** Checks an itemstack array (eg an inventory) for a given itemstack.
	 * Args: Check-for itemstack, Inventory, Match size T/F */
	public static boolean checkForItemStack(ItemStack is, IInventory inv, boolean matchsize) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack in = inv.getStackInSlot(i);
			if (in != null) {
				if (matchsize) {
					return ItemStack.areItemStacksEqual(is, in);
				}
				else {
					return ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in);
				}
			}
		}
		return false;
	}

	/** Returns the location (array index) of an itemstack in the specified inventory.
	 * Returns -1 if not present. Args: Itemstack to check, Inventory, Match size T/F */
	public static int locateInInventory(ItemStack is, ItemStack[] inv, boolean matchsize) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (matchsize) {
					if (ItemStack.areItemStacksEqual(is, in)) {
						return i;
					}
				}
				else {
					if (ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/** Returns the location (array index) of an itemstack in the specified inventory.
	 * Returns -1 if not present. Args: Itemstack to check, Inventory, Match size T/F */
	public static int locateInInventory(ItemStack is, IInventory inv, boolean matchsize) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack in = inv.getStackInSlot(i);
			if (in != null) {
				if (matchsize) {
					if (ItemStack.areItemStacksEqual(is, in)) {
						return i;
					}
				}
				else {
					if (ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/** Returns the location (array index) of an item in the specified inventory.
	 * Returns -1 if not present. Args: Item ID, Inventory */
	public static int locateInInventory(int id, ItemStack[] inv) {
		if (!checkForItem(id, inv))
			return -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id) {
					return i;
				}
			}
		}
		return -1;
	}

	/** Returns the location (array index) of an item in the specified inventory.
	 * Returns -1 if not present. Args: Item ID, Metadata, Inventory */
	public static int locateInInventory(int id, int meta, ItemStack[] inv) {
		if (!checkForItem(id, inv))
			return -1;
		if (meta == -1) {
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] != null) {
					if (inv[i].itemID == id) {
						return i;
					}
				}
			}
		}
		else {
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] != null) {
					if (inv[i].itemID == id && inv[i].getItemDamage() == meta) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/** Counts the number of a certain item ID in the inventory.
	 * Args: ID, Inventory */
	public static int countItem(int id, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	/** Counts the number of a certain item in the inventory.
	 * Args: Item ID, Item Metadata, Inventory */
	public static int countItem(int id, int meta, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id && inv[i].getItemDamage() == meta) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	/** Returns the existence of an item in the specified inventory, and decrements it if found.
	 * Args: Item ID, Item Metadata, Inventory. Set meta to -1 for any. */
	public static boolean findAndDecrStack(int id, int meta, ItemStack[] inv) {
		int slot;
		if (meta != -1)
			slot = locateInInventory(id, meta, inv);
		else
			slot = locateInInventory(id, inv);
		if (slot == -1)
			return false;
		else {
			if (inv[slot].stackSize > 1)
				inv[slot].stackSize--;
			else
				inv[slot] = null;
			return true;
		}
	}

	/** Returns the an item in the specified inventory, and decrements it if found.
	 * Args: Item ID, Item Metadata, Inventory. Set meta to -1 for any. */
	public static ItemStack findAndDecrStack2(int id, int meta, ItemStack[] inv) {
		int slot;
		if (meta != -1)
			slot = locateInInventory(id, meta, inv);
		else
			slot = locateInInventory(id, inv);
		if (slot == -1)
			return null;
		else {
			ItemStack inslot = inv[slot];
			if (inv[slot].stackSize > 1)
				inv[slot].stackSize--;
			else
				inv[slot] = null;
			return inslot;
		}
	}

	/** Counts the number of separate stacks of the item in an inventory.
	 * Args: ID, metadata, inventory. Set meta to -1 for any. */
	public static int countNumStacks(int id, int meta, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (meta != -1) {
					//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(id)+" = "+String.valueOf(inv[i].itemID));
					if (inv[i].itemID == id && inv[i].getItemDamage() == meta)
						count++;
				}
				else {
					//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(id)+" == "+String.valueOf(inv[i].itemID));
					if (inv[i].itemID == id)
						count++;
					//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(count));
				}
			}
		}
		return count;
	}

	/** Counts the number of instances of an ItemStack in an inventory. Does not care about size.
	 * Args: ItemStack, inventory. Set meta to -1 for any. */
	public static int countNumStacks(ItemStack item, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == item.itemID)
					count++;
			}
		}
		return count;
	}

	/** Attempts to add an itemstack to an inventory. Is all-or-nothing - will not add a
	 * partial stack. Returns true if the stack "fit". Args: Itemstack, inventory */
	public static boolean putStackInInventory(ItemStack stack, ItemStack[] inventory) {
		int slot = locateInInventory(stack.itemID, stack.getItemDamage(), inventory);
		int empty = findEmptySlot(inventory);
		if (slot == -1) {
			if (empty == -1)
				return false;
			inventory[empty] = stack;
			return true;
		}
		if (inventory[slot].stackSize+stack.stackSize <= stack.getMaxStackSize()) {
			inventory[slot].stackSize += stack.stackSize;
			return true;
		}
		return false;
	}

	/** Attempts to add an item to an inventory. Is all-or-nothing - will not add a
	 * partial stack. Returns true if the stack "fit". Set metadata to -1 for any.
	 * Args: Item ID, item metadata, number of items, inventory */
	public static boolean putStackInInventory(int id, int meta, int size, ItemStack[] inventory) {
		if (meta == -1) {
			boolean fits = putStackInInventory(id, size, inventory);
			return fits;
		}
		int slot = locateInInventory(id, meta, inventory);
		int empty = findEmptySlot(inventory);
		if (slot == -1) {
			if (empty == -1)
				return false;
			inventory[empty] = new ItemStack(id, size, meta);
			return true;
		}
		if (inventory[slot].stackSize+size <= inventory[slot].getMaxStackSize()) {
			inventory[slot].stackSize += size;
			return true;
		}
		return false;
	}

	private static boolean putStackInInventory(int id, int size, ItemStack[] inventory) {
		int slot = locateInInventory(id, inventory);
		int empty = findEmptySlot(inventory);
		if (slot == -1) {
			if (empty == -1)
				return false;
			inventory[empty] = new ItemStack(id, size, 0);
			return true;
		}
		if (inventory[slot].stackSize+size <= inventory[slot].getMaxStackSize()) {
			inventory[slot].stackSize += size;
			return true;
		}
		return false;
	}

	/** Adds as many of the specified item as it can (up to the specified maximum) and
	 * returns the number of "leftover" items that did not fit. Set metadata to -1 for any.
	 * Args: Item ID, item metadata, number of items, inventory */
	public static int addToInventoryWithLeftover(int id, int meta, int size, ItemStack[] inventory) {
		if (meta == -1) {
			int leftover = addToInventoryWithLeftover(id, size, inventory);
			return leftover;
		}
		int slot = locateInInventory(id, meta, inventory);
		int empty = findEmptySlot(inventory);
		if (slot == -1) {
			if (empty == -1)
				return size;
			inventory[empty] = new ItemStack(id, size, meta);
			return 0;
		}
		int space = inventory[slot].getMaxStackSize()-inventory[slot].stackSize;
		if (space >= size) {
			inventory[slot].stackSize += size;
			return 0;
		}
		inventory[slot].stackSize += space;
		size -= space;
		return size;
	}

	private static int addToInventoryWithLeftover(int id, int size, ItemStack[] inventory) {
		int slot = locateInInventory(id, inventory);
		int empty = findEmptySlot(inventory);
		if (slot == -1) {
			if (empty == -1)
				return size;
			inventory[empty] = new ItemStack(id, size, 0);
			return 0;
		}
		int space = inventory[slot].getMaxStackSize()-inventory[slot].stackSize;
		if (space >= size) {
			inventory[slot].stackSize += size;
			return 0;
		}
		inventory[slot].stackSize += space;
		size -= space;
		return size;
	}

	/** Adds as much of the specified item stack as it can and
	 * returns the number of "leftover" items that did not fit.
	 * Args: Itemstack, inventory */
	public static int addToInventoryWithLeftover(ItemStack stack, ItemStack[] inventory) {
		int leftover = addToInventoryWithLeftover(stack.itemID, stack.getItemDamage(), stack.stackSize, inventory);
		return leftover;
	}

	/** Returns the location of an empty slot in an inventory. Returns -1 if none.
	 * Args: Inventory */
	public static int findEmptySlot(ItemStack[] inventory) {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null)
				return i;
			if (inventory[i].stackSize <= 0) {
				inventory[i] = null;
				return i;
			}
		}
		return -1;
	}

	/** Returns true if the inventory is empty. Args: Inventory */
	public static boolean isEmpty(ItemStack[] inventory) {
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null)
				return false;
		}
		return true;
	}

	/** Returns the number of items to add to a stack when adding a random number
	 * between min and max, but taking care to stay within stackSize limits.
	 * Args: Intial slot contents, min items, max items */
	public static int addUpToStack(ItemStack init, int min, int max) {
		int num = min+rand.nextInt(max-min+1);
		if (init == null)
			return (num);
		while (num+init.stackSize > init.getMaxStackSize())
			num--;
		return num;
	}

	/** Intelligently decrements a stack in an inventory, setting it to null if necessary.
	 * Also performs sanity checks. Args: Inventory, Slot */
	public static void decrStack(int slot, ItemStack[] inv) {
		if (slot >= inv.length) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is larger than the inventory.");
			return;
		}
		if (slot < 0) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is < 0.");
			return;
		}
		if (inv[slot] == null) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is empty.");
			return;
		}
		if (inv[slot].stackSize > 1)
			inv[slot].stackSize--;
		else
			inv[slot] = null;
	}

	/** Intelligently decrements a stack in an inventory, setting it to null if necessary.
	 * Also performs sanity checks. Args: Inventory, Slot */
	public static void decrStack(int slot, IInventory inv, int amount) {
		if (slot >= inv.getSizeInventory()) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is larger than the inventory.");
			return;
		}
		if (slot < 0) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is < 0.");
			return;
		}
		if (inv.getStackInSlot(slot) == null) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is empty.");
			return;
		}
		if (inv.getStackInSlot(slot).stackSize > amount)
			inv.getStackInSlot(slot).stackSize -= amount;
		else
			inv.setInventorySlotContents(slot, null);
	}

	/** Checks a crafting inventory for a specific ID and metadata (-1 for any). Args: InventoryCrafting, int ID */
	public static boolean checkForItem(InventoryCrafting ic, int id, int meta) {
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack is = ic.getStackInSlot(i);
			if (is != null) {
				if (is.getItem().itemID == id && (meta == -1 || is.getItemDamage() == meta))
					return true;
			}
		}
		return false;
	}

	/** Converts a crafting inventory to a standard ItemStack array. Args: InventoryCrafting */
	public static ItemStack[] convertCraftToItemStacks(InventoryCrafting ic) {
		ItemStack[] slots = new ItemStack[ic.getSizeInventory()];
		for (int i = 0; i < slots.length; i++)
			slots[i] = ic.getStackInSlot(i);
		return slots;
	}

	/** Counts the number of empty slots in an inventory. Args: Inventory */
	public static int countEmptySlots(ItemStack[] inv) {
		int num = 0;
		for (int i = 0; i < inv.length; i++)
			if (inv[i] == null)
				num++;
		return num;
	}

	/** Returns the highest metadata of a specific item/block ID in the specified inventory.
	 * If there is none of that ID, it will return -1. Args: ID, inventory */
	public static int findMaxMetadataOfID(int id, ItemStack[] inv) {
		int max = -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].itemID == id) {
				if (inv[i].getItemDamage() > max)
					max = inv[i].getItemDamage();
			}
		}
		return max;
	}

	/** Returns true if the inventory is full. Args: Inventory */
	public static boolean isInventoryFull(ItemStack[] inv) {
		if (countEmptySlots(inv) > 0)
			return false;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i].getMaxStackSize() > inv[i].stackSize)
				return false;
		}
		return true;
	}

	/** Returns true if the inventory has space for more of a specific item. Args: ID, metadata, inventory */
	public static boolean canAcceptMoreOf(int id, int meta, ItemStack[] inv) {
		if (countEmptySlots(inv) > 0)
			return true;
		if (locateInInventory(id, meta, inv) == -1)
			return false;
		int num = 0;
		int maxnum = new ItemStack(id, 1, meta).getMaxStackSize()*countNumStacks(id, meta, inv);
		for (int i = 0; i < inv.length; i++) {
			if (inv[i].itemID == id && inv[i].getItemDamage() == meta)
				num += inv[i].stackSize;
		}
		return (num < maxnum);
	}

	/** Returns the number of unique itemstacks in the inventory after sorting and cleaning. Args: Inventory */
	public static int getTotalUniqueStacks(ItemStack[] inv) {
		ItemStack[] cp = new ItemStack[inv.length];
		for (int i = 0; i < cp.length; i++)
			cp[i] = inv[i];
		return 0;
	}

	/** Sorts and cleans an inventory, combining identical items and arranging them. Args: Inventory */
	public static void sortInventory(ItemStack[] inv) {
		ArrayList<int[]> ids = new ArrayList<int[]>();
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				int[] entry = {inv[i].itemID, inv[i].getItemDamage(), inv[i].stackSize};
				ids.add(entry);
			}
		}
	}

	/** Returns true iff succeeded; adds iff can fit whole stack */
	public static boolean addToIInv(ItemStack is, IInventory ii) {
		if (!hasSpaceFor(is, ii))
			return false;
		int max = Math.min(ii.getInventoryStackLimit(), is.getMaxStackSize());
		for (int i = 0; i < ii.getSizeInventory() && is.stackSize > 0; i++) {
			if (ii.isItemValidForSlot(i, is)) {
				ItemStack in = ii.getStackInSlot(i);
				if (in == null) {
					int added = Math.min(is.stackSize, max);
					is.stackSize -= added;
					ii.setInventorySlotContents(i, ReikaItemHelper.getSizedItemStack(is, added));
				}
				else {
					if (ReikaItemHelper.matchStacks(is, in) && ItemStack.areItemStackTagsEqual(is, in)) {
						int space = max-in.stackSize;
						int added = Math.min(is.stackSize, space);
						is.stackSize -= added;
						ii.getStackInSlot(i).stackSize += added;
					}
				}
			}
		}
		return true;
	}

	public static boolean hasSpaceFor(ItemStack is, IInventory ii) {
		int size = is.stackSize;
		int max = Math.min(ii.getInventoryStackLimit(), is.getMaxStackSize());
		for (int i = 0; i < ii.getSizeInventory() && size > 0; i++) {
			ItemStack in = ii.getStackInSlot(i);
			if (in == null) {
				size -= max;
			}
			else {
				if (ReikaItemHelper.matchStacks(is, in) && ItemStack.areItemStackTagsEqual(is, in)) {
					int space = max-in.stackSize;
					size -= space;
				}
			}
		}
		return size <= 0;
	}

	public static int getFirstEmptySlot(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) == null)
				return i;
		}
		return -1;
	}

	public static boolean hasItemStack(ItemStack is, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null)
				if (ii.getStackInSlot(i).itemID == is.itemID && ii.getStackInSlot(i).getItemDamage() == is.getItemDamage())
					return true;
		}
		return false;
	}

	public static int locateNonFullStackOf(ItemStack is, IInventory ii) {
		if (is == null)
			return -1;
		int slot = -1;
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack in = ii.getStackInSlot(i);
			if (in != null) {
				if (ReikaItemHelper.matchStacks(is, in) && ItemStack.areItemStackTagsEqual(is, in))
					if (in.stackSize < is.getMaxStackSize())
						return i;
			}
		}
		return -1;
	}

	public static boolean hasNonFullStackOf(ItemStack is, IInventory ii) {
		return locateNonFullStackOf(is, ii) != -1;
	}

	public static boolean hasItem(int id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null)
				if (ii.getStackInSlot(i).itemID == id)
					return true;
		}
		return false;
	}

	public static int findMaxMetadataOfIDWithinMaximum(int id, ItemStack[] inv, int maxmeta) {
		int max = -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].itemID == id) {
				if (inv[i].getItemDamage() > max && inv[i].getItemDamage() <= maxmeta)
					max = inv[i].getItemDamage();
			}
		}
		return max;
	}

	public static void convertItems(int id0, int m0, int id1, int m1, ItemStack[] inv) {
		ItemStack to;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if ((inv[i].itemID == id0 || id0 == -1) && (inv[i].getItemDamage() == m0 || m0 == -1)) {
					if (id1 == -1 && m1 == -1) {
						return;
					}
					else if (m1 == -1) {
						to = new ItemStack(id1, inv[i].stackSize, inv[i].getItemDamage());
					}
					else if (id1 == -1) {
						to = new ItemStack(inv[i].itemID, inv[i].stackSize, m1);
					}
					else {
						to = new ItemStack(id1, inv[i].stackSize, m1);
					}
					inv[i] = to;
				}
			}
		}
	}

	public static void damageInventory(ItemStack[] inv) {
		ItemStack to;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				to = new ItemStack(inv[i].itemID, inv[i].stackSize, inv[i].getItemDamage()+1);
				inv[i] = to;
			}
		}
	}

	public static void repairInventory(ItemStack[] inv) {
		ItemStack to;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				to = new ItemStack(inv[i].itemID, inv[i].stackSize, 0);
				inv[i] = to;
			}
		}
	}

	public static boolean hasNEmptyStacks(IInventory ii, int n) {
		int e = 0;
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) == null)
				e++;
		}
		return e == n;
	}

	public static boolean hasNEmptyStacks(ItemStack[] inv, int n) {
		int e = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] == null)
				e++;
		}
		return e == n;
	}

	public static int locateIDInInventory(int id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null) {
				if (ii.getStackInSlot(i).itemID == id)
					return i;
			}
		}
		return -1;
	}

	public static int[] getWholeInventoryForISided(ISidedInventory ii) {
		int[] n = new int[ii.getSizeInventory()];
		for (int i = 0; i < n.length; i++)
			n[i] = i;
		return n;
	}

	/** Fill-in so one does not need to constantly rewrite the IInventory method */
	public static ItemStack getStackInSlotOnClosing(IInventory ii, int slot) {
		if (ii.getStackInSlot(slot) != null){
			ItemStack itemstack = ii.getStackInSlot(slot);
			ii.setInventorySlotContents(slot, null);
			return itemstack;
		}
		else
			return null;
	}

	/** Fill-in so one does not need to constantly rewrite the IInventory method */
	public static ItemStack decrStackSize(IInventory ii, int slot, int decr) {
		if (ii.getStackInSlot(slot) != null) {
			if (ii.getStackInSlot(slot).stackSize <= decr) {
				ItemStack itemstack = ii.getStackInSlot(slot);
				ii.setInventorySlotContents(slot, null);
				return itemstack;
			}
			ItemStack itemstack1 = ii.getStackInSlot(slot).splitStack(decr);
			if (ii.getStackInSlot(slot).stackSize == 0)
				ii.setInventorySlotContents(slot, null);
			return itemstack1;
		}
		else
			return null;
	}

	public static boolean addOrSetStack(ItemStack is, ItemStack[] inv, int slot) {
		if (is == null)
			return false;
		if (inv[slot] == null) {
			inv[slot] = is.copy();
			return true;
		}
		int max = inv[slot].getMaxStackSize();
		if (!(ReikaItemHelper.matchStacks(is, inv[slot]) && ItemStack.areItemStackTagsEqual(is, inv[slot])) || inv[slot].stackSize+is.stackSize > max)
			return false;
		inv[slot].stackSize += is.stackSize;
		return true;
	}

	/** Adds a certain amount of a specified ID and metadata to an inventory slot, creating the itemstack if necessary.
	 * Returns true if the whole stack fit and was added. Args: ID, number, metadata (-1 for any), inventory, slot */
	public static boolean addOrSetStack(int id, int size, int meta, ItemStack[] inv, int slot) {
		return addOrSetStack(new ItemStack(id, size, meta), inv, slot);
	}

	public static List<ItemStack> getWholeInventory(IInventory ii) {
		List<ItemStack> li = new ArrayList<ItemStack>();
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			li.add(ii.getStackInSlot(i));
		}
		return li;
	}

	public static void clearInventory(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ii.setInventorySlotContents(i, null);
		}
	}

	/** Add multiple items to an inventory. Args: IInventory, Items. Returns the ones that could not be added. */
	public static List<ItemStack> addMultipleItems(IInventory ii, List<ItemStack> items) {
		List<ItemStack> extra = new ArrayList<ItemStack>();
		for (int i = 0; i < items.size(); i++) {
			if (!addToIInv(items.get(i), ii))
				extra.add(items.get(i));
		}
		return extra;
	}

	/** Gets the first block in an inventory, optionally consuming one. Args: Inventory, Decr yes/no */
	public static ItemStack getNextBlockInInventory(ItemStack[] inv, boolean decr) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				Item item = is.getItem();
				if (item instanceof ItemBlock) {
					if (decr)
						decrStack(i, inv);
					return inv[i];
				}
			}
		}
		return null;
	}

	/** Returns whether an inventory is empty. Args: IInventory */
	public static boolean isEmpty(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack is = ii.getStackInSlot(i);
			if (is != null)
				return false;
		}
		return true;
	}

	/** Returns whether an inventory is full. Args: IInventory */
	public static boolean isFull(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack is = ii.getStackInSlot(i);
			if (is == null)
				return false;
			if (is.stackSize < is.getMaxStackSize())
				return false;
		}
		return true;
	}

	/** Spills the entire inventory of an ItemStack[] at the specified coordinates with a 1-block spread.
	 * Args: World, x, y, z, inventory */
	public static void spillAndEmptyInventory(World world, int x, int y, int z, ItemStack[] inventory) {
		EntityItem ei;
		ItemStack is;
		for (int i = 0; i < inventory.length; i++) {
			is = inventory[i];
			inventory[i] = null;
			if (is != null && !world.isRemote) {
				ei = new EntityItem(world, x+rand.nextFloat(), y+rand.nextFloat(), z+rand.nextFloat(), is);
				ReikaEntityHelper.addRandomDirVelocity(ei, 0.2);
				world.spawnEntityInWorld(ei);
			}
		}
	}

	/** Spills the entire inventory of an ItemStack[] at the specified coordinates with a 1-block spread.
	 * Args: World, x, y, z, IInventory */
	public static void spillAndEmptyInventory(World world, int x, int y, int z, IInventory ii) {
		int size = ii.getSizeInventory();
		for (int i = 0; i < size; i++) {
			ItemStack s = ii.getStackInSlot(i);
			if (s != null) {
				ii.setInventorySlotContents(i, null);
				EntityItem ei = new EntityItem(world, x+rand.nextFloat(), y+rand.nextFloat(), z+rand.nextFloat(), s);
				ReikaEntityHelper.addRandomDirVelocity(ei, 0.2);
				ei.delayBeforeCanPickup = 10;
				if (!world.isRemote)
					world.spawnEntityInWorld(ei);
			}
		}
	}

	public static int getFirstNonEmptySlot(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null)
				return i;
		}
		return -1;
	}

	/** Adds an ItemStack to an inventory and returns how many items were successfully added. */
	public static int addStackAndReturnCount(ItemStack stack, IInventory ii) {
		int transferred = 0;
		for (int i = 0; i < ii.getSizeInventory() && stack.stackSize > 0; i++) {
			ItemStack is = ii.getStackInSlot(i);
			if (is == null) {
				ii.setInventorySlotContents(i, stack);
				transferred += stack.stackSize;
				stack.stackSize = 0;
			}
			else {
				if (ItemStack.areItemStacksEqual(stack, is)) {
					int max = Math.min(stack.getMaxStackSize(), ii.getInventoryStackLimit());
					int space = max-is.stackSize;
					if (space > 0) {
						int added = Math.min(space, stack.stackSize);
						transferred += added;
						is.stackSize += added;
						stack.stackSize -= added;
					}
				}
			}
		}
		return transferred;
	}

	public static ArrayList<ItemStack> getAllTransferrables(ForgeDirection from, IInventory source) {
		ArrayList<ItemStack> li = new ArrayList();
		if (source instanceof ISidedInventory) {
			ISidedInventory ii = (ISidedInventory)source;
			for (int slot = 0; slot < source.getSizeInventory(); slot++) {
				ItemStack is = ii.getStackInSlot(slot);
				if (is != null) {
					if (ii.canExtractItem(slot, is, from.getOpposite().ordinal())) {
						li.add(is);
					}
				}
			}
		}
		else if (source instanceof IInventory) {
			IInventory ii = source;
			for (int slot = 0; slot < source.getSizeInventory(); slot++) {
				ItemStack is = ii.getStackInSlot(slot);
				if (is != null) {
					li.add(is);
				}
			}
		}
		return li;
	}

	public static HashMap<Integer, ItemStack> getLocatedTransferrables(ForgeDirection from, IInventory source) {
		HashMap<Integer, ItemStack> li = new HashMap<Integer, ItemStack>();
		if (source instanceof ISidedInventory) {
			ISidedInventory ii = (ISidedInventory)source;
			for (int slot = 0; slot < source.getSizeInventory(); slot++) {
				ItemStack is = ii.getStackInSlot(slot);
				if (is != null) {
					if (ii.canExtractItem(slot, is, from.getOpposite().ordinal())) {
						li.put(slot, is);
					}
				}
			}
		}
		else if (source instanceof IInventory) {
			IInventory ii = source;
			for (int slot = 0; slot < source.getSizeInventory(); slot++) {
				ItemStack is = ii.getStackInSlot(slot);
				if (is != null) {
					li.put(slot, is);
				}
			}
		}
		return li;
	}
}
