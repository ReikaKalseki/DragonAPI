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

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class IDMap {

	private static final BiMap<Block, Integer> blockIDs = HashBiMap.create();
	private static final BiMap<Item, Integer> itemIDs = HashBiMap.create();

	public static int getIntegerForBlock(Block b) {
		return blockIDs.get(b);
	}

	public static Block getBlockFromInt(int id) {
		return blockIDs.inverse().get(id);
	}

	public static int getIntegerForItem(Item b) {
		return itemIDs.get(b);
	}

	public static Item getItemFromInt(int id) {
		return itemIDs.inverse().get(id);
	}

	private static void addBlock(Block b, int id) {
		blockIDs.put(b, id);
	}

	private static void addItem(Item i, int id) {
		itemIDs.put(i, id+256);
	}

	static {
		addBlock(Block.stone, 1);
		addBlock(Block.grass, 2);
		addBlock(Block.dirt, 3);
		addBlock(Block.cobblestone, 4);
		addBlock(Block.planks, 5);
		addBlock(Block.sapling, 6);
		addBlock(Block.bedrock, 7);
		addBlock(Block.waterMoving, 8);
		addBlock(Block.waterStill, 9);
		addBlock(Block.lavaMoving, 10);
		addBlock(Block.lavaStill, 11);
		addBlock(Block.sand, 12);
		addBlock(Block.gravel, 13);
		addBlock(Block.oreGold, 14);
		addBlock(Block.oreIron, 15);
		addBlock(Block.oreCoal, 16);
		addBlock(Block.wood, 17);
		addBlock(Block.leaves, 18);
		addBlock(Block.sponge, 19);
		addBlock(Block.glass, 20);
		addBlock(Block.oreLapis, 21);
		addBlock(Block.blockLapis, 22);
		addBlock(Block.dispenser, 23);
		addBlock(Block.sandStone, 24);
		addBlock(Block.music, 25);
		addBlock(Block.bed, 26);
		addBlock(Block.railPowered, 27);
		addBlock(Block.railDetector, 28);
		addBlock(Block.pistonStickyBase, 29);
		addBlock(Block.web, 30);
		addBlock(Block.tallGrass, 31);
		addBlock(Block.deadBush, 32);
		addBlock(Block.pistonBase, 33);
		addBlock(Block.pistonExtension, 34);
		addBlock(Block.cloth, 35);
		addBlock(Block.pistonMoving, 36);
		addBlock(Block.plantYellow, 37);
		addBlock(Block.plantRed, 38);
		addBlock(Block.mushroomBrown, 39);
		addBlock(Block.mushroomRed, 40);
		addBlock(Block.blockGold, 41);
		addBlock(Block.blockIron, 42);
		addBlock(Block.stoneDoubleSlab, 43);
		addBlock(Block.stoneSingleSlab, 44);
		addBlock(Block.brick, 45);
		addBlock(Block.tnt, 46);
		addBlock(Block.bookShelf, 47);
		addBlock(Block.cobblestoneMossy, 48);
		addBlock(Block.obsidian, 49);
		addBlock(Block.torchWood, 50);
		addBlock(Block.fire, 51);
		addBlock(Block.mobSpawner, 52);
		addBlock(Block.stairsWoodOak, 53);
		addBlock(Block.chest, 54);
		addBlock(Block.redstoneWire, 55);
		addBlock(Block.oreDiamond, 56);
		addBlock(Block.blockDiamond, 57);
		addBlock(Block.workbench, 58);
		addBlock(Block.crops, 59);
		addBlock(Block.tilledField, 60);
		addBlock(Block.furnaceIdle, 61);
		addBlock(Block.furnaceBurning, 62);
		addBlock(Block.signPost, 63);
		addBlock(Block.doorWood, 64);
		addBlock(Block.ladder, 65);
		addBlock(Block.rail, 66);
		addBlock(Block.stairsCobblestone, 67);
		addBlock(Block.signWall, 68);
		addBlock(Block.lever, 69);
		addBlock(Block.pressurePlateStone, 70);
		addBlock(Block.doorIron, 71);
		addBlock(Block.pressurePlatePlanks, 72);
		addBlock(Block.oreRedstone, 73);
		addBlock(Block.oreRedstoneGlowing, 74);
		addBlock(Block.torchRedstoneIdle, 75);
		addBlock(Block.torchRedstoneActive, 76);
		addBlock(Block.stoneButton, 77);
		addBlock(Block.snow, 78);
		addBlock(Block.ice, 79);
		addBlock(Block.blockSnow, 80);
		addBlock(Block.cactus, 81);
		addBlock(Block.blockClay, 82);
		addBlock(Block.reed, 83);
		addBlock(Block.jukebox, 84);
		addBlock(Block.fence, 85);
		addBlock(Block.pumpkin, 86);
		addBlock(Block.netherrack, 87);
		addBlock(Block.slowSand, 88);
		addBlock(Block.glowStone, 89);
		addBlock(Block.portal, 90);
		addBlock(Block.pumpkinLantern, 91);
		addBlock(Block.cake, 92);
		addBlock(Block.redstoneRepeaterIdle, 93);
		addBlock(Block.redstoneRepeaterActive, 94);
		addBlock(Block.lockedChest, 95);
		addBlock(Block.trapdoor, 96);
		addBlock(Block.silverfish, 97);
		addBlock(Block.stoneBrick, 98);
		addBlock(Block.mushroomCapBrown, 99);
		addBlock(Block.mushroomCapRed, 100);
		addBlock(Block.fenceIron, 101);
		addBlock(Block.thinGlass, 102);
		addBlock(Block.melon, 103);
		addBlock(Block.pumpkinStem, 104);
		addBlock(Block.melonStem, 105);
		addBlock(Block.vine, 106);
		addBlock(Block.fenceGate, 107);
		addBlock(Block.stairsBrick, 108);
		addBlock(Block.stairsStoneBrick, 109);
		addBlock(Block.mycelium, 110);
		addBlock(Block.waterlily, 111);
		addBlock(Block.netherBrick, 112);
		addBlock(Block.netherFence, 113);
		addBlock(Block.stairsNetherBrick, 114);
		addBlock(Block.netherStalk, 115);
		addBlock(Block.enchantmentTable, 116);
		addBlock(Block.brewingStand, 117);
		addBlock(Block.cauldron, 118);
		addBlock(Block.endPortal, 119);
		addBlock(Block.endPortalFrame, 120);
		addBlock(Block.whiteStone, 121);
		addBlock(Block.dragonEgg, 122);
		addBlock(Block.redstoneLampIdle, 123);
		addBlock(Block.redstoneLampActive, 124);
		addBlock(Block.woodDoubleSlab, 125);
		addBlock(Block.woodSingleSlab, 126);
		addBlock(Block.cocoaPlant, 127);
		addBlock(Block.stairsSandStone, 128);
		addBlock(Block.oreEmerald, 129);
		addBlock(Block.enderChest, 130);
		addBlock(Block.tripWireSource, 131);
		addBlock(Block.tripWire, 132);
		addBlock(Block.blockEmerald, 133);
		addBlock(Block.stairsWoodSpruce, 134);
		addBlock(Block.stairsWoodBirch, 135);
		addBlock(Block.stairsWoodJungle, 136);
		addBlock(Block.commandBlock, 137);
		addBlock(Block.beacon, 138);
		addBlock(Block.cobblestoneWall, 139);
		addBlock(Block.flowerPot, 140);
		addBlock(Block.carrot, 141);
		addBlock(Block.potato, 142);
		addBlock(Block.woodenButton, 143);
		addBlock(Block.skull, 144);
		addBlock(Block.anvil, 145);
		addBlock(Block.chestTrapped, 146);
		addBlock(Block.pressurePlateGold, 147);
		addBlock(Block.pressurePlateIron, 148);
		addBlock(Block.redstoneComparatorIdle, 149);
		addBlock(Block.redstoneComparatorActive, 150);
		addBlock(Block.daylightSensor, 151);
		addBlock(Block.blockRedstone, 152);
		addBlock(Block.oreNetherQuartz, 153);
		addBlock(Block.hopperBlock, 154);
		addBlock(Block.blockNetherQuartz, 155);
		addBlock(Block.stairsNetherQuartz, 156);
		addBlock(Block.railActivator, 157);
		addBlock(Block.dropper, 158);

		addItem(Item.shovelIron, 0);
		addItem(Item.pickaxeIron, 1);
		addItem(Item.axeIron, 2);
		addItem(Item.flintAndSteel, 3);
		addItem(Item.appleRed, 4);
		addItem(Item.bow, 5);
		addItem(Item.arrow, 6);
		addItem(Item.coal, 7);
		addItem(Item.diamond, 8);
		addItem(Item.ingotIron, 9);
		addItem(Item.ingotGold, 10);
		addItem(Item.swordIron, 11);
		addItem(Item.swordWood, 12);
		addItem(Item.shovelWood, 13);
		addItem(Item.pickaxeWood, 14);
		addItem(Item.axeWood, 15);
		addItem(Item.swordStone, 16);
		addItem(Item.shovelStone, 17);
		addItem(Item.pickaxeStone, 18);
		addItem(Item.axeStone, 19);
		addItem(Item.swordDiamond, 20);
		addItem(Item.shovelDiamond, 21);
		addItem(Item.pickaxeDiamond, 22);
		addItem(Item.axeDiamond, 23);
		addItem(Item.stick, 24);
		addItem(Item.bowlEmpty, 25);
		addItem(Item.bowlSoup, 26);
		addItem(Item.swordGold, 27);
		addItem(Item.shovelGold, 28);
		addItem(Item.pickaxeGold, 29);
		addItem(Item.axeGold, 30);
		addItem(Item.silk, 31);
		addItem(Item.feather, 32);
		addItem(Item.gunpowder, 33);
		addItem(Item.hoeWood, 34);
		addItem(Item.hoeStone, 35);
		addItem(Item.hoeIron, 36);
		addItem(Item.hoeDiamond, 37);
		addItem(Item.hoeGold, 38);
		addItem(Item.seeds, 39);
		addItem(Item.wheat, 40);
		addItem(Item.bread, 41);
		addItem(Item.helmetLeather, 42);
		addItem(Item.plateLeather, 43);
		addItem(Item.legsLeather, 44);
		addItem(Item.bootsLeather, 45);
		addItem(Item.helmetChain, 46);
		addItem(Item.plateChain, 47);
		addItem(Item.legsChain, 48);
		addItem(Item.bootsChain, 49);
		addItem(Item.helmetIron, 50);
		addItem(Item.plateIron, 51);
		addItem(Item.legsIron, 52);
		addItem(Item.bootsIron, 53);
		addItem(Item.helmetDiamond, 54);
		addItem(Item.plateDiamond, 55);
		addItem(Item.legsDiamond, 56);
		addItem(Item.bootsDiamond, 57);
		addItem(Item.helmetGold, 58);
		addItem(Item.plateGold, 59);
		addItem(Item.legsGold, 60);
		addItem(Item.bootsGold, 61);
		addItem(Item.flint, 62);
		addItem(Item.porkRaw, 63);
		addItem(Item.porkCooked, 64);
		addItem(Item.painting, 65);
		addItem(Item.appleGold, 66);
		addItem(Item.sign, 67);
		addItem(Item.doorWood, 68);
		addItem(Item.bucketEmpty, 69);
		addItem(Item.bucketWater, 70);
		addItem(Item.bucketLava, 71);
		addItem(Item.minecartEmpty, 72);
		addItem(Item.saddle, 73);
		addItem(Item.doorIron, 74);
		addItem(Item.redstone, 75);
		addItem(Item.snowball, 76);
		addItem(Item.boat, 77);
		addItem(Item.leather, 78);
		addItem(Item.bucketMilk, 79);
		addItem(Item.brick, 80);
		addItem(Item.clay, 81);
		addItem(Item.reed, 82);
		addItem(Item.paper, 83);
		addItem(Item.book, 84);
		addItem(Item.slimeBall, 85);
		addItem(Item.minecartCrate, 86);
		addItem(Item.minecartPowered, 87);
		addItem(Item.egg, 88);
		addItem(Item.compass, 89);
		addItem(Item.fishingRod, 90);
		addItem(Item.pocketSundial, 91);
		addItem(Item.glowstone, 92);
		addItem(Item.fishRaw, 93);
		addItem(Item.fishCooked, 94);
		addItem(Item.dyePowder, 95);
		addItem(Item.bone, 96);
		addItem(Item.sugar, 97);
		addItem(Item.cake, 98);
		addItem(Item.bed, 99);
		addItem(Item.redstoneRepeater, 100);
		addItem(Item.cookie, 101);
		addItem(Item.map, 102);
		addItem(Item.shears, 103);
		addItem(Item.melon, 104);
		addItem(Item.pumpkinSeeds, 105);
		addItem(Item.melonSeeds, 106);
		addItem(Item.beefRaw, 107);
		addItem(Item.beefCooked, 108);
		addItem(Item.chickenRaw, 109);
		addItem(Item.chickenCooked, 110);
		addItem(Item.rottenFlesh, 111);
		addItem(Item.enderPearl, 112);
		addItem(Item.blazeRod, 113);
		addItem(Item.ghastTear, 114);
		addItem(Item.goldNugget, 115);
		addItem(Item.netherStalkSeeds, 116);
		addItem(Item.potion, 117);
		addItem(Item.glassBottle, 118);
		addItem(Item.spiderEye, 119);
		addItem(Item.fermentedSpiderEye, 120);
		addItem(Item.blazePowder, 121);
		addItem(Item.magmaCream, 122);
		addItem(Item.brewingStand, 123);
		addItem(Item.cauldron, 124);
		addItem(Item.eyeOfEnder, 125);
		addItem(Item.speckledMelon, 126);
		addItem(Item.monsterPlacer, 127);
		addItem(Item.expBottle, 128);
		addItem(Item.fireballCharge, 129);
		addItem(Item.writableBook, 130);
		addItem(Item.writtenBook, 131);
		addItem(Item.emerald, 132);
		addItem(Item.itemFrame, 133);
		addItem(Item.flowerPot, 134);
		addItem(Item.carrot, 135);
		addItem(Item.potato, 136);
		addItem(Item.bakedPotato, 137);
		addItem(Item.poisonousPotato, 138);
		addItem(Item.emptyMap, 139);
		addItem(Item.goldenCarrot, 140);
		addItem(Item.skull, 141);
		addItem(Item.carrotOnAStick, 142);
		addItem(Item.netherStar, 143);
		addItem(Item.pumpkinPie, 144);
		addItem(Item.firework, 145);
		addItem(Item.fireworkCharge, 146);
		addItem(Item.enchantedBook, 147);
		addItem(Item.comparator, 148);
		addItem(Item.netherrackBrick, 149);
		addItem(Item.netherQuartz, 150);
		addItem(Item.minecartTnt, 151);
		addItem(Item.minecartHopper, 152);
		addItem(Item.record13, 2000);
		addItem(Item.recordCat, 2001);
		addItem(Item.recordBlocks, 2002);
		addItem(Item.recordChirp, 2003);
		addItem(Item.recordFar, 2004);
		addItem(Item.recordMall, 2005);
		addItem(Item.recordMellohi, 2006);
		addItem(Item.recordStal, 2007);
		addItem(Item.recordStrad, 2008);
		addItem(Item.recordWard, 2009);
		addItem(Item.record11, 2010);
		addItem(Item.recordWait, 2011);
	}
}
