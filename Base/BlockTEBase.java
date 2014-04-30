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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public abstract class BlockTEBase extends Block {

	public BlockTEBase(int id, Material mat) {
		super(id, mat);
	}

	@Override
	public abstract boolean hasTileEntity(int meta);

	@Override
	public abstract TileEntity createTileEntity(World world, int meta);

	@Override
	public final void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ)
	{
		ForgeDirection dir = ReikaDirectionHelper.getDirectionBetween(x, y, z, tileX, tileY, tileZ);
		TileEntityBase te = (TileEntityBase)world.getBlockTileEntity(x, y, z);
		if (te != null)
			te.updateCache(dir);
	}

	public final AxisAlignedBB getBlockAABB(int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
	}

	public final void setFullBlockBounds() {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float par7, float par8, float par9) {
		TileEntityBase te = (TileEntityBase)world.getBlockTileEntity(x, y, z);
		te.syncAllData();
		return false;
	}

	@Override
	public final boolean hasComparatorInputOverride()
	{
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
	 * strength when this block inputs to a comparator.
	 */
	@Override
	public final int getComparatorInputOverride(World world, int x, int y, int z, int par5)
	{
		return ((TileEntityBase)world.getBlockTileEntity(x, y, z)).getRedstoneOverride();
	}

}
