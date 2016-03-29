/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemMatch {

	private HashSet<KeyedItemStack> items = new HashSet();

	private ArrayList<ItemStack> displayList = new ArrayList();

	private ItemMatch() {

	}

	public ItemMatch(Block b) {
		this.addItem(new KeyedItemStack(b));
	}

	public ItemMatch(Item i) {
		this.addItem(new KeyedItemStack(i));
	}

	public ItemMatch(ItemStack is) {
		this.addItem(new KeyedItemStack(is));
	}

	public ItemMatch(String s) {
		this.addAll(OreDictionary.getOres(s));
		if (items.isEmpty())
			throw new RegistrationException(DragonAPIInit.instance, "This recipe uses an OreDict tag with no registered items!");
	}

	public ItemMatch(Collection<ItemStack> c) {
		this.addAll(c);
		if (items.isEmpty())
			throw new RegistrationException(DragonAPIInit.instance, "This recipe uses an list with no items!");
	}

	private void addAll(Collection<ItemStack> li) {
		for (ItemStack is : li) {
			this.addItem(new KeyedItemStack(is));
		}
	}

	public ItemMatch copy() {
		ItemMatch m = new ItemMatch();
		m.items.addAll(items);
		m.displayList.addAll(displayList);
		return m;
	}

	private void addItem(KeyedItemStack ks) {
		items.add(ks.setSimpleHash(true).lock());
		//if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		displayList.add(ks.getItemStack());
	}

	public boolean match(ItemStack is) {
		/*
		for (KeyedItemStack in : items) {
			if (ReikaItemHelper.matchStacks(in, is) && (in.stackTagCompound == null || ItemStack.areItemStackTagsEqual(in, is))) {
				return true;
			}
		}
		return false;
		 */
		return is != null && items.contains(new KeyedItemStack(is).setSimpleHash(true));
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getCycledItem() {
		if (displayList.isEmpty()) {
			DragonAPICore.logError("Could not provide cycled item for "+this+"!");
			return new ItemStack(Blocks.fire);
		}
		return displayList.get((int)((System.currentTimeMillis()/2000+Math.abs(this.hashCode()))%displayList.size()));
	}

	public Set<KeyedItemStack> getItemList() {
		return Collections.unmodifiableSet(items);
	}

	@Override
	public String toString() {
		return items.toString();
	}

	@Override
	public int hashCode() {
		return items.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ItemMatch) {
			ItemMatch m = (ItemMatch)o;
			/*
			if (m.items.size() == items.size()) {
				for (ItemStack is : items) {
					if (ReikaItemHelper.listContainsItemStack(m.items, is, false)) {

					}
					else {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
			 */
			return m.items.equals(items);
		}
		else {
			return false;
		}
	}

}
