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
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaItemHelper extends DragonAPICore {

	public static final ItemStack inksac = new ItemStack(Item.dyePowder.itemID, 1, 0);
	public static final ItemStack redDye = new ItemStack(Item.dyePowder.itemID, 1, 1);
	public static final ItemStack cactusDye = new ItemStack(Item.dyePowder.itemID, 1, 2);
	public static final ItemStack cocoaBeans = new ItemStack(Item.dyePowder.itemID, 1, 3);
	public static final ItemStack lapisDye = new ItemStack(Item.dyePowder.itemID, 1, 4);
	public static final ItemStack purpleDye = new ItemStack(Item.dyePowder.itemID, 1, 5);
	public static final ItemStack cyanDye = new ItemStack(Item.dyePowder.itemID, 1, 6);
	public static final ItemStack lgrayDye = new ItemStack(Item.dyePowder.itemID, 1, 7);
	public static final ItemStack grayDye = new ItemStack(Item.dyePowder.itemID, 1, 8);
	public static final ItemStack pinkDye = new ItemStack(Item.dyePowder.itemID, 1, 9);
	public static final ItemStack limeDye = new ItemStack(Item.dyePowder.itemID, 1, 10);
	public static final ItemStack yellowDye = new ItemStack(Item.dyePowder.itemID, 1, 11);
	public static final ItemStack lblueDye = new ItemStack(Item.dyePowder.itemID, 1, 12);
	public static final ItemStack magentaDye = new ItemStack(Item.dyePowder.itemID, 1, 13);
	public static final ItemStack orangeDye = new ItemStack(Item.dyePowder.itemID, 1, 14);
	public static final ItemStack bonemeal = new ItemStack(Item.dyePowder.itemID, 1, 15);

	public static final ItemStack blackWool = new ItemStack(Block.cloth.blockID, 1, 15);
	public static final ItemStack redWool = new ItemStack(Block.cloth.blockID, 1, 14);
	public static final ItemStack greenWool = new ItemStack(Block.cloth.blockID, 1, 13);
	public static final ItemStack brownWool = new ItemStack(Block.cloth.blockID, 1, 12);
	public static final ItemStack blueWool = new ItemStack(Block.cloth.blockID, 1, 11);
	public static final ItemStack purpleWool = new ItemStack(Block.cloth.blockID, 1, 10);
	public static final ItemStack cyanWool = new ItemStack(Block.cloth.blockID, 1, 9);
	public static final ItemStack lgrayWool = new ItemStack(Block.cloth.blockID, 1, 8);
	public static final ItemStack grayWool = new ItemStack(Block.cloth.blockID, 1, 7);
	public static final ItemStack pinkWool = new ItemStack(Block.cloth.blockID, 1, 6);
	public static final ItemStack limeWool = new ItemStack(Block.cloth.blockID, 1, 5);
	public static final ItemStack yellowWool = new ItemStack(Block.cloth.blockID, 1, 4);
	public static final ItemStack lblueWool = new ItemStack(Block.cloth.blockID, 1, 3);
	public static final ItemStack magentaWool = new ItemStack(Block.cloth.blockID, 1, 2);
	public static final ItemStack orangeWool = new ItemStack(Block.cloth.blockID, 1, 1);
	public static final ItemStack whiteWool = new ItemStack(Block.cloth.blockID, 1, 0);

	public static final ItemStack stoneBricks = new ItemStack(Block.stoneBrick.blockID, 1, 0);
	public static final ItemStack mossyBricks = new ItemStack(Block.stoneBrick.blockID, 1, 1);
	public static final ItemStack crackBricks = new ItemStack(Block.stoneBrick.blockID, 1, 2);
	public static final ItemStack circleBricks = new ItemStack(Block.stoneBrick.blockID, 1, 3);

	public static final ItemStack sandstone = new ItemStack(Block.sandStone.blockID, 1, 0);
	public static final ItemStack carvedSandstone = new ItemStack(Block.sandStone.blockID, 1, 1);
	public static final ItemStack smoothSandstone = new ItemStack(Block.sandStone.blockID, 1, 2);

	public static final ItemStack quartz = new ItemStack(Block.blockNetherQuartz.blockID, 1, 0);
	public static final ItemStack carvedQuartz = new ItemStack(Block.blockNetherQuartz.blockID, 1, 1);
	public static final ItemStack columnQuartz = new ItemStack(Block.blockNetherQuartz.blockID, 1, 2);

	public static final ItemStack oakLog = new ItemStack(Block.wood.blockID, 1, 0);
	public static final ItemStack spruceLog = new ItemStack(Block.wood.blockID, 1, 1);
	public static final ItemStack birchLog = new ItemStack(Block.wood.blockID, 1, 2);
	public static final ItemStack jungleLog = new ItemStack(Block.wood.blockID, 1, 3);
	public static final ItemStack oakLeaves = new ItemStack(Block.leaves.blockID, 1, 0);
	public static final ItemStack spruceLeaves = new ItemStack(Block.leaves.blockID, 1, 1);
	public static final ItemStack birchLeaves = new ItemStack(Block.leaves.blockID, 1, 2);
	public static final ItemStack jungleLeaves = new ItemStack(Block.leaves.blockID, 1, 3);
	public static final ItemStack oakSapling = new ItemStack(Block.sapling.blockID, 1, 0);
	public static final ItemStack spruceSapling = new ItemStack(Block.sapling.blockID, 1, 1);
	public static final ItemStack birchSapling = new ItemStack(Block.sapling.blockID, 1, 2);
	public static final ItemStack jungleSapling = new ItemStack(Block.sapling.blockID, 1, 3);
	public static final ItemStack oakWood = new ItemStack(Block.planks.blockID, 1, 0);
	public static final ItemStack spruceWood = new ItemStack(Block.planks.blockID, 1, 1);
	public static final ItemStack birchWood = new ItemStack(Block.planks.blockID, 1, 2);
	public static final ItemStack jungleWood = new ItemStack(Block.planks.blockID, 1, 3);

	public static final ItemStack stoneSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 0);
	public static final ItemStack sandstoneSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 1);
	public static final ItemStack cobbleSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 3);
	public static final ItemStack brickSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 4);
	public static final ItemStack stonebrickSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 5);
	public static final ItemStack netherSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 6);
	public static final ItemStack quartzSlab = new ItemStack(Block.stoneSingleSlab.blockID, 1, 7);
	public static final ItemStack oakSlab = new ItemStack(Block.woodSingleSlab.blockID, 1, 0);
	public static final ItemStack spruceSlab = new ItemStack(Block.woodSingleSlab.blockID, 1, 1);
	public static final ItemStack birchSlab = new ItemStack(Block.woodSingleSlab.blockID, 1, 2);
	public static final ItemStack jungleSlab = new ItemStack(Block.woodSingleSlab.blockID, 1, 3);


	/** Returns true if the block or item has metadata variants. Args: ID */
	public static boolean hasMetadata(int id) {
		if (id > 255)
			return Item.itemsList[id].getHasSubtypes();
		else {
			return Item.itemsList[id-256].getHasSubtypes();
		}
	}

	/** Like .equals for comparing ItemStacks, but does not care about size or NBT tags.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b */
	public static boolean matchStacks(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		if (a.getItem() == null || b.getItem() == null)
			return false;
		//if (!ItemStack.areItemStackTagsEqual(a, b))
		//	return false;
		if (a.getItem().getHasSubtypes() || b.getItem().getHasSubtypes())
			return (a.itemID == b.itemID && (a.getItemDamage() == b.getItemDamage() || a.getItemDamage() == OreDictionary.WILDCARD_VALUE || b.getItemDamage() == OreDictionary.WILDCARD_VALUE));
		else
			return a.itemID == b.itemID;
	}

	public static boolean isFireworkIngredient(int id) {
		if (id == Item.diamond.itemID)
			return true;
		if (id == Item.dyePowder.itemID)
			return true;
		if (id == Item.glowstone.itemID)
			return true;
		if (id == Item.feather.itemID)
			return true;
		if (id == Item.goldNugget.itemID)
			return true;
		if (id == Item.fireballCharge.itemID)
			return true;
		if (id == Item.diamond.itemID)
			return true;
		if (id == Item.skull.itemID)
			return true;
		if (id == Item.fireworkCharge.itemID)
			return true;
		if (id == Item.paper.itemID)
			return true;
		if (id == Item.gunpowder.itemID)
			return true;
		return false;
	}

	public static ItemStack getSizedItemStack(ItemStack is, int num) {
		if (is == null)
			return null;
		if (num <= 0)
			return null;
		ItemStack is2 = new ItemStack(is.itemID, num, is.getItemDamage());
		if (is.stackTagCompound != null)
			is2.stackTagCompound = (NBTTagCompound)is.stackTagCompound.copy();
		return is2;
	}

	public static void dropItem(World world, double x, double y, double z, ItemStack is) {
		if (is == null)
			return;
		EntityItem ei = new EntityItem(world, x, y, z, is.copy());
		ei.delayBeforeCanPickup = 10;
		ei.motionX = -0.1+0.2*rand.nextDouble();
		ei.motionZ = -0.1+0.2*rand.nextDouble();
		ei.motionY = 0.2*rand.nextDouble();
		if (!world.isRemote) {
			world.spawnEntityInWorld(ei);
		}
	}

	public static void dropItems(World world, double x, double y, double z, List<ItemStack> li) {
		for (int i = 0; i < li.size(); i++)
			dropItem(world, x, y, z, li.get(i));
	}

	public static boolean isBlock(ItemStack is) {
		if (is.itemID < 256)
			return true;
		try {
			//ReikaJavaLibrary.pConsole(Block.blocksList[is.itemID].getLocalizedName());
			return Block.blocksList[is.itemID] != null && !Block.blocksList[is.itemID].getLocalizedName().contains("tile.ForgeFiller.name");
		}
		catch (Exception e) {
			return false;
		}
	}

	public static boolean listContainsItemStack(List<ItemStack> li, ItemStack is) {
		for (int i = 0; i < li.size(); i++) {
			ItemStack is2 = li.get(i);
			if (matchStacks(is, is2))
				return true;
		}
		return false;
	}

	public static void dropInventory(World world, int x, int y, int z)
	{
		IInventory ii = (IInventory)world.getBlockTileEntity(x, y, z);
		Random par5Random = new Random();
		if (ii != null) {
			label0:
				for (int i = 0; i < ii.getSizeInventory(); i++){
					ItemStack itemstack = ii.getStackInSlot(i);
					if (itemstack == null)
						continue;
					float f = par5Random.nextFloat() * 0.8F + 0.1F;
					float f1 = par5Random.nextFloat() * 0.8F + 0.1F;
					float f2 = par5Random.nextFloat() * 0.8F + 0.1F;
					do {
						if (itemstack.stackSize <= 0)
							continue label0;
						int j = par5Random.nextInt(21) + 10;
						if (j > itemstack.stackSize)
							j = itemstack.stackSize;
						itemstack.stackSize -= j;
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));
						if (itemstack.hasTagCompound())
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						float f3 = 0.05F;
						entityitem.motionX = (float)par5Random.nextGaussian() * f3;
						entityitem.motionY = (float)par5Random.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)par5Random.nextGaussian() * f3;
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
					while (true);
				}
		}
	}

	public static int getWorldBlockIDFromItem(ItemStack is) {
		if (is == null)
			return 0;
		if (!(is.getItem() instanceof ItemBlock))
			return 0;
		return is.itemID;
	}

	public static int getWorldBlockMetaFromItem(ItemStack is) {
		if (is == null)
			return 0;
		if (!(is.getItem() instanceof ItemBlock))
			return 0;
		int id = is.itemID;
		if (id == Block.pistonBase.blockID || id == Block.pistonStickyBase.blockID)
			return 0;
		return is.getItem().getMetadata(is.getItemDamage());
	}

	public static boolean canCombineStacks(ItemStack is, ItemStack is2) {
		if (is == null || is2 == null)
			return false;
		if (is.itemID != is2.itemID)
			return false;
		if (is.getItemDamage() != is2.getItemDamage())
			return false;
		if (is.stackSize+is2.stackSize > is.getMaxStackSize())
			return false;
		return ItemStack.areItemStackTagsEqual(is, is2);
	}

	public static boolean oreItemExists(String tag) {
		ArrayList<ItemStack> li = OreDictionary.getOres(tag);
		return li != null && !li.isEmpty();
	}

	public static boolean oreItemsExist(String... tags) {
		for (int i = 0; i < tags.length; i++) {
			if (!oreItemExists(tags[i]))
				return false;
		}
		return true;
	}
}
