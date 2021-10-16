/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.common.ChestGenHooks;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.BasicModEntry;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaGenHelper;

public class LootController {

	private static final HashMap<String, ChestGenLootLocation> locationsByID = new HashMap();
	private static final HashMap<String, ChestGenLootLocation> locationsByObject = new HashMap();

	private static Field chestTable;
	private static Field chestContents;

	public static ChestGenLootLocation netherFortress = new ChestGenLootLocation() {

		@Override
		public String getTag() {
			return "netherFortress";
		}

		@Override
		public WeightedRandomChestContent[] getContents() throws Exception {
			return StructureNetherBridgePieces.Piece.field_111019_a;
		}

		@Override
		public void setContents(WeightedRandomChestContent[] items) throws Exception {
			StructureNetherBridgePieces.Piece.field_111019_a = items;
		}

	};

	static {
		try {
			chestTable = ChestGenHooks.class.getDeclaredField("chestInfo");
			chestTable.setAccessible(true);

			chestContents = ChestGenHooks.class.getDeclaredField("contents");
			chestContents.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		locationsByObject.put(WorldGenDungeons.class.getName(), Location.DUNGEON);
		locationsByObject.put(StructureNetherBridgePieces.Piece.class.getName(), netherFortress);
		locationsByObject.put(StructureMineshaftPieces.Corridor.class.getName(), Location.MINESHAFT);
		locationsByObject.put(StructureStrongholdPieces.Crossing.class.getName(), Location.STRONGHOLD_CROSSING);
		locationsByObject.put(StructureStrongholdPieces.RoomCrossing.class.getName(), Location.STRONGHOLD_CROSSING);
		locationsByObject.put(StructureStrongholdPieces.Library.class.getName(), Location.STRONGHOLD_LIBRARY);
		locationsByObject.put(StructureStrongholdPieces.ChestCorridor.class.getName(), Location.STRONGHOLD_HALLWAY);
		locationsByObject.put(ComponentScatteredFeaturePieces.JunglePyramid.class.getName(), Location.JUNGLE_PUZZLE);
		locationsByObject.put(ComponentScatteredFeaturePieces.DesertPyramid.class.getName(), Location.PYRAMID);
		locationsByObject.put(StructureVillagePieces.House2.class.getName(), Location.VILLAGE);

		locationsByID.put("netherFortress", netherFortress);
	}

	private List<LootStack> items = new ArrayList();

	public void registerToWorldGen(DragonAPIMod mod, int tier) {
		if (tier <= 0) {
			mod.getModLogger().log("Skipping dungeon loot generation, as it has been disabled.");
			return;
		}
		for (LootStack ls : items) {
			String itemName = ls.toString();
			if (ls.lootTier <= tier) {
				ReikaGenHelper.addChestLoot(ls.chestLocation.tag, ls.item, ls.minSize, ls.maxSize, ls.weight);
				mod.getModLogger().log("Adding "+itemName+" to "+ls.chestLocation);
			}
			else {
				mod.getModLogger().log("Not adding "+itemName+" to "+ls.chestLocation+", as its generation tier ("+ls.lootTier+") is greater than the configured tier of "+tier+".");
			}
		}
	}

	public void addItem(int tier, Location loc, ItemStack is, int min, int max, int chance) {
		LootStack ls = new LootStack(tier, loc, is, min, max, chance);
		if (!items.contains(ls))
			items.add(ls);
	}

	public void addItem(int tier, Location loc, ItemStack is, int chance) {
		this.addItem(tier, loc, is, 1, 1, chance);
	}

	public void addItems(int tier, Location loc, List<ItemStack> li, int chance) {
		for (int i = 0; i < li.size(); i++) {
			this.addItem(tier, loc, li.get(i), chance);
		}
	}

	@Override
	public String toString() {
		return items.toString();
	}

	class LootStack {
		public final int lootTier;
		public final Location chestLocation;
		private final ItemStack item;
		public final int minSize;
		public final int maxSize;
		public final int weight;

		public LootStack(int tier, Location loc, ItemStack is, int min, int max, int chance) {
			lootTier = tier;
			chestLocation = loc;
			item = is;
			minSize = min;
			maxSize = max;
			weight = chance;
		}

		public ItemStack getItemStack() {
			return item.copy();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof LootStack) {
				LootStack ls = (LootStack)o;
				return ls.chestLocation == chestLocation && ReikaItemHelper.matchStacks(item, ls.item);
			}
			return false;
		}

		@Override
		public String toString() {
			if (maxSize == minSize)
				return item.getDisplayName()+" with size "+minSize;
			else
				return item.getDisplayName()+" with size "+minSize+"-"+maxSize;
		}
	}

	public static ChestGenLootLocation getLocationForStructure(Object struct) {
		if (struct instanceof StructureNetherBridgePieces || struct instanceof StructureNetherBridgePieces.Piece)
			return getLocationForID("netherFortress");
		return struct == null ? null : locationsByObject.get(struct.getClass().getName());
	}

	public static ChestGenLootLocation getLocationForID(String id) {
		return locationsByID.get(id);
	}

	public static ArrayList<WeightedRandomChestContent> getCGHItems(String tag) throws Exception {
		return getCGHItems(ChestGenHooks.getInfo(tag));
	}

	public static ArrayList<WeightedRandomChestContent> getCGHItems(ChestGenHooks entry) throws Exception {
		return (ArrayList<WeightedRandomChestContent>)chestContents.get(entry);
	}

	public static void setCGHItems(String tag, WeightedRandomChestContent[] items) throws Exception {
		setCGHItems(ChestGenHooks.getInfo(tag), items);
	}

	public static void setCGHItems(ChestGenHooks entry, WeightedRandomChestContent[] items) throws Exception {
		chestContents.set(entry, ReikaJavaLibrary.makeListFromArray(items));
	}

