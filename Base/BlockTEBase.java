/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.FrameBlacklist;

import framesapi.IMoveCheck;

@Strippable("framesapi.IMoveCheck")
public abstract class BlockTEBase extends Block implements IMoveCheck {

	public BlockTEBase(Material mat) {
		super(mat);
	}

	@Override
	public abstract boolean hasTileEntity(int meta);

	@Override
	public abstract TileEntity createTileEntity(World world, int meta);

	@Override
	public final float getBlockHardness(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityBase && ((TileEntityBase)te).isUnMineable())
			return -1;
		return blockHardness;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b)
	{
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te != null)
			te.onBlockUpdate();
	}

	@Override
	public final void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ)
	{
		ForgeDirection dir = ReikaDirectionHelper.getDirectionBetween(x, y, z, tileX, tileY, tileZ);
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te != null)
			te.updateCache(dir);
	}

	public void updateTileCache(World world, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			te.updateCache(dir);
		}
	}

	public final AxisAlignedBB getBlockAABB(int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
	}

	protected final void setFullBlockBounds() {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	protected final void setBounds(AxisAlignedBB box, int x, int y, int z) {
		this.setBlockBounds((float)box.minX-x, (float)box.minY-y, (float)box.minZ-z, (float)box.maxX-x, (float)box.maxY-y, (float)box.maxZ-z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float par7, float par8, float par9) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		te.syncAllData(true);
		return false;
	}

	@Override
	public final boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public final int getComparatorInputOverride(World world, int x, int y, int z, int par5)
	{
		return ((TileEntityBase)world.getTileEntity(x, y, z)).getRedstoneOverride();
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e)
	{
		return false;
	}

	@Override
	public boolean canMove(World world, int x, int y, int z) {
		return !FrameBlacklist.instance.fireFrameEvent(world, x, y, z);
	}

}
