/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class SortedCreativeTab extends CreativeTabs {

	private final String name;

	public SortedCreativeTab(String name) {
		super(name);
		this.name = name;
	}

	@Override
	public final String getTabLabel() {
		return name;
	}

	@Override
	public final String getTranslatedTabLabel() {
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void displayAllReleventItems(List li) { //"Relevent"...
		List add = new ArrayList();
		super.displayAllReleventItems(add);
		Collections.sort(add, this.getComparator());
		li.addAll(add);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final Item getTabIconItem() {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract ItemStack getIconItemStack();

	protected abstract Comparator<ItemStack> getComparator();

}
