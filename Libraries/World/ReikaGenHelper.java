/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaGenHelper extends DragonAPICore {

	/** Adds an ItemStack to chest generation. Args: Chest Type, ItemStack, min size, max size, frequency */
	public static void addStackToChestGen(String chest, ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(chest).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to village chests. Args: ItemStack, min size, max size, frequency */
	public static void addStackToVillage(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to dungeons (and many mod dungeons). Args: ItemStack, min size, max size, frequency */
	public static void addStackToDungeon(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to abandoned mineshafts. Args: ItemStack, min size, max size, frequency */
	public static void addStackToMineshaft(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to stronghold hallways. Args: ItemStack, min size, max size, frequency */
	public static void addStackToStronghold(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to stronghold "crossings". Args: ItemStack, min size, max size, frequency */
	public static void addStackToStronghold2(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to stronghold libraries. Args: ItemStack, min size, max size, frequency */
	public static void addStackToLibrary(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds an ItemStack to desert temples. Args: ItemStack, min size, max size, frequency */
	public static void addStackToPyramid(ItemStack is, int min, int max, int rate) {
		ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(is, min, max, rate));
	}

	/** Adds ItemStacks to chest generation. Args: Chest Type, ItemStacks, min size, max size, frequency */
	public static void addStacksToChestGen(String chest, ArrayList<ItemStack> is, int min, int max, int rate) {
		for (int i = 0; i < is.size(); i++)
			ChestGenHooks.getInfo(chest).addItem(new WeightedRandomChestContent(is.get(i), min, max, rate));
	}

}
