/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ThaumOreHandler extends ModHandlerBase {

	public final Block oreID;
	public final Item oreItemID;
	public final Item shardID;

	public final int metaCinnabar = 0;
	public final int metaAir = 1;
	public final int metaFire = 2;
	public final int metaWater = 3;
	public final int metaEarth = 4;
	public final int metaVis = 5;
	public final int metaDull = 6;
	public final int metaAmber = 7;

	public final int metaCinnabarItem = 3;
	public final int metaAmberItem = 6;

	public final int metaAirShard = 0;
	public final int metaFireShard = 1;
	public final int metaWaterShard = 2;
	public final int metaEarthShard = 3;
	public final int metaVisShard = 4;
	public final int metaDullShard = 5;

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

	private boolean isOreDict = false;

	private static final ThaumOreHandler instance = new ThaumOreHandler();

	private ThaumOreHandler() {
		super();
		Block idore = null;
		Item iditem = null;
		Item idshard = null;

		if (this.hasMod()) {
			Class thaum = ModList.THAUMCRAFT.getBlockClass();
			Class items = ModList.THAUMCRAFT.getItemClass();

			idore = this.loadBlockID(thaum, "blockCustomOre");
			iditem = this.loadItemID(items, "itemResource");
			idshard = this.loadItemID(items, "itemShard");
		}
		else {
			this.noMod();
		}

		oreID = idore;
		shardID = idshard;
		oreItemID = iditem;

		oreCinnabar = new ItemStack(oreID, 1, metaCinnabar);
		oreAir = new ItemStack(oreID, 1, metaAir);
		oreFire = new ItemStack(oreID, 1, metaFire);
		oreWater = new ItemStack(oreID, 1, metaWater);
		oreEarth = new ItemStack(oreID, 1, metaEarth);
		oreVis = new ItemStack(oreID, 1, metaVis);
		oreDull = new ItemStack(oreID, 1, metaDull);
		oreAmber = new ItemStack(oreID, 1, metaAmber);

		ores.add(oreCinnabar);
		ores.add(oreAir);
		ores.add(oreFire);
		ores.add(oreWater);
		ores.add(oreEarth);
		ores.add(oreVis);
		ores.add(oreDull);
		ores.add(oreAmber);

		dropCinnabar = new ItemStack(oreItemID, 1, metaCinnabarItem);
		dropAmber = new ItemStack(oreItemID, 1, metaAmberItem);

		shardAir = new ItemStack(shardID, 1, metaAirShard);
		shardFire = new ItemStack(shardID, 1, metaFireShard);
		shardWater = new ItemStack(shardID, 1, metaWaterShard);
		shardEarth = new ItemStack(shardID, 1, metaEarthShard);
		shardVis = new ItemStack(shardID, 1, metaVisShard);
		shardDull = new ItemStack(shardID, 1, metaDullShard);

		items.add(dropCinnabar);
		items.add(shardAir);
		items.add(shardFire);
		items.add(shardWater);
		items.add(shardEarth);
		items.add(shardVis);
		items.add(shardDull);
		items.add(dropAmber);
	}

	public static ThaumOreHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return oreID != null && shardID != null && oreItemID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.THAUMCRAFT;
	}

	public ItemStack getOre(int meta) {
		if (!this.initializedProperly())
			return null;
		switch(meta) {
		case metaAmber:
			return oreAmber.copy();
		case metaCinnabar:
			return oreCinnabar.copy();
		case metaAir:
			return oreAir.copy();
		case metaDull:
			return oreDull.copy();
		case metaEarth:
			return oreEarth.copy();
		case metaFire:
			return oreFire.copy();
		case metaVis:
			return oreVis.copy();
		case metaWater:
			return oreWater.copy();
		}
		return null;
	}

	public ItemStack getItem(int meta) {
		if (!this.initializedProperly())
			return null;
		switch(meta) {
		case metaAmberItem:
			return dropAmber.copy();
		case metaCinnabarItem:
			return dropCinnabar.copy();
		}
		return null;
	}

	public ItemStack getShard(int meta) {
		if (!this.initializedProperly())
			return null;
		switch(meta) {
		case metaAirShard:
			return shardAir.copy();
		case metaDullShard:
			return shardDull.copy();
		case metaEarthShard:
			return shardEarth.copy();
		case metaFireShard:
			return shardFire.copy();
		case metaVisShard:
			return shardVis.copy();
		case metaWaterShard:
			return shardWater.copy();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getOre(ModOreList ore) {
		if (!this.initializedProperly())
			return null;
		if (!ore.isThaumcraft())
			return null;
		switch(ore) {
		case AMBER:
			return oreAmber.copy();
		case CINNABAR:
			return oreCinnabar.copy();
		case INFUSEDAIR:
			return oreAir.copy();
		case INFUSEDENTROPY:
			return oreDull.copy();
		case INFUSEDEARTH:
			return oreEarth.copy();
		case INFUSEDFIRE:
			return oreFire.copy();
		case INFUSEDORDER:
			return oreVis.copy();
		case INFUSEDWATER:
			return oreWater.copy();
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public ItemStack getItem(ModOreList ore) {
		if (!this.initializedProperly())
			return null;
		if (!ore.isThaumcraft())
			return null;
		switch(ore) {
		case AMBER:
			return dropAmber.copy();
		case CINNABAR:
			return dropCinnabar.copy();
		case INFUSEDAIR:
			return shardAir.copy();
		case INFUSEDENTROPY:
			return shardDull.copy();
		case INFUSEDEARTH:
			return shardEarth.copy();
		case INFUSEDFIRE:
			return shardFire.copy();
		case INFUSEDORDER:
			return shardVis.copy();
		case INFUSEDWATER:
			return shardWater.copy();
		}
		return null;
	}

	public boolean isThaumOre(ItemStack is) {
		if (!this.initializedProperly())
			return false;
		if (is == null)
			return false;
		//return ReikaItemHelper.listContainsItemStack(ores, is);
		return ReikaItemHelper.matchStackWithBlock(is, oreID);
	}

	public boolean isShard(ItemStack is) {
		if (!this.initializedProperly())
			return false;
		//return ReikaItemHelper.listContainsItemStack(items, is) && is.getItem() == shardID;
		return is.getItem() == shardID;
	}

	public boolean isShardOre(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		if (!this.isThaumOre(block))
			return false;
		if (block.getItemDamage() == oreAmber.getItemDamage())
			return false;
		if (block.getItemDamage() == oreCinnabar.getItemDamage())
			return false;
		return true;
	}

	public void forceOreRegistration() {
		if (!isOreDict) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Thaumcraft ores are being registered to Ore Dictionary!");
			for (int i = 0; i < ModOreList.oreList.length; i++) {
				ModOreList o = ModOreList.oreList[i];
				if (o.isThaumcraft()) {
					OreDictionary.registerOre(o.getOreDictNames()[0], this.getOre(o));
					OreDictionary.registerOre(o.getProductLabel(), this.getItem(o));
					o.reloadOreList();
					ReikaJavaLibrary.pConsole("DRAGONAPI: Registering "+o.displayName);
				}
			}
		}
		else {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Thaumcraft ores already registered to ore dictionary! No action taken!");
			Thread.dumpStack();
		}
	}

	/** Tries both instance and ID storage */
	private Block loadBlockID(Class c, String fieldName) {
		Block id = null;
		Exception e1 = null;
		Exception e2 = null;
		try {
			Field block = c.getField(fieldName);
			id = ((Block)block.get(null));
		}
		catch (Exception e) {
			e1 = e;
		}
		return id;
	}

	/** Tries both instance and ID storage */
	private Item loadItemID(Class c, String fieldName) {
		Item id = null;
		Exception e1 = null;
		Exception e2 = null;
		try {
			Field item = c.getField(fieldName);
			id = ((Item)item.get(null));
		}
		catch (Exception e) {
			e1 = e;
		}
		return id;
	}

}
