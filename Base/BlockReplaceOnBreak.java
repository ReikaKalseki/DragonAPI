package Reika.DragonAPI.Base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public abstract class BlockReplaceOnBreak extends Block {

	protected BlockReplaceOnBreak(Material mat) {
		super(mat);
	}
	/*
	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		//ReikaBlockHelper.doBlockHarvest(world, ep, x, y, z, meta, this, this.harvesters);
	}*/

	@Override
	public final boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		int meta = world.getBlockMetadata(x, y, z);
		Block put = this.getBlockReplacedWith(player, x, y, z, meta, willHarvest);
		int pm = this.getMetaReplacedWith(player, x, y, z, meta, willHarvest);
		if (put == this)
			return world.setBlockMetadataWithNotify(x, y, z, pm, 3);
		else
			return world.setBlock(x, y, z, put, pm, 3);
	}

	public abstract Block getBlockReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest);
	public abstract int getMetaReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest);

	public class BlockReplaceSelfOnBreak extends BlockReplaceOnBreak {

		protected BlockReplaceSelfOnBreak(Material mat) {
			super(mat);
		}

		@Override
		public final Block getBlockReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest) {
			return this;
		}

		@Override
		public final int getMetaReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest) {
			return oldMeta;
		}

	}

}
