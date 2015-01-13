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

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class ShapedNBTRecipe extends ShapedRecipes {

	public ShapedNBTRecipe(int w, int h, ItemStack[] in, ItemStack out) {
		super(w, h, in, out);
	}

	@Override
	protected boolean checkMatch(InventoryCrafting ic, int x, int y, boolean mirror) {
		for (int k = 0; k < 3; ++k) {
			for (int l = 0; l < 3; ++l) {
				int i1 = k - x;
				int j1 = l - y;
				ItemStack itemstack = null;

				if (i1 >= 0 && j1 >= 0 && i1 < recipeWidth && j1 < recipeHeight) {
					if (mirror)
						itemstack = recipeItems[recipeWidth - i1 - 1 + j1 * recipeWidth];
					else
						itemstack = recipeItems[i1 + j1 * recipeWidth];
				}

				ItemStack itemstack1 = ic.getStackInRowAndColumn(k, l);

				if (itemstack1 != null || itemstack != null) {

					if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
						return false;

					if (itemstack.getItem() != itemstack1.getItem())
						return false;

					if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage())
						return false;

					if (!ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
						return false;
				}
			}
		}

		return true;
	}

}
