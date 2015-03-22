/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.CropHandler;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class SimpleCropHandler implements CropHandler {

	public final Block crop;
	public final int ripeMeta;
	public final int harvestMeta;
	private final ItemStack seedItem;

	public SimpleCropHandler(Block b, int ripe, ItemStack seed) {
		this(b, 0, ripe, seed);
	}

	public SimpleCropHandler(Block b, int harvest, int ripe, ItemStack seed) {
		crop = b;
		ripeMeta = ripe;
		harvestMeta = harvest;
		seedItem = seed;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return harvestMeta;
	}

	@Override
	public boolean isCrop(Block id) {
		return id == crop;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return this.isCrop(world.getBlock(x, y, z)) && world.getBlockMetadata(x, y, z) == ripeMeta;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, ripeMeta, 3);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return ReikaItemHelper.matchStacks(is, seedItem);
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return new ArrayList();
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {

	}

	@Override
	public boolean initializedProperly() {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

}
