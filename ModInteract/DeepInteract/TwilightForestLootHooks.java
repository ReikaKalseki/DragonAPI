/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;

import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class TwilightForestLootHooks {

	private static final int DEFAULT_RARITY = 10;
	private static Constructor entryConstructor;
	private static Field tableList;

	public static enum DungeonTypes {
		SMALL_HOLLOW("hill1", 1),
		MEDIUM_HOLLOW("hill2", 2),
		LARGE_HOLLOW("hill3", 3),
		HEDGE_MAZE("hedgemaze", 4),
		LABYRINTH("labyrinth_room", 5),
		LABYRINTH_END("labyrinth_deadend", 6),
		LICH_ROOM("tower_room", 7),
		LICH_LIBRARY("tower_library", 8),
		RUINS_BASEMENT("basement", 9),
		LABYRINTH_VAULT("labyrinth_vault", 10),
		DARKTOWER_CACHE("darktower_cache", 11),
		DARKTOWER_KEY("darktower_key", 12),
		URGHAST("darktower_boss", 13),
		TREE_DUNGEON("tree_cache", 14),
		STRONGHOLD_CACHE("stronghold_cache", 15),
		STRONGHOLD_ROOM("stronghold_room", 16),
		GOBLIN_KNIGHT("stronghold_boss", 17),
		AURORA_CACHE("aurora_cache", 18),
		AURORA_ROOM("aurora_room", 19),
		AURORA_BOSS("aurora_boss", 20),
		TROLL_GARDEN("troll_garden", 21),
		TROLL_VAULT("troll_vault", 22);

		private final String field;
		private int index;
		private Object instance;
		private final EnumMap<LootLevels, Object> treasureTables = new EnumMap(LootLevels.class);

		private static final DungeonTypes[] list = values();

		private DungeonTypes(String s, int i) {
			field = s;
			index = i;
		}

		public void addItem(ItemStack item, LootLevels level) {
			this.addItem(item, level, DEFAULT_RARITY);
		}

		public void addItem(ItemStack item, LootLevels level, int rarity) {
			try {
				Object table = treasureTables.get(level);
				Object entry = generateTreasureEntry(item, rarity);
				insertTableEntry(table, entry);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Added "+item+" to TF loot table "+this+" with rarity "+rarity);
			}
			catch (Exception e) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Could not add loot to TF dungeon loot table "+this+"!");
				e.printStackTrace();
			}
		}
	}

	private static Object generateTreasureEntry(ItemStack item, int rarity) throws Exception {
		return entryConstructor.newInstance(item, rarity);
	}

	private static void insertTableEntry(Object table, Object entry) throws Exception {
		ArrayList li = (ArrayList)tableList.get(table);
		li.add(entry);
	}

	public static enum LootLevels {
		USELESS("useless"),
		COMMON("common"),
		UNCOMMON("uncommon"),
		RARE("rare"),
		ULTRARARE("ultrarare");

		private final String field;
		private Field fieldInstance;

		private static final LootLevels[] list = values();

		private LootLevels(String s) {
			field = s;
		}
	}

	static {
		try {
			Class c = Class.forName("twilightforest.TFTreasure");
			Class tableClass = Class.forName("twilightforest.TFTreasureTable");
			Class entryClass = Class.forName("twilightforest.TFTreasureItem");

			tableList = tableClass.getDeclaredField("list");
			tableList.setAccessible(true);

			entryConstructor = entryClass.getConstructor(ItemStack.class, int.class);

			for (int k = 0; k < LootLevels.list.length; k++) {
				LootLevels l = LootLevels.list[k];
				l.fieldInstance = c.getDeclaredField(l.field);
				l.fieldInstance.setAccessible(true);
			}

			for (int i = 0; i < DungeonTypes.list.length; i++) {
				DungeonTypes type = DungeonTypes.list[i];
				try {
					Field f = c.getField(type.field);
					type.instance = f.get(null);

					for (int k = 0; k < LootLevels.list.length; k++) {
						LootLevels l = LootLevels.list[k];
						Object table = l.fieldInstance.get(type.instance);
						type.treasureTables.put(l, table);
					}
				}
				catch (Exception e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load TF dungeon loot table "+type+"!");
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: Could not load TF dungeon loot tables!");
			e.printStackTrace();
		}
	}

}
