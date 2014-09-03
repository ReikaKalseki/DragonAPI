/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class ItemDrop {

	public final int minDrops;
	public final int maxDrops;

	private final ItemStack item;

	private static final Random rand = new Random();

	public ItemDrop(Block b) {
		this(b, 1, 1);
	}

	public ItemDrop(Block b, int min, int max) {
		this(new ItemStack(b), min, max);
	}

	public ItemDrop(Item i) {
		this(i, 1, 1);
	}

	public ItemDrop(Item i, int min, int max) {
		this(new ItemStack(i), min, max);
	}

	public ItemDrop(ItemStack is, int min, int max) {
		maxDrops = max;
		minDrops = min;
		item = is.copy();
	}

	public void enchant(HashMap<Enchantment, Integer> map) {
		ReikaEnchantmentHelper.applyEnchantments(item, map);
	}

	public void enchant(Enchantment ench, int level) {
		item.addEnchantment(ench, level);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ItemDrop) {
			ItemDrop it = (ItemDrop)o;
			if (!ReikaItemHelper.matchStacks(item, it.item))
				return false;
			return ItemStack.areItemStackTagsEqual(item, it.item);
		}
		return false;
	}

	public ItemStack getItem() {
		int num = minDrops+rand.nextInt(1+maxDrops-minDrops);
		ItemStack is = ReikaItemHelper.getSizedItemStack(item.copy(), num);
		return is;
	}

	public void drop(World world, double x, double y, double z) {
		ReikaItemHelper.dropItem(world, x, y, z, this.getItem());
	}

	public void drop(Entity e) {
		ReikaItemHelper.dropItem(e.worldObj, e.posX, e.posY+0.25, e.posZ, this.getItem());
	}

	public Item getID() {
		return item.getItem();
	}

	public int getMetadata() {
		return item.getItemDamage();
	}

	public boolean isEnchanted() {
		Map map = EnchantmentHelper.getEnchantments(item);
		return map != null && !map.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(item.getDisplayName());
		sb.append(": ");
		sb.append(item.getItem());
		sb.append(":");
		sb.append(item.getItemDamage());
		sb.append(" (");
		if (minDrops != maxDrops) {
			sb.append(minDrops);
			sb.append("-");
		}
		sb.append(maxDrops);
		sb.append(")");
		if (this.isEnchanted()) {
			sb.append("; ");
			sb.append(EnchantmentHelper.getEnchantments(item));
		}
		return sb.toString();
	}

}
