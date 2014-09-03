/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Interfaces.EnumItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EnumCreativeTab extends CreativeTabs {

	private final String name;

	private final ArrayList<ItemStack> items = new ArrayList();

	private static final ItemSorter sorter = new ItemSorter();

	private static final class ItemSorter implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			ItemStack is1 = (ItemStack)o1;
			ItemStack is2 = (ItemStack)o2;
			Item i1 = is1.getItem();
			Item i2 = is2.getItem();
			if (i1 instanceof EnumItem && i2 instanceof EnumItem) {
				return ((Enum)((EnumItem)i1).getRegistry(is1)).ordinal() - ((Enum)((EnumItem)i2).getRegistry(is2)).ordinal();
			}
			if (i1 instanceof EnumItem) {
				return Integer.MAX_VALUE;
			}
			if (i2 instanceof EnumItem) {
				return Integer.MIN_VALUE;
			}
			return 0;
		}

	}

	public EnumCreativeTab(String name) {
		super(name);
		this.name = name;
	}

	@Override
	public final String getTabLabel()
	{
		return name;
	}

	@Override
	public final String getTranslatedTabLabel()
	{
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void displayAllReleventItems(List li) { //"Relevent"...
		//super.displayAllReleventItems(li);

		items.clear();
		List add = new ArrayList();
		if (items.isEmpty()) {
			super.displayAllReleventItems(add);
			Collections.sort(add, sorter);
			items.addAll(add);
		}
		li.addAll(items);
		/*
		RegistrationList[] list = this.getRegistry();
		for (int i = 0; i < list.length; i++) {
			RegistrationList r = list[i];
			Item item = r instanceof ItemEnum ? ((ItemEnum)r).getItemInstance() : Item.getItemFromBlock(((BlockEnum)r).getBlockInstance());
			CreativeTabs[] c = item.getCreativeTabs();
			for (int k = 0; k < c.length; k++) {
				if (c[k] == this)
					item.getSubItems(item, this, li);
			}
		}
		 */
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final Item getTabIconItem() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract ItemStack getIconItemStack();
}
