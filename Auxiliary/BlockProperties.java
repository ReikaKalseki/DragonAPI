/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockProperties {

	/** A catalogue of all flammable blocks by ID. */
	private static HashMap<Block, Boolean> flammableArray = new HashMap();

	/** A catalogue of all soft (replaceable, like water, tall grass, etc) blocks by ID. */
	private static HashMap<Block, Boolean> softBlocksArray = new HashMap();

	/** A catalogue of all nonsolid (no hitbox) blocks by ID. */
	private static HashMap<Block, Boolean> nonSolidArray = new HashMap();

	/** A catalogue of all block colors by ID. */
	public static int[] blockColorArray = new int[4096];

	public static void setNonSolid() {
		nonSolidArray.put(Blocks.air, true);
		//nonSolidArray.put(Blocks.piston_extenstion, true); //block 36
		nonSolidArray.put(Blocks.water, true);
		nonSolidArray.put(Blocks.flowing_water, true);
		nonSolidArray.put(Blocks.lava, true);
		nonSolidArray.put(Blocks.flowing_lava, true);
		nonSolidArray.put(Blocks.tallgrass, true);
		nonSolidArray.put(Blocks.deadbush, true);
		nonSolidArray.put(Blocks.fire, true);
		nonSolidArray.put(Blocks.snow, true);
		nonSolidArray.put(Blocks.vine, true);
		nonSolidArray.put(Blocks.torch, true);
		nonSolidArray.put(Blocks.sapling, true);
		nonSolidArray.put(Blocks.rail, true);
		nonSolidArray.put(Blocks.golden_rail, true);
		nonSolidArray.put(Blocks.detector_rail, true);
		nonSolidArray.put(Blocks.yellow_flower, true);
		nonSolidArray.put(Blocks.red_flower, true);
		nonSolidArray.put(Blocks.brown_mushroom, true);
		nonSolidArray.put(Blocks.red_mushroom, true);
		nonSolidArray.put(Blocks.redstone_wire, true);
		nonSolidArray.put(Blocks.wheat, true);
		nonSolidArray.put(Blocks.standing_sign, true);
		nonSolidArray.put(Blocks.wall_sign, true);
		nonSolidArray.put(Blocks.wooden_door, true);
		nonSolidArray.put(Blocks.iron_door, true);
		nonSolidArray.put(Blocks.ladder, true);
		nonSolidArray.put(Blocks.wooden_pressure_plate, true);
		nonSolidArray.put(Blocks.stone_pressure_plate, true);
		nonSolidArray.put(Blocks.lever, true);
		nonSolidArray.put(Blocks.stone_button, true);
		nonSolidArray.put(Blocks.carrots, true);
		nonSolidArray.put(Blocks.potatoes, true);
		nonSolidArray.put(Blocks.unlit_redstone_torch, true);
		nonSolidArray.put(Blocks.redstone_torch, true);
		nonSolidArray.put(Blocks.reeds, true);
		nonSolidArray.put(Blocks.portal, true);
		nonSolidArray.put(Blocks.unpowered_repeater, true);
		nonSolidArray.put(Blocks.powered_repeater, true);
		nonSolidArray.put(Blocks.trapdoor, true);
		//nonSolidArray.put(Blocks.iron_bars, true);
		//nonSolidArray.put(Blocks.glass_pane, true);
		nonSolidArray.put(Blocks.pumpkin_stem, true);
		nonSolidArray.put(Blocks.melon_stem, true);
		nonSolidArray.put(Blocks.waterlily, true);
		nonSolidArray.put(Blocks.end_portal, true);
		nonSolidArray.put(Blocks.nether_wart, true);
		nonSolidArray.put(Blocks.tripwire, true);
		nonSolidArray.put(Blocks.tripwire_hook, true);
		nonSolidArray.put(Blocks.flower_pot, true);
		nonSolidArray.put(Blocks.wooden_button, true);
		nonSolidArray.put(Blocks.skull, true);

	}

	public static void setSoft() {
		softBlocksArray.put(Blocks.air, true);
		softBlocksArray.put(Blocks.piston_extension, true); //block 36
		softBlocksArray.put(Blocks.water, true);
		softBlocksArray.put(Blocks.flowing_water, true);
		softBlocksArray.put(Blocks.lava, true);
		softBlocksArray.put(Blocks.flowing_lava, true);
		softBlocksArray.put(Blocks.tallgrass, true);
		softBlocksArray.put(Blocks.deadbush, true);
		softBlocksArray.put(Blocks.fire, true);
		softBlocksArray.put(Blocks.snow, true);
		softBlocksArray.put(Blocks.vine, true);
	}

	public static void setFlammable() {
		flammableArray.put(Blocks.planks, true);
		flammableArray.put(Blocks.log, true);
		flammableArray.put(Blocks.log2, true);
		flammableArray.put(Blocks.leaves, true);
		flammableArray.put(Blocks.leaves2, true);
		flammableArray.put(Blocks.noteblock, true);
		flammableArray.put(Blocks.tallgrass, true);
		flammableArray.put(Blocks.deadbush, true);
		flammableArray.put(Blocks.wool, true);
		flammableArray.put(Blocks.tnt, true);
		flammableArray.put(Blocks.bookshelf, true);
		flammableArray.put(Blocks.oak_stairs, true);
		flammableArray.put(Blocks.jukebox, true);
		flammableArray.put(Blocks.vine, true);
		flammableArray.put(Blocks.wooden_slab, true);
		flammableArray.put(Blocks.double_wooden_slab, true);
		flammableArray.put(Blocks.spruce_stairs, true);
		flammableArray.put(Blocks.birch_stairs, true);
		flammableArray.put(Blocks.jungle_stairs, true);
	}

	static {
		setNonSolid();
		setSoft();
		setFlammable();
	}

	public static boolean isFlammable(Block b) {
		return flammableArray.containsKey(b) && flammableArray.get(b);
	}

	public static boolean isSoft(Block b) {
		return softBlocksArray.containsKey(b) && softBlocksArray.get(b);
	}

	public static boolean isNonSolid(Block b) {
		return nonSolidArray.containsKey(b) && nonSolidArray.get(b);
	}
}
