/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.CropType;

public enum ReikaCropHelper implements CropType {

	WHEAT(Blocks.wheat, 7, Items.wheat_seeds),
	CARROT(Blocks.carrots, 7, Items.carrot),
	POTATO(Blocks.potatoes, 7, Items.potato),
	NETHERWART(Blocks.nether_wart, 3, Items.nether_wart),
	COCOA(Blocks.cocoa, 2, ReikaItemHelper.cocoaBeans);

	public final Block blockID;
	public final int ripeMeta;
	private final ItemStack seedItem;

	public static final ReikaCropHelper[] cropList = values();

	private static final HashMap<Block, ReikaCropHelper> cropMappings = new HashMap();

	private ReikaCropHelper(Block id, int metaripe, Item seed) {
		this(id, metaripe, new ItemStack(seed));
	}

	private ReikaCropHelper(Block id, int metaripe, ItemStack seed) {
		blockID = id;
		ripeMeta = metaripe;
		seedItem = seed;
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		int meta = world.getBlockMetadata(x, y, z);
		return blockID.getDrops(world, x, y, z, meta, fortune);
	}

	public static ReikaCropHelper getCrop(Block id) {
		return cropMappings.get(id);
	}

	public static boolean isCrop(Block id) {
		return getCrop(id) != null;
	}

	public boolean destroyOnHarvest() {
		return false;
	}

	public boolean isRipe(World world, int x, int y, int z) {
		return this.isRipe(world.getBlockMetadata(x, y, z));
	}

	public boolean isRipe(int meta) {
		if (this == COCOA)
			meta /= 4;
		return meta == ripeMeta;
	}

	public int getHarvestedMeta(int meta_ripe) {
		if (this == COCOA)
			return meta_ripe&3;
		return 0;
	}

	static {
		for (int i = 0; i < cropList.length; i++) {
			ReikaCropHelper w = cropList[i];
			Block id = w.blockID;
			cropMappings.put(id, w);
		}
	}

	@Override
	public boolean existsInGame() {
		return true;
	}

	@Override
	public void setHarvested(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, 0, 3);
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, ripeMeta, 3);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return ReikaItemHelper.matchStacks(is, seedItem);
	}

	public ItemStack getSeedItem() {
		return seedItem.copy();
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == blockID;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return false;
	}


}
