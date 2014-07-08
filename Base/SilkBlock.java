/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public abstract class SilkBlock extends Block {

	public SilkBlock(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	public abstract ItemStack getItem(World world, int x, int y, int z);

	public abstract ArrayList<ItemStack> getPieces(World world, int x, int y, int z);

	@Override
	public final boolean canSilkHarvest() {
		return false;
	}

	@Override
	public final void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		boolean silk = EnchantmentHelper.getSilkTouchModifier(ep);
		ArrayList<ItemStack> li = new ArrayList();
		if (silk) {
			li.add(this.getItem(world, x, y, z));
		}
		else {
			li.addAll(this.getPieces(world, x, y, z));
		}
		ReikaItemHelper.dropItems(world, x, y, z, li);
	}

	@Override
	public final boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlock(x, y, z, 0);
	}

	protected abstract boolean canHarvest(World world, EntityPlayer player, int x, int y, int z);

	@Override
	public final ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		return this.getPieces(world, x, y, z);
	}
}
