/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.ModRegistry.ModOreList;

public final class ThaumOreHandler {

	public final int oreID;
	public final int oreItemID;
	public final int shardID;

	private final ItemStack oreCinnabar;
	private final ItemStack oreAir;
	private final ItemStack oreFire;
	private final ItemStack oreWater;
	private final ItemStack oreEarth;
	private final ItemStack oreVis;
	private final ItemStack oreDull;
	private final ItemStack oreAmber;

	/** Quicksilver */
	private final ItemStack dropCinnabar;
	private final ItemStack dropAmber;

	private final ItemStack shardAir;
	private final ItemStack shardFire;
	private final ItemStack shardWater;
	private final ItemStack shardEarth;
	private final ItemStack shardVis;
	private final ItemStack shardDull;

	private final ArrayList ores = new ArrayList<ItemStack>();
	private final ArrayList items = new ArrayList<ItemStack>();

	public ThaumOreHandler(int ore, int item, int shard) {
		oreID = ore;
		shardID = shard;
		oreItemID = item;

		oreCinnabar = new ItemStack(oreID, 1, 0);
		oreAir = new ItemStack(oreID, 1, 1);
		oreFire = new ItemStack(oreID, 1, 2);
		oreWater = new ItemStack(oreID, 1, 3);
		oreEarth = new ItemStack(oreID, 1, 4);
		oreVis = new ItemStack(oreID, 1, 5);
		oreDull = new ItemStack(oreID, 1, 6);
		oreAmber = new ItemStack(oreID, 1, 7);

		ores.add(oreCinnabar);
		ores.add(oreAir);
		ores.add(oreFire);
		ores.add(oreWater);
		ores.add(oreEarth);
		ores.add(oreVis);
		ores.add(oreDull);
		ores.add(oreAmber);

		dropCinnabar = new ItemStack(oreItemID, 1, 3);
		dropAmber = new ItemStack(oreItemID, 1, 6);

		shardAir = new ItemStack(shardID, 1, 0);
		shardFire = new ItemStack(shardID, 1, 1);
		shardWater = new ItemStack(shardID, 1, 2);
		shardEarth = new ItemStack(shardID, 1, 3);
		shardVis = new ItemStack(shardID, 1, 4);
		shardDull = new ItemStack(shardID, 1, 5);

		items.add(dropCinnabar);
		items.add(shardAir);
		items.add(shardFire);
		items.add(shardWater);
		items.add(shardEarth);
		items.add(shardVis);
		items.add(shardDull);
		items.add(dropAmber);
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getOre(ModOreList ore) {
		if (!ore.isThaumcraft())
			return null;
		switch(ore) {
		case AMBER:
			return oreAmber.copy();
		case CINNABAR:
			return oreCinnabar.copy();
		case INFUSEDAIR:
			return oreAir.copy();
		case INFUSEDDULL:
			return oreDull.copy();
		case INFUSEDEARTH:
			return oreEarth.copy();
		case INFUSEDFIRE:
			return oreFire.copy();
		case INFUSEDVIS:
			return oreVis.copy();
		case INFUSEDWATER:
			return oreWater.copy();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getItem(ModOreList ore) {
		if (!ore.isThaumcraft())
			return null;
		switch(ore) {
		case AMBER:
			return dropAmber.copy();
		case CINNABAR:
			return dropCinnabar.copy();
		case INFUSEDAIR:
			return shardAir.copy();
		case INFUSEDDULL:
			return shardDull.copy();
		case INFUSEDEARTH:
			return shardEarth.copy();
		case INFUSEDFIRE:
			return shardFire.copy();
		case INFUSEDVIS:
			return shardVis.copy();
		case INFUSEDWATER:
			return shardWater.copy();
		}
		return null;
	}

	public boolean isThaumOre(ItemStack is) {
		if (is == null)
			return false;
		//return ReikaItemHelper.listContainsItemStack(ores, is);
		return is.itemID == oreID;
	}

	public boolean isShard(ItemStack is) {
		//return ReikaItemHelper.listContainsItemStack(items, is) && is.itemID == shardID;
		return is.itemID == shardID;
	}

	public boolean isShardOre(ItemStack block) {
		if (!this.isThaumOre(block))
			return false;
		if (block.getItemDamage() == oreAmber.getItemDamage())
			return false;
		if (block.getItemDamage() == oreCinnabar.getItemDamage())
			return false;
		return true;
	}

}