	public static ChestGenHooks getChestEntry(String tag) throws Exception {
		return ((Map<String, ChestGenHooks>)chestTable.get(null)).get(tag);
	}

	public static Set<String> getAllIDs() {
		return Collections.unmodifiableSet(locationsByID.keySet());
	}

	public static interface ChestGenLootLocation {

		public String getTag();

		public WeightedRandomChestContent[] getContents() throws Exception;

		public void setContents(WeightedRandomChestContent[] items) throws Exception;

	}

	public enum Location implements ChestGenLootLocation {
		BONUS(ChestGenHooks.BONUS_CHEST),
		VILLAGE(ChestGenHooks.VILLAGE_BLACKSMITH),
		DUNGEON(ChestGenHooks.DUNGEON_CHEST),
		MINESHAFT(ChestGenHooks.MINESHAFT_CORRIDOR),
		STRONGHOLD_LIBRARY(ChestGenHooks.STRONGHOLD_LIBRARY),
		STRONGHOLD_CROSSING(ChestGenHooks.STRONGHOLD_CROSSING),
		STRONGHOLD_HALLWAY(ChestGenHooks.STRONGHOLD_CORRIDOR),
		PYRAMID(ChestGenHooks.PYRAMID_DESERT_CHEST),
		JUNGLE_DISPENSER(ChestGenHooks.PYRAMID_JUNGLE_DISPENSER),
		JUNGLE_PUZZLE(ChestGenHooks.PYRAMID_JUNGLE_CHEST);

		public final String tag;

		private Location(String sg) {
			tag = sg;
			locationsByID.put(tag, this);
		}

		@Override
		public String getTag() {
			return tag;
		}

		@Override
		public WeightedRandomChestContent[] getContents() throws Exception {
			return ChestGenHooks.getItems(tag, DragonAPICore.rand);
		}

		@Override
		public void setContents(WeightedRandomChestContent[] items) throws Exception {
			setCGHItems(tag, items);
		}
	}

	public static enum ModdedStructures implements ChestGenLootLocation {
		THAUMTOWER(ModList.THAUMCRAFT, "thaumcraft.common.lib.world.ComponentWizardTower", "towerChestContents", ReferenceType.FIELD),
		APIARIST(ModList.FORESTRY, "forestry.apiculture.worldgen.ComponentVillageBeeHouse", "naturalistChest", ReferenceType.TABLE),
		TINKERVILLAGE(ModList.TINKERER, "tconstruct.world.village.ComponentToolWorkshop", "TinkerHouse", ReferenceType.TABLE),
		APOTHECARY(ModList.WITCHERY, "com.emoniph.witchery.worldgen.ComponentVillageApothecary", "villageApothecaryChestContents", ReferenceType.FIELD),
		BOOKSHOP(ModList.WITCHERY, "com.emoniph.witchery.worldgen.ComponentVillageBookShop", "bookshopChestContents", ReferenceType.FIELD),
		WKEEP(ModList.WITCHERY, "com.emoniph.witchery.worldgen.ComponentVillageKeep", "villageTowerChestContents", ReferenceType.FIELD),
		PHOTOSHOP(new BasicModEntry("WitchingGadgets"), "witchinggadgets.common.world.VillageComponentPhotoshop", "WG:PHOTOWORKSHOP", ReferenceType.TABLE),
		;

		private final ModEntry sourceMod;
		private final ReferenceType dataType;
		private final String className;
		private final String tableID;
		private final Class reference;

		//public static final ModdedStructures[] list = values();

		private ModdedStructures(ModEntry mod, String partName, String lootTable, ReferenceType type) {
			sourceMod = mod;
			dataType = type;
			className = partName;
			tableID = lootTable;
			if (mod.isLoaded()) {
				locationsByID.put(this.getTag(), this);
				locationsByObject.put(partName, this);
				Class c = null;
				try {
					c = Class.forName(className);
				}
				catch (Exception e) {
					e.printStackTrace();
					ReflectiveFailureTracker.instance.logModReflectiveFailure(mod, e);
				}
				reference = c;
			}
			else {
				reference = null;
			}
		}

		public boolean exists() {
			return sourceMod.isLoaded();
		}

		public boolean isInCGHTable() {
			return dataType == ReferenceType.TABLE;
		}

		public String getTag() {
			switch(dataType) {
				case TABLE:
					return tableID;
				default:
					return ReikaStringParser.capFirstChar(this.name());
			}
		}

		public WeightedRandomChestContent[] getContents() throws Exception {
			if (!this.exists())
				throw new IllegalStateException("Mod '"+sourceMod.getDisplayName()+"' is not loaded!");
			return dataType.getData(reference, tableID);
		}

		@Override
		public void setContents(WeightedRandomChestContent[] items) throws Exception {
			if (!this.exists())
				throw new IllegalStateException("Mod '"+sourceMod.getDisplayName()+"' is not loaded!");
			dataType.setData(reference, tableID, items);
		}
	}

	private static enum ReferenceType {

		FIELD,
		TABLE;

		private WeightedRandomChestContent[] getData(Class c, String name) throws Exception {
			switch(this) {
				case FIELD:
					Field f = c.getDeclaredField(name);
					f.setAccessible(true);
					return (WeightedRandomChestContent[])f.get(null);
				case TABLE:
					return ChestGenHooks.getItems(name, DragonAPICore.rand);
			}
			return null;
		}

		private void setData(Class c, String name, WeightedRandomChestContent[] items) throws Exception {
			switch(this) {
				case FIELD:
					Field f = c.getDeclaredField(name);
					f.setAccessible(true);
					f.set(null, items);
					break;
				case TABLE:
					setCGHItems(name, items);
					break;
			}
		}

	}

}
