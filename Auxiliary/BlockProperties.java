/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.block.Block;

public class BlockProperties {

	/** A catalogue of all flammable blocks by ID. */
	public static boolean[] flammableArray = new boolean[4096];

	/** A catalogue of all soft (replaceable, like water, tall grass, etc) blocks by ID. */
	public static boolean[] softBlocksArray = new boolean[4096];

	/** A catalogue of all nonsolid (no hitbox) blocks by ID. */
	public static boolean[] nonSolidArray = new boolean[4096];

	/** A catalogue of all block colors by ID. */
	public static int[] blockColorArray = new int[4096];

	public static void setNonSolid() {
		nonSolidArray[0] = true;
		//nonSolidArray[36] = true; //block 36
		nonSolidArray[Block.waterStill.blockID] = true;
		nonSolidArray[Block.waterMoving.blockID] = true;
		nonSolidArray[Block.lavaStill.blockID] = true;
		nonSolidArray[Block.lavaMoving.blockID] = true;
		nonSolidArray[Block.tallGrass.blockID] = true;
		nonSolidArray[Block.deadBush.blockID] = true;
		nonSolidArray[Block.fire.blockID] = true;
		nonSolidArray[Block.snow.blockID] = true;
		nonSolidArray[Block.vine.blockID] = true;
		nonSolidArray[Block.torchWood.blockID] = true;
		nonSolidArray[Block.sapling.blockID] = true;
		nonSolidArray[Block.rail.blockID] = true;
		nonSolidArray[Block.railPowered.blockID] = true;
		nonSolidArray[Block.railDetector.blockID] = true;
		nonSolidArray[Block.plantYellow.blockID] = true;
		nonSolidArray[Block.plantRed.blockID] = true;
		nonSolidArray[Block.mushroomBrown.blockID] = true;
		nonSolidArray[Block.mushroomRed.blockID] = true;
		nonSolidArray[Block.redstoneWire.blockID] = true;
		nonSolidArray[Block.crops.blockID] = true;
		nonSolidArray[Block.signPost.blockID] = true;
		nonSolidArray[Block.signWall.blockID] = true;
		nonSolidArray[Block.doorWood.blockID] = true;
		nonSolidArray[Block.doorIron.blockID] = true;
		nonSolidArray[Block.ladder.blockID] = true;
		nonSolidArray[Block.pressurePlatePlanks.blockID] = true;
		nonSolidArray[Block.pressurePlateStone.blockID] = true;
		nonSolidArray[Block.lever.blockID] = true;
		nonSolidArray[Block.stoneButton.blockID] = true;
		nonSolidArray[Block.carrot.blockID] = true;
		nonSolidArray[Block.potato.blockID] = true;
		nonSolidArray[Block.torchRedstoneIdle.blockID] = true;
		nonSolidArray[Block.torchRedstoneActive.blockID] = true;
		nonSolidArray[Block.reed.blockID] = true;
		nonSolidArray[Block.portal.blockID] = true;
		nonSolidArray[Block.redstoneRepeaterIdle.blockID] = true;
		nonSolidArray[Block.redstoneRepeaterActive.blockID] = true;
		nonSolidArray[Block.trapdoor.blockID] = true;
		//nonSolidArray[Block.fenceIron.blockID] = true;
		//nonSolidArray[Block.thinGlass.blockID] = true;
		nonSolidArray[Block.pumpkinStem.blockID] = true;
		nonSolidArray[Block.melonStem.blockID] = true;
		nonSolidArray[Block.waterlily.blockID] = true;
		nonSolidArray[Block.endPortal.blockID] = true;
		nonSolidArray[Block.netherStalk.blockID] = true;
		nonSolidArray[Block.tripWire.blockID] = true;
		nonSolidArray[Block.tripWireSource.blockID] = true;
		nonSolidArray[Block.flowerPot.blockID] = true;
		nonSolidArray[Block.woodenButton.blockID] = true;
		nonSolidArray[Block.skull.blockID] = true;

	}

	public static void setSoft() {
		softBlocksArray[0] = true;
		softBlocksArray[36] = true; //block 36
		softBlocksArray[Block.waterStill.blockID] = true;
		softBlocksArray[Block.waterMoving.blockID] = true;
		softBlocksArray[Block.lavaStill.blockID] = true;
		softBlocksArray[Block.lavaMoving.blockID] = true;
		softBlocksArray[Block.tallGrass.blockID] = true;
		softBlocksArray[Block.deadBush.blockID] = true;
		softBlocksArray[Block.fire.blockID] = true;
		softBlocksArray[Block.snow.blockID] = true;
		softBlocksArray[Block.vine.blockID] = true;
	}

	public static void setFlammable() {
		flammableArray[Block.planks.blockID] = true;
		flammableArray[Block.wood.blockID] = true;
		flammableArray[Block.leaves.blockID] = true;
		flammableArray[Block.music.blockID] = true;
		flammableArray[Block.tallGrass.blockID] = true;
		flammableArray[Block.deadBush.blockID] = true;
		flammableArray[Block.cloth.blockID] = true;
		flammableArray[Block.tnt.blockID] = true;
		flammableArray[Block.bookShelf.blockID] = true;
		flammableArray[Block.stairsWoodOak.blockID] = true;
		flammableArray[Block.jukebox.blockID] = true;
		flammableArray[Block.vine.blockID] = true;
		flammableArray[Block.woodSingleSlab.blockID] = true;
		flammableArray[Block.woodDoubleSlab.blockID] = true;
		flammableArray[Block.stairsWoodSpruce.blockID] = true;
		flammableArray[Block.stairsWoodBirch.blockID] = true;
		flammableArray[Block.stairsWoodJungle.blockID] = true;
	}

	static {
		setNonSolid();
		setSoft();
		setFlammable();
	}
}
