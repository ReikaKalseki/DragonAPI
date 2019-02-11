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
import net.minecraft.world.gen.structure.StructureStrongholdPieces.RoomCrossing;
import net.minecraft.world.gen.structure.StructureVillagePieces.House2;
import net.minecraftforge.common.MinecraftForge;

public class ChestLootEvent extends Event {

	public final IInventory inventory;
	public final String chestID;

	public ChestLootEvent(IInventory ii, String loc) {
		chestID = loc;
		inventory = ii;
	}

	public static void fire(Object struct, IInventory ii) { //called after the call to WeightedRandomChestContent.generateChestContents inside generateStructureChestContents
		String table = calculateTable(struct);
		if (table != null) {
			MinecraftForge.EVENT_BUS.post(new ChestLootEvent(ii, table));
		}
		else {
			DragonAPICore.logError("Tried to fire an event for chest loot from an unrecognized structure "+struct+"!");
		}
	}

	private static String calculateTable(Object struct) {
		if (struct instanceof WorldGenDungeons)
			return Location.DUNGEON.tag;
		if (struct instanceof Piece)
			return "netherFortress";
		else if (struct instanceof Corridor)
			return Location.MINESHAFT.tag;
		else if (struct instanceof Crossing || struct instanceof RoomCrossing)
			return Location.STRONGHOLD_CROSSING.tag;
		else if (struct instanceof Library)
			return Location.STRONGHOLD_LIBRARY.tag;
		else if (struct instanceof ChestCorridor)
			return Location.STRONGHOLD_HALLWAY.tag;
		else if (struct instanceof JunglePyramid)
			return Location.JUNGLE_PUZZLE.tag;
		else if (struct instanceof DesertPyramid)
			return Location.PYRAMID.tag;
		else if (struct instanceof House2)
			return Location.VILLAGE.tag;
		else if (struct.getClass().getSimpleName().contains("WizardTower")) //ThaumCraft
			return "ThaumVillageTower";
		return null;
	}

}
