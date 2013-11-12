/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

public final class ReikaBlockHelper extends DragonAPICore {

	/** Tests if a block always drops itself. Args: ID */
	public static boolean alwaysDropsSelf(int ID) {
		int k = 0;
		//for (k = 0; k <= 20; k++)
		for (int i = 0; i < 16; i++)
			if (ID != Block.blocksList[ID].idDropped(i, rand, k) && ID-256 != Block.blocksList[ID].idDropped(i, rand, k))
				return false;/*
		for (int i = 0; i < 16; i++)
			if (Block.blocksList[ID].damageDropped(i) != i)
				return false;*/
		return true;
	}

	/** Tests if a block never drops itself. Args: ID */
	public static boolean neverDropsSelf(int ID) {
		boolean hasID = false;
		boolean hasMeta = false;
		for (int k = 0; k <= 20 && !hasID; k++)
			for (int i = 0; i < 16 && !hasID; i++)
				if (ID == Block.blocksList[ID].idDropped(i, rand, k) || ID-256 == Block.blocksList[ID].idDropped(i, rand, k))
					hasID = true;/*
		for (int i = 0; i < 16 && !hasMeta; i++)
			if (Block.blocksList[ID].damageDropped(i) == i)*/
		hasMeta = true;
		return (hasID && hasMeta);
	}

	/** Returns true if the Block ID corresponds to an ore block. Args: ItemStack */
	public static boolean isOre(ItemStack is) {
		if (is == null)
			return false;
		if (is.itemID == Block.oreRedstoneGlowing.blockID)
			return true;
		if (ReikaOreHelper.isVanillaOre(is.itemID))
			return true;
		if (ModOreList.isModOre(is))
			return true;
		if (ReikaOreHelper.getEntryByOreDict(is) != null)
			return true;
		if (ReikaOreHelper.isExtraOre(is))
			return true;
		return false;
	}

	/** Returns true if the Block ID corresponds to an ore block. Args: ID, Metadata */
	public static boolean isOre(int id, int meta) {
		return isOre(new ItemStack(id, 1, meta));
	}

	/** Gets a world block as an itemstack. Args: World, x, y, z */
	public static ItemStack getWorldBlockAsItemStack(World world, int x, int y, int z) {
		return new ItemStack(world.getBlockId(x, y, z), 1, world.getBlockMetadata(x, y, z));
	}

	/** Get the block ID silverfish stone is imitating. Args; Metadata */
	public static int getSilverfishImitatedBlock(int meta) {
		switch(meta) {
		case 0:
			return Block.stone.blockID;
		case 1:
			return Block.cobblestone.blockID;
		case 2:
			return Block.stoneBrick.blockID;
		default:
			return 0;
		}
	}
}
