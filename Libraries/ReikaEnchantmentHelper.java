/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * 
 * Distribution of the software in any form is only allowed
 * with explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class ReikaEnchantmentHelper {

	public static HashMap<Enchantment,Integer> getEnchantments(ItemStack is) {
		Map enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return null;
		HashMap<Enchantment,Integer> ench = new HashMap<Enchantment,Integer>();
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				if (enchants.containsKey(Enchantment.enchantmentsList[i].effectId)) {
					int level = (Integer)enchants.get(Enchantment.enchantmentsList[i].effectId);
					ench.put(Enchantment.enchantmentsList[i], level);
				}
				else {
					ench.put(Enchantment.enchantmentsList[i], 0);
				}
			}
		}
		return ench;
	}

	public static int getEnchantmentLevel(Enchantment e, ItemStack is) {
		Map enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return 0;
		if (enchants.containsKey(e.effectId)) {
			int level = (Integer)enchants.get(e.effectId);
			return level;
		}
		return 0;
	}

	public static boolean hasEnchantment(Enchantment e, ItemStack is) {
		Map enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return false;
		return (enchants.containsKey(e.effectId));
	}

	public static float getEfficiencyMultiplier(int level) {
		return (float)Math.pow(1.3, level);
	}

}
