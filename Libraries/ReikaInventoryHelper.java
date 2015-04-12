/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.TemporaryInventory;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Interfaces.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class ReikaInventoryHelper extends DragonAPICore {

	/** Checks an itemstack array (eg an inventory) for an item of a specific id.
	 * Returns true if found. Args: Item ID, Inventory */
	public static boolean checkForItem(Item id, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (in.getItem() == id) {
					return true;
				}
				else if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItem(id, ((ActivatedInventoryItem)in.getItem()).getInventory(in)))
						return true;
				}
			}
		}
		return false;
	}

	public static boolean checkForItem(Block id, ItemStack[] inv) {
		return checkForItem(Item.getItemFromBlock(id), inv);
	}

	public static boolean checkForItem(Item id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack in = ii.getStackInSlot(i);
			if (in != null) {
				if (in.getItem() == id) {
					return true;
				}
				else if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItem(id, ((ActivatedInventoryItem)in.getItem()).getInventory(in)))
						return true;
				}
			}
		}
		return false;
	}

	public static boolean checkForItem(Block id, IInventory ii) {
		return checkForItem(Item.getItemFromBlock(id), ii);
	}

	/** Checks an itemstack array (eg an inventory) for an itemstack,
	 * defined by id, size, metadata. Returns true if found.
	 * Args: Item ID, Metadata, StackSize, Inventory *//*
	 @Deprecated
	public static boolean checkForItemStack(Item id, int dmg, int num, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].getItem() == id && inv[i].getItemDamage() == dmg && inv[i].stackSize == num)
					return true;
			}
		}
		return false;
	}
	  */
	/** Checks an itemstack array (eg an inventory) for an itemstack,
	 * defined by id, and metadata. Returns true if found.
	 * Args: Item ID, Metadata, Inventory */
	public static boolean checkForItemStack(Item id, int dmg, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (in.getItem() == id && in.getItemDamage() == dmg) {
					return true;
				}
				else if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(id, dmg, ((ActivatedInventoryItem)in.getItem()).getInventory(in)))
						return true;
				}
			}
		}
		return false;
	}

	/** Checks an inventory for an itemstack,
	 * defined by id, and metadata. Returns true if found.
	 * Args: Item ID, Metadata, Inventory */
	public static boolean checkForItemStack(Item id, int dmg, IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (is != null) {
				if (is.getItem() == id && is.getItemDamage() == dmg) {
					return true;
				}
				else if (is.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(id, dmg, ((ActivatedInventoryItem)is.getItem()).getInventory(is)))
						return true;
				}
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
				if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(is, ((ActivatedInventoryItem)in.getItem()).getInventory(in), matchsize))
						return true;
				}
				if (matchsize) {
					if (ItemStack.areItemStacksEqual(is, in))
						return true;
				}
				else {
					if (ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in))
						return true;
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
				if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(is, ((ActivatedInventoryItem)in.getItem()).getInventory(in), matchsize))
						return true;
				}
				if (matchsize) {
					if (ItemStack.areItemStacksEqual(is, in))
						return true;
				}
				else {
					if (ItemStack.areItemStackTagsEqual(is, in) && ReikaItemHelper.matchStacks(is, in))
						return true;
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
				if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(is, ((ActivatedInventoryItem)in.getItem()).getInventory(in), matchsize))
						return i;
				}
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
				if (in.getItem() instanceof ActivatedInventoryItem) {
					if (checkForItemStack(is, ((ActivatedInventoryItem)in.getItem()).getInventory(in), matchsize))
						return i;
				}
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
	public static int locateInInventory(Item id, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (in.getItem() == id) {
					return i;
				}
				else if (in.getItem() instanceof ActivatedInventoryItem) {
					if (locateInInventory(id, ((ActivatedInventoryItem)in.getItem()).getInventory(in)) >= 0)
						return i;
				}
			}
		}
		return -1;
	}

	public static int locateInInventory(Block id, ItemStack[] inv) {
		return locateInInventory(Item.getItemFromBlock(id), inv);
	}

	public static int locateInInventory(Block id, int meta, ItemStack[] inv) {
		return locateInInventory(Item.getItemFromBlock(id), meta, inv);
	}

	/** Returns the location (array index) of an item in the specified inventory.
	 * Returns -1 if not present. Args: Item ID, Metadata, Inventory */
	public static int locateInInventory(Item id, int meta, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null) {
				if (in.getItem() == id && (meta == -1 || in.getItemDamage() == meta)) {
					return i;
				}
				else if (in.getItem() instanceof ActivatedInventoryItem) {
					if (locateInInventory(id, meta, ((ActivatedInventoryItem)in.getItem()).getInventory(in)) >= 0)
						return i;
				}
			}
		}
		return -1;
	}

	/** Counts the number of a certain item ID in the inventory.
	 * Args: ID, Inventory */
	public static int countItem(Item id, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].getItem() == id) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	/** Counts the number of a certain item in the inventory.
	 * Args: Item ID, Item Metadata, Inventory */
	public static int countItem(Item id, int meta, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].getItem() == id && inv[i].getItemDamage() == meta) {
					count += inv[i].stackSize;
				}
			}
		}
		return count;
	}

	public static boolean findAndDecrStack(Block id, int meta, ItemStack[] inv) {
		return findAndDecrStack(Item.getItemFromBlock(id), meta, inv);
	}

	/** Returns the existence of an item in the specified inventory, and decrements it if found.
	 * Args: Item ID, Item Metadata, Inventory. Set meta to -1 for any. */
	public static boolean findAndDecrStack(Item id, int meta, ItemStack[] inv) {
		ItemStack is = meta >= 0 ? new ItemStack(id, 1, meta) : new ItemStack(id, 1, OreDictionary.WILDCARD_VALUE);
		return findAndDecrStack(is, inv);
	}

	public static boolean findAndDecrStack(ItemStack is, ItemStack[] inv) {
		return findAndDecrStack(is, inv, false);
	}

	public static boolean findAndDecrStack(ItemStack is, ItemStack[] inv, boolean nbt) {
		int slot;
		if (is.getItemDamage() != OreDictionary.WILDCARD_VALUE)
			slot = locateInInventory(is.getItem(), is.getItemDamage(), inv);
		else
			slot = locateInInventory(is.getItem(), inv);
		if (slot == -1)
			return false;
		else {
			decrStack(slot, inv);
			return true;
		}
	}

	/** Returns the an item in the specified inventory, and decrements it if found.
	 * Args: Item ID, Item Metadata, Inventory. Set meta to -1 for any. */
	public static ItemStack findAndDecrStack2(Item id, int meta, ItemStack[] inv) {
		int slot;
		if (meta != -1)
			slot = locateInInventory(id, meta, inv);
		else
			slot = locateInInventory(id, inv);
		if (slot == -1)
			return null;
		else {
			ItemStack inslot = inv[slot];
			decrStack(slot, inv);
			return inslot;
		}
	}

	/** Counts the number of separate stacks of the item in an inventory.
	 * Args: ID, metadata, inventory. Set meta to -1 for any. */
	public static int countNumStacks(Item id, int meta, ItemStack[] inv) {
		int count = 0;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (meta != -1) {
					//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(id)+" = "+String.valueOf(inv[i].getItem()));
					if (inv[i].getItem() == id && inv[i].getItemDamage() == meta)
						count++;
				}
				else {
					//ModLoader.getMinecraftInstance().thePlayer.addChatMessage(String.valueOf(id)+" == "+String.valueOf(inv[i].getItem()));
					if (inv[i].getItem() == id)
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
				if (ReikaItemHelper.matchStacks(item, inv[i]))
					count++;
			}
		}
		return count;
	}

	/** Attempts to add an itemstack to an inventory. Is all-or-nothing - will not add a
	 * partial stack. Returns true if the stack "fit". Args: Itemstack, inventory */
	public static boolean putStackInInventory(ItemStack is, IInventory ii, boolean overrideValid) {
		ItemStack stack = is.copy();
		ArrayList<Integer> slots = getSlotsWithItemStack(stack, ii, false);
		int empty = findEmptySlot(ii);

		int max = Math.min(ii.getInventoryStackLimit(), stack.getMaxStackSize());

		int addable = 0;
		ArrayList<Integer> validslots = new ArrayList();
		for (int i = 0; i < slots.size() && stack.stackSize > 0; i++) {
			int slot = slots.get(i);
			if (overrideValid || ii.isItemValidForSlot(slot, stack)) {
				ItemStack in = ii.getStackInSlot(slot);
				if (ReikaItemHelper.matchStacks(stack, in) && ItemStack.areItemStackTagsEqual(stack, in)) {
					int space = Math.min(max-in.stackSize, stack.stackSize);
					addable += space;
					validslots.add(slot);
				}
			}
		}
		if (empty != -1)
			addable += stack.getMaxStackSize();

		if (addable < stack.stackSize)
			return false;

		for (int i = 0; i < validslots.size() && stack.stackSize > 0; i++) {
			int slot = validslots.get(i);
			ItemStack in = ii.getStackInSlot(slot);
			int space = Math.min(max-in.stackSize, stack.stackSize);
			in.stackSize += space;
			stack.stackSize -= space;
		}
		if (stack.stackSize <= 0)
			return true;
		if (empty != -1) {
			ii.setInventorySlotContents(empty, stack.copy());
			return true;
		}
		return false;
	}

	/** Attempts to add an item to an inventory. Is all-or-nothing - will not add a
	 * partial stack. Returns true if the stack "fit". Set metadata to -1 for any.
	 * Args: Item ID, item metadata, number of items, inventory */
	public static boolean putStackInInventory(Item id, int meta, int size, ItemStack[] inventory) {
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

	private static boolean putStackInInventory(Item id, int size, ItemStack[] inventory) {
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
	public static int addToInventoryWithLeftover(Item id, int meta, int size, ItemStack[] inventory) {
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

	private static int addToInventoryWithLeftover(Item id, int size, ItemStack[] inventory) {
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
		int leftover = addToInventoryWithLeftover(stack.getItem(), stack.getItemDamage(), stack.stackSize, inventory);
		return leftover;
	}

	public static int addToInventoryWithLeftover(ItemStack stack, IInventory inventory) {
		int left = stack.stackSize;
		int max = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (in == null) {
				int add = Math.min(max, left);
				inventory.setInventorySlotContents(i, ReikaItemHelper.getSizedItemStack(stack, add));
				left -= add;
				if (left <= 0)
					return 0;
			}
			else {
				if (ReikaItemHelper.matchStacks(stack, in) && ItemStack.areItemStackTagsEqual(stack, in)) {
					int space = max-in.stackSize;
					int add = Math.min(space, stack.stackSize);
					if (add > 0) {
						in.stackSize += add;
						left -= add;
						if (left <= 0)
							return 0;
					}
				}
			}
		}
		return left;
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

	/** Returns the location of an empty slot in an inventory. Returns -1 if none.
	 * Args: Inventory */
	public static int findEmptySlot(IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack is = ii.getStackInSlot(i);
			if (is == null)
				return i;
			if (is.stackSize <= 0) {
				is = null;
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<Integer> findEmptySlots(ItemStack[] inventory) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null)
				li.add(i);
			if (inventory[i].stackSize <= 0) {
				inventory[i] = null;
				li.add(i);
			}
		}
		return li;
	}

	public static ArrayList<Integer> findEmptySlots(IInventory inventory) {
		ArrayList li = new ArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack is = inventory.getStackInSlot(i);
			if (is == null)
				li.add(i);
			if (is.stackSize <= 0) {
				inventory.setInventorySlotContents(i, null);
				li.add(i);
			}
		}
		return li;
	}

	/** Returns true if the inventory is empty. Args: Inventory */
	public static boolean isEmpty(ItemStack[] inventory) {
		return isEmpty(inventory, 0, inventory.length);
	}

	/** Returns true if the inventory is empty in the slot range specified. Args: Inventory, int min, int max */
	public static boolean isEmpty(ItemStack[] inventory, int min, int max) {
		for (int i = min; i < max; i++) {
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
		ItemStack in = inv[slot];
		if (in == null) {
			ReikaChatHelper.write("Tried to access Slot "+slot+", which is empty.");
			return;
		}
		if (in.getItem() instanceof ActivatedInventoryItem) {
			((ActivatedInventoryItem)in.getItem()).decrementSlot(in, slot);
		}
		else if (in.stackSize > 1)
			in.stackSize--;
		else
			inv[slot] = null;
	}

	/** Intelligently decrements a stack in an inventory, setting it to null if necessary.
	 * Also performs sanity checks. Args: Inventory, Slot, Amount */
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
		//ReikaJavaLibrary.pConsole("pre: "+inv.getStackInSlot(slot)+" w "+amount);
		if (inv.getStackInSlot(slot).stackSize > amount)
			inv.getStackInSlot(slot).stackSize -= amount;
		else
			inv.setInventorySlotContents(slot, null);
		//ReikaJavaLibrary.pConsole("post: "+inv.getStackInSlot(slot)+" w "+amount);

	}

	/** Checks a crafting inventory for a specific ID and metadata (-1 for any). Args: InventoryCrafting, int ID */
	public static boolean checkForItem(InventoryCrafting ic, Item id, int meta) {
		for (int i = 0; i < ic.getSizeInventory(); i++) {
			ItemStack is = ic.getStackInSlot(i);
			if (is != null) {
				if (is.getItem() == id && (meta == -1 || is.getItemDamage() == meta))
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
	public static int findMaxMetadataOfID(Item id, ItemStack[] inv) {
		int max = -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].getItem() == id) {
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

	public static boolean canAcceptMoreOf(Item item, int meta, int amt, IInventory inv) {
		return canAcceptMoreOf(new ItemStack(item, amt, meta), inv);
	}

	/** Returns true if the inventory has space for more of a specific Items. Args: ID, metadata, inventory */
	public static boolean canAcceptMoreOf(ItemStack is, IInventory inv) {
		/*
		if (countEmptySlots(inv) > 0)
			return true;
		if (locateInInventory(id, meta, inv) == -1)
			return false;

		int num = 0;
		int maxnum = new ItemStack(id, 1, meta).getMaxStackSize()*countNumStacks(id, meta, inv);
		for (int i = 0; i < inv.length; i++) {
			if (inv[i].getItem() == id && inv[i].getItemDamage() == meta)
				num += inv[i].stackSize;
		}
		return (num < maxnum);*/
		int space = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.isItemValidForSlot(i, is)) {
				ItemStack in = inv.getStackInSlot(i);
				if (in == null)
					return true;
				else {
					if (ReikaItemHelper.matchStacks(in, is) && ItemStack.areItemStackTagsEqual(is, in)) {
						int max = Math.min(in.getMaxStackSize(), inv.getInventoryStackLimit());
						space += max-in.stackSize;
					}
				}
			}
		}
		return space >= is.stackSize;
	}

	/** Returns the number of unique itemstacks in the inventory after sorting and cleaning. Args: Inventory */
	public static int getTotalUniqueStacks(ItemStack[] inv) {
		ItemStack[] cp = new ItemStack[inv.length];
		for (int i = 0; i < cp.length; i++)
			cp[i] = inv[i];
		return 0;
	}

	/** Sorts and cleans an inventory, combining identical items and arranging them. Args: Inventory *//*
	public static void sortInventory(ItemStack[] inv) {
		ArrayList<int[]> ids = new ArrayList<int[]>();
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				int[] entry = {inv[i].getItem(), inv[i].getItemDamage(), inv[i].stackSize};
				ids.add(entry);
			}
		}
	}*/

	public static boolean addToIInv(Block b, IInventory ii) {
		return addToIInv(new ItemStack(b), ii);
	}

	public static boolean addToIInv(Item is, IInventory ii) {
		return addToIInv(new ItemStack(is), ii);
	}

	public static boolean addToIInv(ItemStack is, IInventory ii) {
		return addToIInv(is, ii, false);
	}

	public static boolean addToIInv(ItemStack is, IInventory ii, boolean overrideValid) {
		return addToIInv(is, ii, overrideValid, 0, ii.getSizeInventory());
	}

	public static boolean addToIInv(ItemStack is, IInventory ii, int first, int last) {
		return addToIInv(is, ii, false, first, last);
	}

	/** Returns true iff succeeded; adds iff can fit whole stack */
	public static boolean addToIInv(ItemStack is, IInventory ii, boolean overrideValid, int firstSlot, int maxSlot) {
		if (!hasSpaceFor(is, ii, overrideValid, firstSlot, maxSlot)) {
			return false;
		}
		int max = Math.min(ii.getInventoryStackLimit(), is.getMaxStackSize());
		for (int i = firstSlot; i < maxSlot; i++) {
			if (overrideValid || ii.isItemValidForSlot(i, is)) {
				ItemStack in = ii.getStackInSlot(i);
				if (in == null) {
					int added = Math.min(is.stackSize, max);
					is.stackSize -= added;
					ii.setInventorySlotContents(i, ReikaItemHelper.getSizedItemStack(is, added));
					return true;
				}
				else {
					if (ReikaItemHelper.matchStacks(is, in) && ItemStack.areItemStackTagsEqual(is, in)) {
						int space = max-in.stackSize;
						int added = Math.min(is.stackSize, space);
						is.stackSize -= added;
						ii.getStackInSlot(i).stackSize += added;
						if (is.stackSize <= 0)
							return true;
					}
				}
			}
		}
		return is.stackSize == 0;
	}

	public static boolean hasSpaceFor(ItemStack is, IInventory ii, boolean overrideValid) {
		return hasSpaceFor(is, ii, overrideValid, 0, ii.getSizeInventory());
	}

	public static boolean hasSpaceFor(ItemStack is, IInventory ii, boolean overrideValid, int firstSlot, int maxSlot) {
		int size = is.stackSize;
		int max = Math.min(ii.getInventoryStackLimit(), is.getMaxStackSize());
		for (int i = firstSlot; i < maxSlot && size > 0; i++) {
			if (overrideValid || ii.isItemValidForSlot(i, is)) {
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
				if (ii.getStackInSlot(i).getItem() == is.getItem() && ii.getStackInSlot(i).getItemDamage() == is.getItemDamage())
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

	public static boolean hasItem(Item id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null)
				if (ii.getStackInSlot(i).getItem() == id)
					return true;
		}
		return false;
	}

	public static int findMaxMetadataOfIDWithinMaximum(Item id, ItemStack[] inv, int maxmeta) {
		int max = -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].getItem() == id) {
				if (inv[i].getItemDamage() > max && inv[i].getItemDamage() <= maxmeta)
					max = inv[i].getItemDamage();
			}
		}
		return max;
	}

	public static void convertItems(Item id0, int m0, Item id1, int m1, ItemStack[] inv) {
		ItemStack to;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if ((inv[i].getItem() == id0 || id0 == null) && (inv[i].getItemDamage() == m0 || m0 == -1)) {
					if (id1 == null && m1 == -1) {
						return;
					}
					else if (m1 == -1) {
						to = new ItemStack(id1, inv[i].stackSize, inv[i].getItemDamage());
					}
					else if (id1 == null) {
						to = new ItemStack(inv[i].getItem(), inv[i].stackSize, m1);
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
				to = new ItemStack(inv[i].getItem(), inv[i].stackSize, inv[i].getItemDamage()+1);
				inv[i] = to;
			}
		}
	}

	public static void repairInventory(ItemStack[] inv) {
		ItemStack to;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				to = new ItemStack(inv[i].getItem(), inv[i].stackSize, 0);
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

	public static int locateIDInInventory(Item id, IInventory ii) {
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			if (ii.getStackInSlot(i) != null) {
				if (ii.getStackInSlot(i).getItem() == id)
					return i;
			}
		}
		return -1;
	}

	public static int[] getWholeInventoryForISided(ISidedInventory ii) {
		return ReikaArrayHelper.getLinearArray(ii.getSizeInventory());
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
	public static boolean addOrSetStack(Item id, int size, int meta, ItemStack[] inv, int slot) {
		return addOrSetStack(new ItemStack(id, size, meta), inv, slot);
	}

	public static boolean addOrSetStack(Block id, int size, int meta, ItemStack[] inv, int slot) {
		return addOrSetStack(Item.getItemFromBlock(id), size, meta, inv, slot);
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
			int max = Math.min(is.getMaxStackSize(), ii.getInventoryStackLimit());
			if (is.stackSize < max)
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

	/** Spills the entire inventory of an IInventory at the specified coordinates with a 1-block spread.
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

	public static ArrayList<Integer> getSlotsWithItemStack(ItemStack is, IInventory ii, boolean matchSize) {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < ii.getSizeInventory(); i++) {
			ItemStack in = ii.getStackInSlot(i);
			if (ReikaItemHelper.matchStacks(is, in)) {
				if (!matchSize || in.stackSize == is.stackSize) {
					li.add(i);
				}
			}
		}
		return li;
	}

	public static ArrayList<Integer> getSlotsWithItemStack(ItemStack is, ItemStack[] inv, boolean matchSize) {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (ReikaItemHelper.matchStacks(is, in)) {
				if (!matchSize || in.stackSize == is.stackSize) {
					li.add(i);
				}
			}
		}
		return li;
	}

	public static ArrayList<Integer> getSlotsBetweenWithItemStack(ItemStack is, IInventory ii, int min, int max, boolean matchSize) {
		ArrayList<Integer> li = new ArrayList();
		for (int i = min; i <= max; i++) {
			ItemStack in = ii.getStackInSlot(i);
			if (ReikaItemHelper.matchStacks(is, in)) {
				if (!matchSize || in.stackSize == is.stackSize) {
					li.add(i);
				}
			}
		}
		return li;
	}

	public static boolean inventoryContains(ItemHashMap<Integer> map, IInventory ii) {
		ItemHashMap<Integer> inv = ItemHashMap.getFromInventory(ii);
		for (ItemStack is : map.keySet()) {
			int need = map.get(is);
			int has = inv.get(is);
			if (need > has)
				return false;
		}
		return true;
	}

	public static void removeFromInventory(ItemHashMap<Integer> map, IInventory ii) {
		for (ItemStack is : map.keySet()) {
			int need = map.get(is);
			int loc = locateInInventory(is, ii, false);
			while (loc >= 0 && need > 0) {
				ItemStack in = ii.getStackInSlot(loc);
				int max = Math.min(need, in.stackSize);
				decrStack(loc, ii, max);
				loc = locateInInventory(is, ii, false);
			}
		}
	}

	public static void generateMultipliedLoot(int bonus, Random r, String s, IInventory te) {
		for (int n = 0; n < bonus; n++) {
			TemporaryInventory ii = new TemporaryInventory(te.getSizeInventory());
			WeightedRandomChestContent[] loot = ChestGenHooks.getItems(s, r);
			WeightedRandomChestContent.generateChestContents(r, loot, ii, ChestGenHooks.getCount(s, r));
			for (int i = 0; i < ii.getSizeInventory(); i++) {
				ItemStack in = ii.getStackInSlot(i);
				if (in != null) {
					int tg = r.nextInt(ii.getSizeInventory());
					int tries = 0;
					while (te.getStackInSlot(tg) != null && tries < 10) {
						tg = r.nextInt(ii.getSizeInventory());
						tries++;
					}
					if (te.getStackInSlot(tg) == null) {
						te.setInventorySlotContents(tg, in);
					}
				}
			}
		}
	}

	public static void addItems(IInventory ii, ArrayList<ItemStack> li) {
		for (ItemStack is : li) {
			addToIInv(is, ii);
		}
	}

	/** Returns the number successfully removed. */
	public static int drawFromInventory(ItemStack is, int max, IInventory inventory) {
		int amt = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack in = inventory.getStackInSlot(i);
			if (ReikaItemHelper.matchStacks(is, in)) {
				int rem = Math.min(max, in.stackSize);
				if (rem > 0) {
					if (in.stackSize > rem) {
						in.stackSize -= rem;
					}
					else {
						inventory.setInventorySlotContents(i, null);
					}
					max -= rem;
					amt += rem;
					if (max <= 0)
						break;
				}
			}
		}
		return amt;
	}
}
