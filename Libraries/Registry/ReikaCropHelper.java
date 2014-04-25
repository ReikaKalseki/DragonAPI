/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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

	WHEAT(Block.crops.blockID, 7),
	CARROT(Block.carrot.blockID, 7),
	POTATO(Block.potato.blockID, 7),
	NETHERWART(Block.netherStalk.blockID, 3),
	COCOA(Block.cocoaPlant.blockID, 2);

	public final int blockID;
	public final int ripeMeta;

	public static final ReikaCropHelper[] cropList = values();

	private ReikaCropHelper(int id, int metaripe) {
		blockID = id;
		ripeMeta = metaripe;
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
		return false;
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


}
