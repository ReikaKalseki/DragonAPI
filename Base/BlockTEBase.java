package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockTEBase extends Block {

	public BlockTEBase(int id, Material mat) {
		super(id, mat);
	}

	@Override
	public final void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ)
	{
		TileEntityBase te = (TileEntityBase)world.getBlockTileEntity(x, y, z);
		te.updateCache(tileX, tileY, tileZ);
	}

}
