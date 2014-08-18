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
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class CropHandlerBase extends ModHandlerBase {

	public abstract int getHarvestedMeta(World world, int x, int y, int z);

	public abstract boolean isCrop(Block id);

	public abstract boolean isRipeCrop(World world, int x, int y, int z);

	public abstract void makeRipe(World world, int x, int y, int z);

	public abstract boolean isSeedItem(ItemStack is);

	public abstract float getSecondSeedDropRate();

	public abstract ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune);

	public void editTileDataForHarvest(World world, int x, int y, int z) {}


}
