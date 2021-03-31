package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Worldgen.LootController;
import Reika.DragonAPI.Instantiable.Worldgen.LootController.ChestGenLootLocation;

import cpw.mods.fml.common.eventhandler.Event;

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
			//ReikaJavaLibrary.pConsole("Generated a "+table+" in "+(struct instanceof StructureComponent ? struct+" - "+((StructureComponent)struct).getBoundingBox() : struct.toString()));
		}
		else {
			DragonAPICore.logError("Tried to fire an event for chest loot from an unrecognized structure "+struct+"!");
		}
	}

	private static String calculateTable(Object struct) {
		ChestGenLootLocation find = LootController.getLocationForStructure(struct);
		return find != null ? find.getTag() : null;
	}

}
