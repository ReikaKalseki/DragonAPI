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
import Reika.DragonAPI.Interfaces.CustomCropHandler;
import Reika.DragonAPI.Interfaces.ModEntry;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public final class SimpleCropHandler implements CustomCropHandler {

	public final Block crop;
	public final int ripeMeta;
	public final int harvestMeta;
	private final ItemStack seedItem;

	private final ModEntry mod;
	private final int color;
	private final String name;

	public SimpleCropHandler(ModEntry mod, int color, String name, Block b, int ripe, ItemStack seed) {
		this(mod, color, name, b, 0, ripe, seed);
	}

	public SimpleCropHandler(ModEntry mod, int color, String name, Block b, int harvest, int ripe, ItemStack seed) {
		crop = b;
		ripeMeta = ripe;
		harvestMeta = harvest;
		seedItem = seed;

		this.mod = mod;
		this.color = color;
		this.name = name;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return harvestMeta;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == crop;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.isCrop(world.getBlock(x, y, z), meta) && meta == ripeMeta;
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

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public ModEntry getMod() {
		return mod;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public String getEnumEntryName() {
		return name;
	}

	@Override
	public boolean isTileEntity() {
		return false;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return false;
	}

}
