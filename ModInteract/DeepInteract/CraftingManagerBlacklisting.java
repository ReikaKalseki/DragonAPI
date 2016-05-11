/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.Registry.RegistrationList;
import Reika.DragonAPI.Interfaces.Registry.RegistryEntry;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import com.creativemd.craftingmanager.api.utils.sorting.ItemSortingList;
import com.creativemd.craftingmanager.api.utils.sorting.SortingItem;
import com.creativemd.craftingmanager.api.utils.sorting.items.BlockSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemSorting;
import com.creativemd.craftingmanager.api.utils.sorting.items.ItemStackSorting;

public class CraftingManagerBlacklisting {

	private static ItemSortingList blacklist;

	public static void registerItem(Item sort) {
		registerItem(new ItemSorting(sort));
	}

	public static void registerItem(Block sort) {
		registerItem(new BlockSorting(sort));
	}

	public static void registerItem(ItemStack sort) {
		registerItem(new ItemStackSorting(sort));
	}

	public static void registerItem(SortingItem sort) {
		blacklist.add(sort);
	}

	static {
		try {
			Class c = Class.forName("com.creativemd.craftingmanager.mod.CraftingManagerMod");
			Field f = c.getField("forbiddenOutputs");
			blacklist = (ItemSortingList)f.get(null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ModSorting extends SortingItem {

		public final String modID;

		public ModSorting(DragonAPIMod mod) {
			if (mod == null)
				throw new MisuseException("You cannot block items from a null mod ID!");
			String id = mod.getModContainer().getModId();
			modID = id;
		}

		@Override
		protected boolean isObject(ItemStack stack) {
			return ReikaItemHelper.isItemAddedByMod(stack.getItem(), modID);
		}

	}

	public static class RegistrySorting extends SortingItem {

		public final Class<? extends RegistrationList> registryEnum;

		public RegistrySorting(Class<? extends RegistrationList> reg) {
			if (reg == null)
				throw new MisuseException("You cannot block items from a null registry!");
			registryEnum = reg;
		}

		@Override
		protected boolean isObject(ItemStack stack) {
			RegistryEntry reg = ReikaRegistryHelper.getRegistryForObject(stack.getItem());
			return reg != null && reg.getClass() == registryEnum;
		}

	}

}
