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

	/** Returns true if the Block ID corresponds to an ore block. Args: ID */
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
		return false;
	}

	public static boolean isOre(int id, int meta) {
		return isOre(new ItemStack(id, 1, meta));
	}

	public static boolean canSilkTouch(int id, int meta) {
		if (isOre(id, meta))
			return true;
		if (id == Block.stone.blockID)
			return true;
		if (id == Block.grass.blockID)
			return true;
		if (id == Block.glass.blockID)
			return true;
		if (id == Block.glowStone.blockID)
			return true;
		if (id == Block.thinGlass.blockID)
			return true;
		if (id == Block.ice.blockID)
			return true;
		if (id == Block.leaves.blockID)
			return true;
		if (id == Block.silverfish.blockID)
			return true;
		return false;
	}

	public static ItemStack getWorldBlockAsItemStack(World world, int x, int y, int z) {
		return new ItemStack(world.getBlockId(x, y, z), 1, world.getBlockMetadata(x, y, z));
	}

	public static boolean isRail(int id) {
		if (id == Block.rail.blockID)
			return true;
		if (id == Block.railActivator.blockID)
			return true;
		if (id == Block.railDetector.blockID)
			return true;
		if (id == Block.railPowered.blockID)
			return true;
		return false;
	}

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
