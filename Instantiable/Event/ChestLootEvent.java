package Reika.DragonAPI.Instantiable.Event;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.Location;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.DesertPyramid;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;
import net.minecraft.world.gen.structure.StructureMineshaftPieces.Corridor;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces.Piece;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.ChestCorridor;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Crossing;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Library;
import net.minecraft.world.gen.structure.StructureVillagePieces.House2;
import net.minecraftforge.common.MinecraftForge;

public class ChestLootEvent extends Event {

	public final IInventory inventory;
	public final Location chestID;

	public ChestLootEvent(IInventory ii, Location loc) {
		chestID = loc;
		inventory = ii;
	}

	public static void fire(Object struct, IInventory ii) { //called after the call to WeightedRandomChestContent.generateChestContents inside generateStructureChestContents
		Location table = calculateTable(struct);
		if (table != null) {
			MinecraftForge.EVENT_BUS.post(new ChestLootEvent(ii, table));
		}
		else {
			DragonAPICore.logError("Tried to fire an event for chest loot from an unrecognized structure "+struct+"!");
		}
	}

	private static Location calculateTable(Object struct) {
		if (struct instanceof WorldGenDungeons)
			return Location.DUNGEON;
		if (struct instanceof Piece)
			return Location.NETHER_FORTRESS;
		else if (struct instanceof Corridor)
			return Location.MINESHAFT;
		else if (struct instanceof Crossing)
			return Location.STRONGHOLD_CROSSING;
		else if (struct instanceof Library)
			return Location.STRONGHOLD_LIBRARY;
		else if (struct instanceof ChestCorridor)
			return Location.STRONGHOLD_HALLWAY;
		else if (struct instanceof JunglePyramid)
			return Location.JUNGLE_PUZZLE;
		else if (struct instanceof DesertPyramid)
			return Location.PYRAMID;
		else if (struct instanceof House2)
			return Location.VILLAGE;
		return null;
	}

}
