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
import Reika.DragonAPI.Libraries.ReikaColorAPI;

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

	public static void setBlockColors(boolean renderOres) {
		blockColorArray[0] = ReikaColorAPI.RGBtoHex(33);
		blockColorArray[1] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[2] = ReikaColorAPI.RGBtoHex(104, 167, 65);
		blockColorArray[3] = ReikaColorAPI.RGBtoHex(120, 85, 60);
		blockColorArray[4] = ReikaColorAPI.RGBtoHex(99);
		blockColorArray[5] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[6] = ReikaColorAPI.RGBtoHex(0, 255, 0);
		blockColorArray[7] = ReikaColorAPI.RGBtoHex(50);
		blockColorArray[8] = ReikaColorAPI.RGBtoHex(0, 0, 255);
		blockColorArray[9] = ReikaColorAPI.RGBtoHex(0, 0, 255);
		blockColorArray[10] = ReikaColorAPI.RGBtoHex(255, 40, 0);
		blockColorArray[11] = ReikaColorAPI.RGBtoHex(255, 40, 0);
		blockColorArray[12] = ReikaColorAPI.RGBtoHex(225, 219, 163);
		blockColorArray[13] = ReikaColorAPI.RGBtoHex(159, 137, 131);
		blockColorArray[14] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[15] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[16] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[17] = ReikaColorAPI.RGBtoHex(103, 83, 53);
		blockColorArray[18] = ReikaColorAPI.RGBtoHex(87, 171, 65);
		blockColorArray[19] = ReikaColorAPI.RGBtoHex(204, 204, 71);
		blockColorArray[20] = ReikaColorAPI.RGBtoHex(190, 244, 254);
		blockColorArray[21] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[22] = ReikaColorAPI.RGBtoHex(21, 52, 188);
		blockColorArray[23] = ReikaColorAPI.RGBtoHex(119);
		blockColorArray[24] = ReikaColorAPI.RGBtoHex(212, 205, 153);
		blockColorArray[25] = ReikaColorAPI.RGBtoHex(147, 90, 64);
		blockColorArray[26] = ReikaColorAPI.RGBtoHex(136, 27, 27);
		blockColorArray[27] = ReikaColorAPI.RGBtoHex(220, 182, 47);
		blockColorArray[28] = ReikaColorAPI.RGBtoHex(134, 0, 0);
		blockColorArray[29] = ReikaColorAPI.RGBtoHex(122, 190, 111);
		blockColorArray[30] = ReikaColorAPI.RGBtoHex(220);
		blockColorArray[31] = ReikaColorAPI.RGBtoHex(104, 167, 65);
		blockColorArray[32] = ReikaColorAPI.RGBtoHex(146, 99, 44);
		blockColorArray[33] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[34] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[35] = ReikaColorAPI.RGBtoHex(240);
		blockColorArray[36] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[37] = ReikaColorAPI.RGBtoHex(255, 255, 0);
		blockColorArray[38] = ReikaColorAPI.RGBtoHex(255, 0, 0);
		blockColorArray[39] = ReikaColorAPI.RGBtoHex(202, 151, 119);
		blockColorArray[40] = ReikaColorAPI.RGBtoHex(225, 24, 25);
		blockColorArray[41] = ReikaColorAPI.RGBtoHex(255, 240, 69);
		blockColorArray[42] = ReikaColorAPI.RGBtoHex(232);
		blockColorArray[43] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[44] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[45] = ReikaColorAPI.RGBtoHex(175, 91, 72);
		blockColorArray[46] = ReikaColorAPI.RGBtoHex(216, 58, 19);
		blockColorArray[47] = ReikaColorAPI.RGBtoHex(186, 150, 98);
		blockColorArray[48] = ReikaColorAPI.RGBtoHex(69, 143, 69);
		blockColorArray[49] = ReikaColorAPI.RGBtoHex(62, 51, 86);
		blockColorArray[50] = ReikaColorAPI.RGBtoHex(255, 214, 0);
		blockColorArray[51] = ReikaColorAPI.RGBtoHex(255, 170, 0);
		blockColorArray[52] = ReikaColorAPI.RGBtoHex(39, 64, 81);
		blockColorArray[53] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[54] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[55] = ReikaColorAPI.RGBtoHex(145, 0, 16);
		blockColorArray[56] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[57] = ReikaColorAPI.RGBtoHex(104, 222, 217);
		blockColorArray[58] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[59] = ReikaColorAPI.RGBtoHex(4, 189, 18);
		blockColorArray[60] = ReikaColorAPI.RGBtoHex(96, 55, 27);
		blockColorArray[61] = ReikaColorAPI.RGBtoHex(119);
		blockColorArray[62] = ReikaColorAPI.RGBtoHex(119);
		blockColorArray[63] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[64] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[65] = ReikaColorAPI.RGBtoHex(170, 134, 82);
		blockColorArray[66] = ReikaColorAPI.RGBtoHex(170, 134, 82);
		blockColorArray[67] = ReikaColorAPI.RGBtoHex(99);
		blockColorArray[68] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[69] = ReikaColorAPI.RGBtoHex(123, 98, 64);
		blockColorArray[70] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[71] = ReikaColorAPI.RGBtoHex(222);
		blockColorArray[72] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[73] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[74] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[75] = ReikaColorAPI.RGBtoHex(86, 0, 0);
		blockColorArray[76] = ReikaColorAPI.RGBtoHex(173, 0, 0);
		blockColorArray[77] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[78] = ReikaColorAPI.RGBtoHex(255);
		blockColorArray[79] = ReikaColorAPI.RGBtoHex(117, 166, 255);
		blockColorArray[80] = ReikaColorAPI.RGBtoHex(255);
		blockColorArray[81] = ReikaColorAPI.RGBtoHex(24, 126, 37);
		blockColorArray[82] = ReikaColorAPI.RGBtoHex(171, 175, 191);
		blockColorArray[83] = ReikaColorAPI.RGBtoHex(168, 217, 115);
		blockColorArray[84] = ReikaColorAPI.RGBtoHex(147, 90, 64);
		blockColorArray[85] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[86] = ReikaColorAPI.RGBtoHex(226, 142, 34);
		blockColorArray[87] = ReikaColorAPI.RGBtoHex(163, 66, 66);
		blockColorArray[88] = ReikaColorAPI.RGBtoHex(92, 74, 63);
		blockColorArray[89] = ReikaColorAPI.RGBtoHex(248, 210, 154);
		blockColorArray[90] = ReikaColorAPI.RGBtoHex(128, 0, 255);
		blockColorArray[91] = ReikaColorAPI.RGBtoHex(226, 142, 34);
		blockColorArray[92] = ReikaColorAPI.RGBtoHex(165, 83, 37);
		blockColorArray[93] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[94] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[95] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[96] = ReikaColorAPI.RGBtoHex(141, 106, 55);
		blockColorArray[97] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[98] = ReikaColorAPI.RGBtoHex(135);
		blockColorArray[99] = ReikaColorAPI.RGBtoHex(148, 113, 90);
		blockColorArray[100] = ReikaColorAPI.RGBtoHex(179, 34, 32);
		blockColorArray[101] = ReikaColorAPI.RGBtoHex(106, 104, 106);
		blockColorArray[102] = ReikaColorAPI.RGBtoHex(190, 244, 254);
		blockColorArray[103] = ReikaColorAPI.RGBtoHex(175, 173, 43);
		blockColorArray[104] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[105] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[106] = ReikaColorAPI.RGBtoHex(26, 139, 40);
		blockColorArray[107] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[108] = ReikaColorAPI.RGBtoHex(175, 91, 72);
		blockColorArray[109] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[110] = ReikaColorAPI.RGBtoHex(97, 82, 104);
		blockColorArray[111] = ReikaColorAPI.RGBtoHex(30, 53, 15);
		blockColorArray[112] = ReikaColorAPI.RGBtoHex(73, 39, 46);
		blockColorArray[113] = ReikaColorAPI.RGBtoHex(73, 39, 46);
		blockColorArray[114] = ReikaColorAPI.RGBtoHex(73, 39, 46);
		blockColorArray[115] = ReikaColorAPI.RGBtoHex(159, 41, 45);
		blockColorArray[116] = ReikaColorAPI.RGBtoHex(160, 46, 45);
		blockColorArray[117] = ReikaColorAPI.RGBtoHex(196, 186, 81);
		blockColorArray[118] = ReikaColorAPI.RGBtoHex(59);
		blockColorArray[119] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[120] = ReikaColorAPI.RGBtoHex(67, 114, 102);
		blockColorArray[121] = ReikaColorAPI.RGBtoHex(234, 247, 180);
		blockColorArray[122] = ReikaColorAPI.RGBtoHex(48, 5, 54);
		blockColorArray[123] = ReikaColorAPI.RGBtoHex(222, 147, 71);
		blockColorArray[124] = ReikaColorAPI.RGBtoHex(222, 147, 71);
		blockColorArray[125] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[126] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[127] = ReikaColorAPI.RGBtoHex(177, 98, 28);
		blockColorArray[128] = ReikaColorAPI.RGBtoHex(212, 205, 153);
		blockColorArray[129] = ReikaColorAPI.RGBtoHex(126);
		blockColorArray[130] = ReikaColorAPI.RGBtoHex(43, 61, 63);
		blockColorArray[131] = ReikaColorAPI.RGBtoHex(33); //render tripwires as air
		blockColorArray[132] = ReikaColorAPI.RGBtoHex(33);
		blockColorArray[133] = ReikaColorAPI.RGBtoHex(63, 213, 102);
		blockColorArray[134] = ReikaColorAPI.RGBtoHex(127, 94, 56);
		blockColorArray[135] = ReikaColorAPI.RGBtoHex(213, 201, 139);
		blockColorArray[136] = ReikaColorAPI.RGBtoHex(182, 133, 99);
		blockColorArray[137] = ReikaColorAPI.RGBtoHex(199, 126, 79);
		blockColorArray[138] = ReikaColorAPI.RGBtoHex(44, 197, 87);
		blockColorArray[139] = ReikaColorAPI.RGBtoHex(99);
		blockColorArray[140] = ReikaColorAPI.RGBtoHex(116, 63, 48);
		blockColorArray[141] = ReikaColorAPI.RGBtoHex(4, 189, 18);
		blockColorArray[142] = ReikaColorAPI.RGBtoHex(4, 189, 18);
		blockColorArray[143] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[144] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[145] = ReikaColorAPI.RGBtoHex(67);
		blockColorArray[146] = ReikaColorAPI.RGBtoHex(178, 142, 90);
		blockColorArray[147] = ReikaColorAPI.RGBtoHex(255, 240, 69);
		blockColorArray[148] = ReikaColorAPI.RGBtoHex(232);
		blockColorArray[149] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[150] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[151] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[152] = ReikaColorAPI.RGBtoHex(255, 100, 0);
		blockColorArray[153] = ReikaColorAPI.RGBtoHex(163, 66, 66);
		blockColorArray[154] = ReikaColorAPI.RGBtoHex(75);
		blockColorArray[156] = ReikaColorAPI.RGBtoHex(-1);
		blockColorArray[157] = ReikaColorAPI.RGBtoHex(183, 12, 12);
		blockColorArray[158] = ReikaColorAPI.RGBtoHex(119);
		if (!renderOres)
			return;
		blockColorArray[16] = ReikaColorAPI.RGBtoHex(70);
		blockColorArray[15] = ReikaColorAPI.RGBtoHex(214, 173, 145);
		blockColorArray[14] = ReikaColorAPI.RGBtoHex(251, 237, 76);
		blockColorArray[21] = ReikaColorAPI.RGBtoHex(40, 98, 175);
		blockColorArray[73] = ReikaColorAPI.RGBtoHex(215, 0, 0);
		blockColorArray[74] = ReikaColorAPI.RGBtoHex(215, 0, 0);
		blockColorArray[56] = ReikaColorAPI.RGBtoHex(93, 235, 244);
		blockColorArray[129] = ReikaColorAPI.RGBtoHex(23, 221, 98);
		blockColorArray[153] = ReikaColorAPI.RGBtoHex(203, 191, 177);
	}
}
