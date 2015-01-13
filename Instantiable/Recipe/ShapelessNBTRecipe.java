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
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class ShapelessNBTRecipe extends ShapelessRecipes {

	public ShapelessNBTRecipe(ItemStack is, Object... in) {
		super(is, getList(in));
	}

	private static List<ItemStack> getList(Object... in) {
		ArrayList<ItemStack> li = new ArrayList();
		for (int i = 0; i < in.length; i++) {
			Object o = in[i];
			if (o instanceof ItemStack) {
				li.add((ItemStack)o);
			}
			else if (o instanceof Block) {
				li.add(new ItemStack((Block)o));
			}
			else if (o instanceof Item) {
				li.add(new ItemStack((Item)o));
			}
			else if (o == null)
				throw new IllegalArgumentException("You cannot use a null item in the recipe!");
			else
				throw new IllegalArgumentException("You cannot use an item of type "+o.getClass()+" in the recipe!");
		}
		return li;
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world)
	{
		ArrayList arraylist = new ArrayList(recipeItems);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				ItemStack itemstack = ic.getStackInRowAndColumn(j, i);

				if (itemstack != null) {
					boolean flag = false;
					Iterator iterator = arraylist.iterator();
					while (iterator.hasNext()) {
						ItemStack itemstack1 = (ItemStack)iterator.next();

						if (itemstack.getItem() == itemstack1.getItem() && (itemstack1.getItemDamage() == 32767 || itemstack.getItemDamage() == itemstack1.getItemDamage())) {
							if (ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
								flag = true;
								arraylist.remove(itemstack1);
								break;
							}
						}
					}

					if (!flag)
						return false;
				}
			}
		}

		return arraylist.isEmpty();
	}

}
