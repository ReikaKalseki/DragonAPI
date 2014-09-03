/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI.Slot;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class SlotApprovedItems extends Slot {

	private final ArrayList<ItemStack> items = new ArrayList();

	public SlotApprovedItems(IInventory ii, int par2, int par3, int par4) {
		super(ii, par2, par3, par4);
	}

	public SlotApprovedItems addItem(Item i) {
		return this.addItem(new ItemStack(i));
	}

	public SlotApprovedItems addItem(Block b) {
		return this.addItem(new ItemStack(b));
	}

	public SlotApprovedItems addItem(ItemStack is) {
		if (!ReikaItemHelper.listContainsItemStack(items, is))
			items.add(is);
		return this;
	}

	public SlotApprovedItems addItems(ItemStack... is) {
		for (int i = 0; i < is.length; i++)
			this.addItem(is[i]);
		return this;
	}

	public SlotApprovedItems addItems(ArrayList<ItemStack> li) {
		for (int i = 0; i < li.size(); i++)
			this.addItem(li.get(i));
		return this;
	}

	@Override
	public boolean isItemValid(ItemStack is)
	{
		return ReikaItemHelper.listContainsItemStack(items, is);
	}

}
