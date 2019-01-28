/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Reika.DragonAPI.DragonAPICore;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ReikaEnchantmentHelper extends DragonAPICore {

	public static final Comparator<Enchantment> enchantmentNameSorter = new EnchantmentNameComparator();
	public static final Comparator<Enchantment> enchantmentTypeSorter = new EnchantmentTypeComparator();

	/** Get a listing of all enchantments on an ItemStack. Args: ItemStack */
	public static HashMap<Enchantment,Integer> getEnchantments(ItemStack is) {
		Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return null;
		HashMap<Enchantment,Integer> ench = new HashMap();
		for (Integer id : enchants.keySet()) {
			Enchantment e = Enchantment.enchantmentsList[id];
			int level = enchants.get(id);
			ench.put(e, level);
		}
		return ench;
	}

	public static void applyEnchantment(ItemStack is, Enchantment e, int level) {
		if (is.getItem() == Items.enchanted_book) {
			Items.enchanted_book.addEnchantment(is, new EnchantmentData(e, level));
		}
		else {
			is.addEnchantment(e, level);
		}
	}

	/** Applies all enchantments to an ItemStack. Args: ItemStack, enchantment map */
	public static void applyEnchantments(ItemStack is, HashMap<Enchantment,Integer> en) {
		if (en == null)
			return;
		for (Enchantment e : en.keySet()) {
			int level = en.get(e);
			if (level > 0) {
				applyEnchantment(is, e, level);
			}
		}
	}

	/** Returns the enchantment level of an ItemStack. Args: Enchantment, ItemStack */
	public static int getEnchantmentLevel(Enchantment e, ItemStack is) {
		if (is == null)
			return 0;
		Map enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return 0;
		if (enchants.containsKey(e.effectId)) {
			int level = (Integer)enchants.get(e.effectId);
			return level;
		}
		return 0;
	}

	/** Test whether an ItemStack has an enchantment. Args: Enchantment, ItemStack */
	public static boolean hasEnchantment(Enchantment e, ItemStack is) {
		if (is == null)
			return false;
		Map enchants = EnchantmentHelper.getEnchantments(is);
		if (enchants == null)
			return false;
		return enchants.containsKey(e.effectId);
	}

	/** Returns the speed bonus that efficiency that gives. Args: Level */
	public static float getEfficiencyMultiplier(int level) {
		return (float)Math.pow(1.3, level);
	}

	/** Returns true iff all the enchantments are compatible with each other. */
	public static boolean areCompatible(Collection<Enchantment> enchantments) {
		Iterator<Enchantment> it = enchantments.iterator();
		Iterator<Enchantment> it2 = enchantments.iterator();
		while (it.hasNext()) {
			Enchantment e = it.next();
			while (it2.hasNext()) {
				Enchantment e2 = it2.next();
				if (!areEnchantsCompatible(e, e2))
					return false;
			}
		}
		return true;
	}

	/** Returns true iff the new enchantment is compatible all the other enchantments. */
	public static boolean isCompatible(Collection<Enchantment> enchantments, Enchantment addition) {
		Iterator<Enchantment> it = enchantments.iterator();
		Iterator<Enchantment> it2 = enchantments.iterator();
		while (it.hasNext()) {
			Enchantment e = it.next();
			if (!areEnchantsCompatible(e, addition))
				return false;
		}
		return true;
	}

	public static boolean areEnchantsCompatible(Enchantment e, Enchantment e2) {
		return e.canApplyTogether(e2);
	}

	public static boolean hasEnchantments(ItemStack is) {
		Map map = EnchantmentHelper.getEnchantments(is);
		return map != null && !map.isEmpty();
	}

	private static class EnchantmentTypeComparator implements Comparator<Enchantment> {

		@Override
		public int compare(Enchantment o1, Enchantment o2) {
			return o1.type.ordinal()-o2.type.ordinal();
		}

	}

	private static class EnchantmentNameComparator implements Comparator<Enchantment> {

		@Override
		public int compare(Enchantment o1, Enchantment o2) {
			return o1.getTranslatedName(1).compareTo(o2.getTranslatedName(1));
		}

	}

	public static void addEnchantment(NBTTagCompound tag, Enchantment enchantment, int level, boolean append) {
		//ReikaJavaLibrary.pConsole("Pre: "+tag.getTag("ench"));
		if (!tag.hasKey("ench", 9))
			tag.setTag("ench", new NBTTagList());
		NBTTagList li = tag.getTagList("ench", 10);
		if (!append) {
			for (NBTTagCompound in : ((List<NBTTagCompound>)li.tagList)) {
				short type = in.getShort("id");
				if (type == enchantment.effectId) {
					in.setShort("lvl", (byte)level);
					return;
				}
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setShort("id", (short)enchantment.effectId);
		nbt.setShort("lvl", ((byte)level));
		li.appendTag(nbt);
		//ReikaJavaLibrary.pConsole("Post: "+tag.getTag("ench"));
	}

	public static void removeEnchantments(ItemStack is, Collection<Enchantment> c) {
		for (Enchantment e : c) {
			removeEnchantment(is, e);
		}
	}

	public static void removeEnchantment(ItemStack is, Enchantment e) {
		NBTTagList li = is.getEnchantmentTagList();
		if (li == null)
			return;
		Iterator<NBTTagCompound> it = li.tagList.iterator();
		while (it.hasNext()) {
			NBTTagCompound tag = it.next();
			short s = tag.getShort("id");
			if (s == e.effectId)
				it.remove();
		}
		if (li.tagList.isEmpty())
			is.stackTagCompound.removeTag("ench");
	}

	public static ItemStack getBasicItemForEnchant(Enchantment e) {
		switch(e.type) {
			case all:
				return new ItemStack(Items.diamond_pickaxe);
			case armor:
				return new ItemStack(Items.diamond_chestplate);
			case armor_feet:
				return new ItemStack(Items.iron_boots);
			case armor_head:
				return new ItemStack(Items.iron_helmet);
			case armor_legs:
				return new ItemStack(Items.iron_leggings);
			case armor_torso:
				return new ItemStack(Items.iron_chestplate);
			case bow:
				return new ItemStack(Items.bow);
			case breakable:
				return new ItemStack(Items.golden_pickaxe);
			case digger:
				return new ItemStack(Items.iron_shovel);
			case fishing_rod:
				return new ItemStack(Items.fishing_rod);
			case weapon:
				return new ItemStack(Items.iron_sword);
			default:
				break;
		}
		return new ItemStack(Blocks.dirt);
	}

	public static ItemStack getEnchantedBook(Enchantment e, int lvl) {
		ItemStack is = new ItemStack(Items.enchanted_book);
		HashMap<Enchantment, Integer> map = new HashMap();
		map.put(e, lvl);
		applyEnchantments(is, map);
		return is;
	}

	public static Enchantment getRandomEnchantment(EnumEnchantmentType cat, boolean modded) {
		int idx = rand.nextInt(Enchantment.enchantmentsList.length);
		Enchantment e = Enchantment.enchantmentsList[idx];
		while (e == null || (cat != null && e.type != cat) || (!modded && !isVanillaEnchant(e))) {
			idx = rand.nextInt(Enchantment.enchantmentsList.length);
			e = Enchantment.enchantmentsList[idx];
		}
		return e;
	}

	public static boolean isVanillaEnchant(Enchantment e) {
		return e.getClass().getName().startsWith("net.minecraft");
	}

	public static Enchantment getEnchantmentByName(String s) {
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if (e == null)
				continue;
			String n = e.getName();
			if (n.startsWith("enchantment."))
				n = n.substring("enchantment.".length());
			if (n.equalsIgnoreCase(s))
				return e;
		}
		return null;
	}
}
