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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum ReikaCropHelper {

	WHEAT(Blocks.wheat, 7),
	CARROT(Blocks.carrots, 7),
	POTATO(Blocks.potatoes, 7),
	NETHERWART(Blocks.nether_wart, 3),
	COCOA(Blocks.cocoa, 2);

	public final Block blockID;
	public final int ripeMeta;

	public static final ReikaCropHelper[] cropList = values();

	private static final HashMap<Block, ReikaCropHelper> cropMappings = new HashMap();

	private ReikaCropHelper(Block id, int metaripe) {
		blockID = id;
		ripeMeta = metaripe;
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


}