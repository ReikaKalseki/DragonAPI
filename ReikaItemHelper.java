package Reika.DragonAPI;

import net.minecraft.block.Block;
import net.minecraft.item.*;

public final class ReikaItemHelper {

	private ReikaItemHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

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

	/** Like .equals for comparing ItemStacks, but does not care about size.
	 * Returns true if the ids and metadata match (or both are null).
	 * Args: ItemStacks a, b */
	public static boolean matchStacks(ItemStack a, ItemStack b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return (a.itemID == b.itemID && a.getItemDamage() == b.getItemDamage());
	}

	public static boolean isFireworkIngredient(int id) {
		if (id == Item.diamond.itemID)
			return true;
		if (id == Item.dyePowder.itemID)
			return true;
		if (id == Item.lightStoneDust.itemID)
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
}
