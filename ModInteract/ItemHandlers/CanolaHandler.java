/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.CropHandler;
import Reika.RotaryCraft.Blocks.BlockCanola;
import Reika.RotaryCraft.Registry.BlockRegistry;
import Reika.RotaryCraft.Registry.ItemRegistry;

public final class CanolaHandler implements CropHandler {

	public CanolaHandler() {

	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean isCrop(Block id) {
		return id == BlockRegistry.CANOLA.getBlockInstance();
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return this.isCrop(world.getBlock(x, y, z)) && world.getBlockMetadata(x, y, z) == BlockCanola.GROWN;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, BlockCanola.GROWN, 3);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return ItemRegistry.CANOLA.matchItem(is) && is.getItemDamage() == 0;
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
		return ModList.ROTARYCRAFT.isLoaded() && BlockRegistry.CANOLA.getBlockInstance() != null;
	}

}
