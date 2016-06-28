/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data.immutable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.interfaces.registry.BlockEnum;
import reika.dragonapi.interfaces.registry.ItemEnum;
import reika.dragonapi.libraries.registry.ReikaItemHelper;

public final class ImmutableItemStack {

	private final ItemStack data;

	public ImmutableItemStack(Item i) {
		this(new ItemStack(i));
	}

	public ImmutableItemStack(Block b) {
		this(new ItemStack(b));
	}

	public ImmutableItemStack(ItemEnum i) {
		this(new ItemStack(i.getItemInstance()));
	}

	public ImmutableItemStack(BlockEnum b) {
		this(new ItemStack(b.getBlockInstance()));
	}

	public ImmutableItemStack(Item i, int num) {
		this(new ItemStack(i, num));
	}

	public ImmutableItemStack(Block b, int num) {
		this(new ItemStack(b, num));
	}

	public ImmutableItemStack(Item i, int num, int meta) {
		this(new ItemStack(i, num, meta));
	}

	public ImmutableItemStack(Block b, int num, int meta) {
		this(new ItemStack(b, num, meta));
	}

	public ImmutableItemStack(ItemStack is) {
		data = is.copy();
	}

	public int stackSize() {
		return data.stackSize;
	}

	public int getItemDamage() {
		return data.getItemDamage();
	}

	public Item getItem() {
		return data.getItem();
	}

	public int getMaxStackSize() {
		return data.getMaxStackSize();
	}

	public ItemStack getItemStack() {
		return data.copy();
	}

	public boolean match(ItemStack is) {
		return ReikaItemHelper.matchStacks(is, this.getItemStack());
	}

	public boolean match(ImmutableItemStack is) {
		return ReikaItemHelper.matchStacks(is.getItemStack(), this.getItemStack());
	}

}
