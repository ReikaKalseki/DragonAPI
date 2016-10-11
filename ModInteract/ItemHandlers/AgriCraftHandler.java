/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v2.APIv2;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.ICropPlant;

public class AgriCraftHandler extends CropHandlerBase {

	private static final AgriCraftHandler instance = new AgriCraftHandler();

	private static final WeightedRandom<Integer> tallGrassRand = new WeightedRandom();

	private Object api;
	private final int GROWN = 7;

	private final HashSet<Block> cropBlocks = new HashSet();

	private AgriCraftHandler() {
		super();
		try {
			if (this.getMod().isLoaded()) {
				APIBase a = API.getAPI(2);
				if (a != null && a.getStatus().isOK() && a.getVersion() == 2) {
					api = a;
					cropBlocks.addAll(((APIv2)api).getCropsBlocks());
				}
				else {
					api = null;
				}
			}
			else {
				api = null;
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
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
		return api != null && ((APIv2)api).isHandledByAgricraft(is);
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		if (api == null)
			return null;
		APIv2 apiv2 = (APIv2)api;
		int gain = apiv2.getStats(world, x, y, z).getGain();
		ICropPlant plant = apiv2.getCropPlant(world, x, y, z);
		return gain > 0 && plant != null ? plant.getFruitsOnHarvest(gain, world.rand) : new ArrayList();
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof ICrop) {
			ICrop ic = (ICrop)te;
			if (ic.isMature() && ic.hasWeed()) {
				li.add(new ItemStack(Blocks.tallgrass, 1, tallGrassRand.getRandomEntry()));
			}
		}
		return li;
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

	@ModDependent(ModList.AGRICRAFT)
	public ICropPlant getCropObject(World world, int x, int y, int z) {
		if (api == null)
			return null;
		APIv2 apiv2 = (APIv2)api;
		return apiv2.getCropPlant(world, x, y, z);
	}

	static {
		tallGrassRand.addEntry(1, 20);
		tallGrassRand.addEntry(2, 1);
	}

}
