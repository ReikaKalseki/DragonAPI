/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
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
		String[] parts = s.split("/");
		for (String s2 : parts)
			this.addAll(OreDictionary.getOres(s2));
		if (items.isEmpty())
			throw new RegistrationException(DragonAPIInit.instance, "This recipe uses an OreDict tag with no registered items!");
	}

	public ItemMatch(Fluid f) {
		this(ReikaFluidHelper.getAllContainersFor(f));
	}

	public ItemMatch(Collection<ItemStack> c) {
		this.addAll(c);
		if (items.isEmpty())
			throw new RegistrationException(DragonAPIInit.instance, "This recipe uses an list with no items!");
	}

	public static ItemMatch createFromObject(Object o) {
		if (o instanceof Block) {
			return new ItemMatch((Block)o);
		}
		else if (o instanceof Item) {
			return new ItemMatch((Item)o);
		}
		else if (o instanceof ItemStack) {
			return new ItemMatch((ItemStack)o);
		}
		else if (o instanceof String) {
			return new ItemMatch((String)o);
		}
		else if (o instanceof Fluid) {
			return new ItemMatch((Fluid)o);
		}
		else {
			throw new IllegalArgumentException("Invalid item matching type!");
		}
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
		ks = ks.setSimpleHash(true).setIgnoreNBT(ks.getItemStack().stackTagCompound == null).lock();
		items.add(ks);
		//if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		ItemStack is2 = ks.getItemStack();
		displayList.add(is2);
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
		ItemStack ret = displayList.get((int)((System.currentTimeMillis()/2000+Math.abs(this.hashCode()))%displayList.size()));
		if (ret.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			ArrayList<ItemStack> li = new ArrayList();
			ret.getItem().getSubItems(ret.getItem(), ret.getItem().getCreativeTab(), li);
			int idx = (int)((System.currentTimeMillis()/500)%li.size());
			ret = li.get(idx);
		}
		return ret;
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
