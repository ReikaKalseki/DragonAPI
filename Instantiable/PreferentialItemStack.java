/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class PreferentialItemStack {

	private ArrayList<String> oreNames = new ArrayList();
	private Collection<ItemStack> blacklist = new ArrayList();
	private Collection<Item> itemblacklist = new ArrayList();
	private final ItemStack fallbackItem;

	public PreferentialItemStack(Item backup, String... items) {
		this(new ItemStack(backup), items);
	}

	public PreferentialItemStack(Block backup, String... items) {
		this(new ItemStack(backup), items);
	}

	public PreferentialItemStack(ItemStack backup, String... items) {
		if (backup == null || backup.getItem() == null)
			throw new IllegalArgumentException("Invalid (nonexistent) fallback item!");
		fallbackItem = backup;
		for (int i = 0; i < items.length; i++) {
			oreNames.add(items[i]);
		}
	}

	public Object getItem() {
		String s = this.getStringToUse();
		return s != null ? s : fallbackItem;
	}

	public PreferentialItemStack blockItemStack(ItemStack is) {
		blacklist.add(is.copy());
		return this;
	}

	public PreferentialItemStack blockItem(Item i) {
		itemblacklist.add(i);
		return this;
	}

	private String getStringToUse() {
		for (String ore : oreNames) {
			Collection<ItemStack> items = OreDictionary.getOres(ore);
			if (items != null) {
				for (ItemStack is : items) {
					if (!itemblacklist.contains(is.getItem()) && !ReikaItemHelper.collectionContainsItemStack(blacklist, is))
						return ore;
				}
			}
		}
		return null;
	}
}
