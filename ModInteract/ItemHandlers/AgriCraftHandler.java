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
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import com.InfinityRaider.AgriCraft.api.v1.ICropPlant;

public class AgriCraftHandler extends CropHandlerBase {

	private static final AgriCraftHandler instance = new AgriCraftHandler();

	private final APIv1 api;
	private final int GROWN = 7;

	private final HashSet<Block> cropBlocks = new HashSet();

	private AgriCraftHandler() {
		super();
		if (this.getMod().isLoaded()) {
			APIBase a = API.getAPI(1);
			if (a.getStatus().isOK() && a.getVersion() == 1) {
				api = (APIv1)a;
				cropBlocks.addAll(api.getCropsBlocks());
			}
			else {
				api = null;
			}
		}
		else {
			api = null;
		}
	}

	public static AgriCraftHandler getInstance() {
		return instance;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 2;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return cropBlocks.contains(id);
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return this.getGrowthState(world, x, y, z) == GROWN;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, GROWN, 3);
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return api.isHandledByAgricraft(is);
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		int gain = api.getStats(world, x, y, z).getGain();
		ICropPlant plant = api.getCropPlant(world, x, y, z);
		return gain > 0 && plant != null ? plant.getFruitsOnHarvest(gain, world.rand) : new ArrayList();
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return true;
	}

	@Override
	public boolean initializedProperly() {
		return api != null;
	}

	@Override
	public ModList getMod() {
		return ModList.AGRICRAFT;
	}

}
