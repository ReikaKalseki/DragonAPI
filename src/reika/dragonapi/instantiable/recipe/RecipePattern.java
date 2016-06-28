/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import reika.dragonapi.libraries.registry.ReikaItemHelper;

public final class RecipePattern extends InventoryCrafting {

	private static final BlankContainer craft = new BlankContainer();

	private static final class BlankContainer extends Container {
		@Override
		public final void onCraftMatrixChanged(IInventory inventory) {}
		@Override
		public final boolean canInteractWith(EntityPlayer player) {return false;}
		@Override
		public final void onContainerClosed(EntityPlayer par1EntityPlayer) {}
	}

	public RecipePattern(ItemStack... items) {
		super(craft, 3, 3);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.setInventorySlotContents(i*3+j, items[i*3+j]); //no//since will otherwise add vertically
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RecipePattern) {
			for (int i = 0; i < 9; i++) {
				if (!ReikaItemHelper.matchStacks(this.getStackInSlot(i), ((RecipePattern) o).getStackInSlot(i)))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public final void openInventory() {}
	@Override
	public final void closeInventory() {}
}
