/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
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

	public static Block getBlock(World world, int x, int y, int z) {
		return Block.blocksList[world.getBlockId(x, y, z)];
	}

	/** Returns true if the block has a hitbox. Args: World, x, y, z */
	public static boolean isCollideable(World world, int x, int y, int z) {
		if (world.getBlockId(x, y, z) == 0)
			return false;
		Block b = Block.blocksList[world.getBlockId(x, y, z)];
		return (b.getCollisionBoundingBoxFromPool(world, x, y, z) != null);
	}

	/** Tests if a block is a dirt-type one, such that non-farm plants can grow on it. Args: id, metadata, material */
	public static boolean isDirtType(int id, int meta, Material mat) {
		if (id == Block.dirt.blockID)
			return true;
		if (id == Block.grass.blockID)
			return true;
		if (id == Block.gravel.blockID)
			return false;
		return false;
	}

	/** Tests if a block is a liquid block. Args: ID */
	public static boolean isLiquid(int id) {
		if (id == 0)
			return false;
		Block b = Block.blocksList[id];
		Material mat = b.blockMaterial;
		if (mat == Material.lava || mat == Material.water)
			return true;
		return b instanceof BlockFluid;
	}
}
