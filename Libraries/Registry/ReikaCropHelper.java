/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum ReikaCropHelper {

	WHEAT(Block.crops.blockID, 7, 0),
	CARROT(Block.carrot.blockID, 7, 0),
	POTATO(Block.potato.blockID, 7, 0),
	NETHERWART(Block.netherStalk.blockID, 4, 0),
	COCOA(Block.cocoaPlant.blockID, 3, 0);

	public final int blockID;
	public final int ripeMeta;
	public final int harvestedMeta;

	public static final ReikaCropHelper[] cropList = values();

	private ReikaCropHelper(int id, int metaripe, int metaharvest) {
		blockID = id;
		ripeMeta = metaripe;
		harvestedMeta = metaharvest;
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune) {
		int meta = world.getBlockMetadata(x, y, z);
		return Block.blocksList[blockID].getBlockDropped(world, x, y, z, meta, fortune);
	}

	public static ReikaCropHelper getCrop(int id) {
		for (int i = 0; i < cropList.length; i++) {
			ReikaCropHelper crop = cropList[i];
			if (crop.blockID == id)
				return crop;
		}
		return null;
	}

	public static boolean isCrop(int id) {
		return getCrop(id) != null;
	}

	public boolean destroyOnHarvest() {
		return true;
	}

	public boolean isRipe(int meta) {
		return meta >= ripeMeta;
	}


}
