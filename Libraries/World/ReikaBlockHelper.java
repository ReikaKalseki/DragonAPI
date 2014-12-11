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
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Extras.BlockProperties;
import Reika.DragonAPI.Interfaces.SpecialOreBlock;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModInteract.MystCraftHandler;
import Reika.DragonAPI.ModInteract.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModOreList;

public final class ReikaBlockHelper extends DragonAPICore {

	public static boolean matchMaterialsLoosely(Material m1, Material m2) {
		if (m1 == m2)
			return true;
		if (m1 == Material.ice && m2 == Material.packedIce)
			return true;
		if (m2 == Material.ice && m1 == Material.packedIce)
			return true;
		if (m1 == Material.snow && m2 == Material.craftedSnow)
			return true;
		if (m2 == Material.snow && m1 == Material.craftedSnow)
			return true;
		if (m1.getMaterialMapColor() == MapColor.foliageColor && m2.getMaterialMapColor() == MapColor.foliageColor)
			return true;
		return false;
	}

	/** Tests if a block always drops itself. Args: ID */
	public static boolean alwaysDropsSelf(Block ID) {
		int k = 0;
		//for (k = 0; k <= 20; k++)
		for (int i = 0; i < 16; i++)
			if (Item.getItemFromBlock(ID) != ID.getItemDropped(i, rand, k))
				return false;/*
		for (int i = 0; i < 16; i++)
			if (Blocks.blocksList[ID].damageDropped(i) != i)
				return false;*/
		return true;
	}

	/** Tests if a block never drops itself. Args: ID */
	public static boolean neverDropsSelf(Block ID) {
		boolean hasID = false;
		boolean hasMeta = false;
		for (int k = 0; k <= 20 && !hasID; k++)
			for (int i = 0; i < 16 && !hasID; i++)
				if (Item.getItemFromBlock(ID) == ID.getItemDropped(i, rand, k))
					hasID = true;/*
		for (int i = 0; i < 16 && !hasMeta; i++)
			if (Blocks.blocksList[ID].damageDropped(i) == i)*/
		hasMeta = true;
		return (hasID && hasMeta);
	}

	/** Returns true if the Block ID corresponds to an ore Blocks. Args: ItemStack */
	public static boolean isOre(ItemStack is) {
		if (is == null)
			return false;
		if (is.getItem() == Item.getItemFromBlock(Blocks.lit_redstone_ore))
			return true;
		if (Block.getBlockFromItem(is.getItem()) instanceof SpecialOreBlock)
			return true;
		if (ReikaOreHelper.isVanillaOre(is))
			return true;
		if (ModOreList.isModOre(is))
			return true;
		if (ReikaOreHelper.getEntryByOreDict(is) != null)
			return true;
		if (ReikaOreHelper.isExtraOre(is))
			return true;
		return false;
	}

	/** Returns true if the Block ID corresponds to an ore Blocks. Args: ID, Metadata */
	public static boolean isOre(Block id, int meta) {
		return isOre(new ItemStack(id, 1, meta));
	}

	/** Gets a world block as an itemstack. Args: World, x, y, z */
	public static ItemStack getWorldBlockAsItemStack(World world, int x, int y, int z) {
		return new ItemStack(world.getBlock(x, y, z), 1, world.getBlockMetadata(x, y, z));
	}

	/** Get the block ID silverfish stone is imitating. Args; Metadata */
	public static Block getSilverfishImitatedBlock(int meta) {
		switch(meta) {
		case 0:
			return Blocks.stone;
		case 1:
			return Blocks.cobblestone;
		case 2:
			return Blocks.stonebrick;
		default:
			return Blocks.air;
		}
	}

	/** Returns true if the block has a hitbox. Args: World, x, y, z */
	public static boolean isCollideable(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return false;
		return (b.getCollisionBoundingBoxFromPool(world, x, y, z) != null && !BlockProperties.isNonSolid(b));
	}

	/** Tests if a block is a dirt-type one, such that non-farm plants can grow on it. Args: id, metadata, material */
	public static boolean isDirtType(Block id, int meta, Material mat) {
		if (id == Blocks.dirt)
			return true;
		if (id == Blocks.grass)
			return true;
		if (id == Blocks.gravel)
			return false;
		return false;
	}

	/** Tests if a block is a liquid Blocks. Args: ID */
	public static boolean isLiquid(Block b) {
		if (b == Blocks.air)
			return false;
		Material mat = b.getMaterial();
		if (mat == Material.lava || mat == Material.water)
			return true;
		return b instanceof BlockLiquid;
	}

	public static boolean isPortalBlock(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		if (id == Blocks.portal)
			return true;
		if (id == Blocks.end_portal)
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && id == MystCraftHandler.getInstance().portalID)
			return true;
		if (ModList.TWILIGHT.isLoaded() && id == TwilightForestHandler.getInstance().portalID)
			return true;
		return false;
	}

	public static boolean isStairBlock(Block id) {
		if (id == Blocks.stone_stairs)
			return true;
		if (id == Blocks.stone_brick_stairs)
			return true;
		if (id == Blocks.brick_stairs)
			return true;
		if (id == Blocks.sandstone_stairs)
			return true;
		if (id == Blocks.oak_stairs)
			return true;
		if (id == Blocks.nether_brick_stairs)
			return true;
		if (id == Blocks.spruce_stairs)
			return true;
		if (id == Blocks.birch_stairs)
			return true;
		if (id == Blocks.jungle_stairs)
			return true;
		if (id == Blocks.quartz_stairs)
			return true;
		return false;
	}
}
