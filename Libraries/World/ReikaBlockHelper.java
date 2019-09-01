/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Extras.BlockProperties;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Interfaces.Block.SpecialOreBlock;
import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ChiselBlockHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.MystCraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThaumItemHelper.BlockEntry;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public final class ReikaBlockHelper extends DragonAPICore {

	private static final BlockMap<ItemStack> silkTouchDrops = new BlockMap();

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

	public static boolean isWood(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b instanceof BlockLog)
			return true;
		int meta = world.getBlockMetadata(x, y, z);
		if (ReikaTreeHelper.getTree(b, meta) != null)
			return true;
		if (ModWoodList.getModWood(b, meta) != null)
			return true;
		return false;
	}

	public static boolean isLeaf(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b instanceof BlockLeavesBase)
			return true;
		int meta = world.getBlockMetadata(x, y, z);
		if (ReikaTreeHelper.getTreeFromLeaf(b, meta) != null)
			return true;
		if (ModWoodList.getModWoodFromLeaf(b, meta) != null)
			return true;
		return false;
	}

	public static boolean isWood(Block b, int meta) {
		if (b instanceof BlockLog)
			return true;
		if (ReikaTreeHelper.getTree(b, meta) != null)
			return true;
		if (ModWoodList.getModWood(b, meta) != null)
			return true;
		return false;
	}

	public static boolean isLeaf(Block b, int meta) {
		if (b instanceof BlockLeavesBase)
			return true;
		if (ReikaTreeHelper.getTreeFromLeaf(b, meta) != null)
			return true;
		if (ModWoodList.getModWoodFromLeaf(b, meta) != null)
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
		if (Block.getBlockFromItem(is.getItem()) instanceof SpecialOreBlock)
			return true;
		if (ReikaOreHelper.isVanillaOre(is))
			return true;
		if (ModOreList.isModOre(is))
			return true;
		if (ReikaOreHelper.getEntryByOreDict(is) != null)
			return true;
		return false;
	}

	/** Returns true if the Block ID corresponds to an ore Blocks. Args: ID, Metadata */
	public static boolean isOre(Block id, int meta) {
		if (id == Blocks.air)
			return false;
		if (id == Blocks.lit_redstone_ore)
			return true;
		if (id instanceof SpecialOreBlock)
			return true;
		if (ReikaOreHelper.isVanillaOre(id))
			return true;
		if (ModOreList.isModOre(id, meta))
			return true;
		if (Item.getItemFromBlock(id) == null) {
			//DragonAPICore.logError("Block "+id+" has no item to compare against for Ore Check?!");
			return false;
		}
		if (id instanceof BlockTieredResource && id.getMaterial() == Material.rock)
			return true;
		return ReikaOreHelper.getEntryByOreDict(new ItemStack(id, 1, meta)) != null;
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
	public static boolean isDirtType(Block id, int meta) {
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
		return b instanceof BlockLiquid || b instanceof BlockFluidBase || b instanceof IFluidBlock;
	}

	public static boolean isPortalBlock(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		if (id == Blocks.portal)
			return true;
		if (id == Blocks.end_portal)
			return true;
		if (ModList.MYSTCRAFT.isLoaded() && id == MystCraftHandler.getInstance().portalID)
			return true;
		if (ModList.TWILIGHT.isLoaded() && id == TwilightForestHandler.BlockEntry.PORTAL.getBlock())
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

	public static ItemStack getSilkTouch(World world, int x, int y, int z, Block id, int meta, EntityPlayer ep, boolean dropFluids) {
		if (id == Blocks.air || id == Blocks.piston_extension || id == Blocks.piston_head || id == Blocks.fire)
			return null;
		if (id == Blocks.portal || id == Blocks.end_portal)
			return null;
		if ((id instanceof BlockDoor || id instanceof BlockBed) && meta >= 8)
			return null;
		ItemStack get = silkTouchDrops.get(id, meta);
		if (get != null)
			return get;
		if (Item.getItemFromBlock(id) == null) {
			DragonAPICore.logError("Something tried to silktouch null-item block "+id.getLocalizedName());
			return null;
		}
		if (ReikaBlockHelper.isLiquid(id) && !(dropFluids && ReikaWorldHelper.isLiquidSourceBlock(world, x, y, z)))
			return null;
		if (ModList.THAUMCRAFT.isLoaded() && id == BlockEntry.NODE.getBlock())
			return null;
		if (id instanceof BlockTieredResource) {
			BlockTieredResource b = (BlockTieredResource)id;
			if (ep != null && b.isPlayerSufficientTier(world, x, y, z, ep)) {
				return ReikaItemHelper.collateItemList(b.getHarvestResources(world, x, y, z, 0, ep)).get(0);
			}
			else {
				Collection<ItemStack> li = b.getNoHarvestResources(world, x, y, z, 0, ep);
				return li.isEmpty() ? null : new ArrayList<ItemStack>(li).get(0);
			}
		}
		return new ItemStack(id, 1, getSilkTouchMetaDropped(id, meta));
	}

	private static int getSilkTouchMetaDropped(Block id, int meta) {
		if (id == Blocks.torch)
			return 0;
		if (id == Blocks.redstone_torch || id == Blocks.unlit_redstone_torch)
			return 0;
		if (id == Blocks.leaves || id == Blocks.log || id == Blocks.leaves2 || id == Blocks.log2)
			return meta&3;
		if (id == Blocks.sapling)
			return meta&3;
		if (id == Blocks.vine)
			return 0;
		if (id == Blocks.waterlily)
			return 0;
		if (id == Blocks.sticky_piston || id == Blocks.piston)
			return 0;
		if (ReikaBlockHelper.isStairBlock(id))
			return 0;
		ModWoodList wood = ModWoodList.getModWood(id, meta);
		if (wood != null) {
			return wood.getLogMetadatas().get(0);
		}
		wood = ModWoodList.getModWoodFromLeaf(id, meta);
		if (wood != null) {
			return wood.getLeafMetadatas().get(0);
		}
		return meta;
	}

	static {
		addSilkTouchDrop(Blocks.lit_redstone_ore, Blocks.redstone_ore);
		addSilkTouchDrop(Blocks.redstone_wire, Items.redstone);
		addSilkTouchDrop(Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
		addSilkTouchDrop(Blocks.unpowered_repeater, Items.repeater);
		addSilkTouchDrop(Blocks.powered_repeater, Items.repeater);
		addSilkTouchDrop(Blocks.unpowered_comparator, Items.comparator);
		addSilkTouchDrop(Blocks.powered_comparator, Items.comparator);
		addSilkTouchDrop(Blocks.pumpkin_stem, Items.pumpkin_seeds);
		addSilkTouchDrop(Blocks.melon_stem, Items.melon_seeds);
		addSilkTouchDrop(Blocks.wheat, Items.wheat);
		addSilkTouchDrop(Blocks.carrots, Items.carrot);
		addSilkTouchDrop(Blocks.potatoes, Items.potato);
		addSilkTouchDrop(Blocks.nether_wart, Items.nether_wart);
		addSilkTouchDrop(Blocks.bed, Items.bed);
		addSilkTouchDrop(Blocks.brewing_stand, Items.brewing_stand);
		addSilkTouchDrop(Blocks.cauldron, Items.cauldron);
		addSilkTouchDrop(Blocks.flower_pot, Items.flower_pot);
		addSilkTouchDrop(Blocks.tripwire, Items.string);
		addSilkTouchDrop(Blocks.standing_sign, Items.sign);
		addSilkTouchDrop(Blocks.wall_sign, Items.sign);
		addSilkTouchDrop(Blocks.wooden_door, Items.wooden_door);
		addSilkTouchDrop(Blocks.iron_door, Items.iron_door);
		addSilkTouchDrop(Blocks.reeds, Items.reeds);
	}

	private static void addSilkTouchDrop(Block b, Block drop) {
		addSilkTouchDrop(b, new ItemStack(drop));
	}

	private static void addSilkTouchDrop(Block b, Item drop) {
		addSilkTouchDrop(b, new ItemStack(drop));
	}

	private static void addSilkTouchDrop(Block b, ItemStack drop) {
		silkTouchDrops.put(b, drop);
	}

	public static boolean isUnbreakable(World world, int x, int y, int z, Block id, int meta, EntityPlayer ep) {
		if (id.getBlockHardness(world, x, y, z) < 0 || (ep != null && id.getPlayerRelativeBlockHardness(ep, world, x, y, z) < 0))
			return true;
		if (id instanceof SemiUnbreakable && ((SemiUnbreakable)id).isUnbreakable(world, x, y, z, meta))
			return true;
		return false;
	}

	public static boolean isFacade(Block b) {
		String n = b.getClass().getName().toLowerCase(Locale.ENGLISH);
		if (n.contains("facade"))
			return true;
		if (n.contains("conduitbundle"))
			return true;
		if (n.contains("cover"))
			return true;
		if (n.contains("multipart"))
			return true;
		if (n.contains("cablebus"))
			return true;
		return false;
	}

	public static int getSignMetadataToConnectToWall(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = getWallSignDirection(meta);
		if (dir == ForgeDirection.UNKNOWN)
			return meta;
		int dx = x+dir.offsetX;
		int dz = z+dir.offsetZ;
		Block b = world.getBlock(dx, y, dz);
		if (b.isOpaqueCube() || b.isSideSolid(world, dx, y, dz, dir.getOpposite()))
			return meta;
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir2 = getWallSignDirection(i);
			if (dir == ForgeDirection.UNKNOWN)
				return meta;
			int ddx = x+dir2.offsetX;
			int ddz = z+dir2.offsetZ;
			Block b2 = world.getBlock(ddx, y, ddz);
			if (b2.isOpaqueCube() || b2.isSideSolid(world, ddx, y, ddz, dir2.getOpposite()))
				return i;
		}
		return meta;
	}

	public static ForgeDirection getWallSignDirection(int meta) {
		switch(meta) {
			case 2:
				return ForgeDirection.SOUTH;
			case 3:
				return ForgeDirection.NORTH;
			case 4:
				return ForgeDirection.EAST;
			case 5:
				return ForgeDirection.WEST;
		}
		return ForgeDirection.UNKNOWN;
	}

	/** Note that x,y,z are already offset by side, as per WorldRenderer */
	public static boolean renderLiquidSide(IBlockAccess iba, int x, int y, int z, int side, Block b) {
		Block at = iba.getBlock(x, y, z);
		int meta = iba.getBlockMetadata(x, y, z);
		if (at.getMaterial() == b.getMaterial())
			return false;
		if (at instanceof Submergeable)
			return false;
		if (side == 1)
			return true;
		return genericShouldSideBeRendered(iba, x, y, z, side, b);
	}

	public static boolean genericShouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side, Block b) {
		if (side == 0 && b.getBlockBoundsMinY() > 0.0D)
			return true;
		if (side == 1 && b.getBlockBoundsMaxY() < 1.0D)
			return true;
		if (side == 2 && b.getBlockBoundsMinZ() > 0.0D)
			return true;
		if (side == 3 && b.getBlockBoundsMaxZ() < 1.0D)
			return true;
		if (side == 4 && b.getBlockBoundsMinX() > 0.0D)
			return true;
		if (side == 5 && b.getBlockBoundsMaxX() < 1.0D)
			return true;
		return !iba.getBlock(x, y, z).isOpaqueCube();
	}

	public static boolean isGroundType(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.dirt || b == Blocks.grass || b == Blocks.stone || b == Blocks.sand || b == Blocks.sandstone || b == Blocks.clay || b == Blocks.gravel || b == Blocks.snow_layer)
			return true;
		Material mat = b.getMaterial();
		return mat == Material.ground || b.isReplaceableOreGen(world, x, y, z, Blocks.stone);
	}

	public static boolean isNaturalStone(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.stone || b == Blocks.sandstone || b == Blocks.bedrock)
			return true;
		if (b.isReplaceableOreGen(world, x, y, z, Blocks.stone))
			return true;
		int meta = world.getBlockMetadata(x, y, z);
		if (ReikaBlockHelper.isOre(b, meta))
			return true;
		if (ModList.CHISEL.isLoaded() && ChiselBlockHandler.isWorldgenBlock(b, meta))
			return true;
		return false;
	}
	/*
	public static void doBlockHarvest(World world, EntityPlayer ep, int x, int y, int z, int meta, Block b, ThreadLocal<EntityPlayer> harvesters) {
		ep.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(b)], 1);
		ep.addExhaustion(0.025F);

		if (b.canSilkHarvest(world, ep, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier(ep)) {
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			ItemStack itemstack = b.createStackedBlock(meta);

			if (itemstack != null) {
				items.add(itemstack);
			}

			ForgeEventFactory.fireBlockHarvesting(items, world, b, x, y, z, meta, 0, 1.0f, true, ep);
			for (ItemStack is : items) {
				b.dropBlockAsItem(world, x, y, z, is);
			}
		}
		else {
			harvesters.set(ep);
			int i1 = EnchantmentHelper.getFortuneModifier(ep);
			b.dropBlockAsItem(world, x, y, z, meta, i1);
			harvesters.set(null);
		}
	}*/

	public static void extendPiston(World world, int x, int y, int z) {
		BlockPistonBase b = (BlockPistonBase)world.getBlock(x, y, z);
		int dir = b.getPistonOrientation(world.getBlockMetadata(x, y, z));
		if (dir != 7) {
			//b.tryExtend(world, x, y, z, dir);
			ReikaObfuscationHelper.invoke("tryExtend", b, world, x, y, z, dir);
			world.setBlockMetadataWithNotify(x, y, z, dir | 8, 2);
			world.playSoundEffect(x+0.5, y+0.5, z+0.5, "tile.piston.out", 0.5F, world.rand.nextFloat()*0.25F+0.6F);
		}
	}
}
