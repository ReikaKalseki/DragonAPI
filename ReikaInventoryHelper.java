package Reika.DragonAPI;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public final class ReikaInventoryHelper {
	
	private static Random par5Random = new Random();
	
	private ReikaInventoryHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}
	
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
			if (inv[i] != null) {
				if (matchsize) {
					if (inv[i].itemID == is.itemID && inv[i].getItemDamage() == is.getItemDamage() && inv[i].stackSize == is.stackSize)
						return true;
				}
				else {
					if (inv[i].itemID == is.itemID && inv[i].getItemDamage() == is.getItemDamage())
						return true;
				}
			}
		}
		return false;
	}
	
	/** Returns the location (array index) of an itemstack in the specified inventory.
	 * Returns -1 if not present. Args: Itemstack to check, Inventory, Match size T/F */
	public static int locateInInventory(ItemStack is, ItemStack[] inv, boolean matchsize) {
		if (!checkForItemStack(is, inv, matchsize))
			return -1;
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (matchsize) {
					if (inv[i].itemID == is.itemID && inv[i].getItemDamage() == is.getItemDamage() && inv[i].stackSize == is.stackSize) {
						return i;
					}
				}
				else {
					if (inv[i].itemID == is.itemID && inv[i].getItemDamage() == is.getItemDamage()) {
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
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				if (inv[i].itemID == id && inv[i].getItemDamage() == meta) {
					return i;
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
		int num = min+par5Random.nextInt(max-min+1);
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
			ReikaGuiAPI.write("Tried to Access Slot "+slot+", which is larger than the inventory.");
			return;
		}
		if (inv[slot].stackSize > 1)
			inv[slot].stackSize--;
		else
			inv[slot] = null;
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

	/** Adds a certain amount of a specified ID and metadata to an inventory slot, creating the itemstack if necessary.
	 * Args: ID, number, metadata, inventory, slot */
	public static void addOrSetStack(int id, int size, int meta, ItemStack[] inv, int slot) {
		if (inv[slot] == null) {
			inv[slot] = new ItemStack(id, size, meta);
			return;
		}
		int max = inv[slot].getMaxStackSize();
		if (inv[slot].itemID != id || inv[slot].getItemDamage() != meta || inv[slot].stackSize >= max)
			return;
		inv[slot].stackSize += size;
		if (inv[slot].stackSize > max)
			inv[slot].stackSize = max;
	}
	
	/** Returns true if the player has the given ID and metadata in their inventory, or is in creative mode.
	 * Args: Player, ID, metadata (-1 for any) */
	public static boolean playerHasOrIsCreative(EntityPlayer ep, int id, int meta) {
		if (ep.capabilities.isCreativeMode)
			return true;
		ItemStack[] ii = ep.inventory.mainInventory;
		return (checkForItemStack(id, meta, ii));
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
}
