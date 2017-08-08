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

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.Location;

public final class ReikaGenHelper extends DragonAPICore {

	/** Adds an ItemStack to chest generation. Args: Chest Type, ItemStack, min size, max size, frequency */
	public static void addStackToChestGen(String chest, ItemStack is, int min, int max, int rate) {
		addChestLoot(chest, is, min, max, rate);
	}

	/** Adds an ItemStack to village chests. Args: ItemStack, min size, max size, frequency */
	public static void addStackToVillage(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.VILLAGE_BLACKSMITH, is, min, max, rate);
	}

	/** Adds an ItemStack to dungeons (and many mod dungeons). Args: ItemStack, min size, max size, frequency */
	public static void addStackToDungeon(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.DUNGEON_CHEST, is, min, max, rate);
	}

	/** Adds an ItemStack to abandoned mineshafts. Args: ItemStack, min size, max size, frequency */
	public static void addStackToMineshaft(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.MINESHAFT_CORRIDOR, is, min, max, rate);
	}

	/** Adds an ItemStack to stronghold hallways. Args: ItemStack, min size, max size, frequency */
	public static void addStackToStronghold(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.STRONGHOLD_CORRIDOR, is, min, max, rate);
	}

	/** Adds an ItemStack to stronghold "crossings". Args: ItemStack, min size, max size, frequency */
	public static void addStackToStronghold2(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.STRONGHOLD_CROSSING, is, min, max, rate);
	}

	/** Adds an ItemStack to stronghold libraries. Args: ItemStack, min size, max size, frequency */
	public static void addStackToLibrary(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.STRONGHOLD_LIBRARY, is, min, max, rate);
	}

	/** Adds an ItemStack to desert temples. Args: ItemStack, min size, max size, frequency */
	public static void addStackToPyramid(ItemStack is, int min, int max, int rate) {
		addChestLoot(ChestGenHooks.PYRAMID_DESERT_CHEST, is, min, max, rate);
	}

	/** Adds ItemStacks to chest generation. Args: Chest Type, ItemStacks, min size, max size, frequency */
	public static void addStacksToChestGen(String chest, Collection<ItemStack> li, int min, int max, int rate) {
		for (ItemStack is : li)
			addChestLoot(chest, is, min, max, rate);//ChestGenHooks.getInfo(chest).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	public static void addChestLoot(Location loc, ItemStack is, int minSize, int maxSize, int weight) {
		addChestLoot(loc.tag, is, minSize, maxSize, weight);
	}

	public static void addChestLoot(String location, ItemStack is, int minSize, int maxSize, int weight) {
		if (is == null || is.getItem() == null)
			throw new MisuseException("You cannot add null items to the loot tables!");
		WeightedRandomChestContent stack = new WeightedRandomChestContent(is, minSize, maxSize, weight);
		ChestGenHooks.getInfo(location).addItem(stack);
	}

}
