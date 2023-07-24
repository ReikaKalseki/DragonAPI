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

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.Registry.TileEnum;

public abstract class BlockTileEnum<T extends TileEntityBase, E extends TileEnum> extends BlockTEBase {

	public BlockTileEnum(Material mat) {
		super(mat);
	}

	public abstract E getMapping(int meta);

	public abstract E getMapping(IBlockAccess world, int x, int y, int z);

	@Override
	public abstract T createTileEntity(World world, int meta);

}
