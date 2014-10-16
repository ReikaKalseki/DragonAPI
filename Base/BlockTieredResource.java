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
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;

public abstract class BlockTieredResource extends Block {

	protected static final Random rand = new Random();

	public BlockTieredResource(Material mat) {
		super(mat);
	}

	@Override
	public final Item getItemDropped(int meta, Random r, int fortune) {
		return null;
	}

	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	protected final boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public final int quantityDropped(int meta, int fortune, Random random)
	{
		return 0;
	}

	@Override
	protected final void dropBlockAsItem(World world, int x, int y, int z, ItemStack is) {

	}

	@Override
	public final ItemStack createStackedBlock(int meta) {
		return null;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return ReikaBlockHelper.getWorldBlockAsItemStack(world, x, y, z);
	}

	@Override
	public final boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return false;
	}

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		if (player.capabilities.isCreativeMode) {

		}
		else {
			int fortune = EnchantmentHelper.getFortuneModifier(player);
			if (this.isPlayerSufficientTier(world, x, y, z, player)) {
				Collection<ItemStack> li = this.getHarvestResources(world, x, y, z, fortune);
				if (li != null) {
					for (ItemStack is : li) {
						ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), is);
					}
				}
			}
			else {
				Collection<ItemStack> li = this.getNoHarvestResources(world, x, y, z, fortune);
				if (li != null) {
					for (ItemStack is : li) {
						ReikaItemHelper.dropItem(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), is);
					}
				}
			}
		}
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	public abstract Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune);

	public Collection<ItemStack> getNoHarvestResources(World world, int x, int y, int z, int fortune) {
		return null;
	}

	public abstract boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep);

	@Override
	public final void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase elb, ItemStack is) {
		if (elb instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)elb;
			if (!this.isPlayerSufficientTier(world, x, y, z, ep)) {
				ReikaRenderHelper.spawnDropParticles(world, x, y, z, this, world.getBlockMetadata(x, y, z));
				ReikaSoundHelper.playBreakSound(world, x, y, z, this);
				world.setBlockToAir(x, y, z);
			}
		}
	}

}
