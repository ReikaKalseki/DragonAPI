package Reika.DragonAPI.Auxiliary;

import net.minecraft.block.Block;

import Reika.DragonAPI.Libraries.ReikaGuiAPI;

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
		blockColorArray[0] = ReikaGuiAPI.RGBtoHex(33);
		blockColorArray[1] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[2] = ReikaGuiAPI.RGBtoHex(104, 167, 65);
		blockColorArray[3] = ReikaGuiAPI.RGBtoHex(120, 85, 60);
		blockColorArray[4] = ReikaGuiAPI.RGBtoHex(99);
		blockColorArray[5] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[6] = ReikaGuiAPI.RGBtoHex(0, 255, 0);
		blockColorArray[7] = ReikaGuiAPI.RGBtoHex(50);
		blockColorArray[8] = ReikaGuiAPI.RGBtoHex(0, 0, 255);
		blockColorArray[9] = ReikaGuiAPI.RGBtoHex(0, 0, 255);
		blockColorArray[10] = ReikaGuiAPI.RGBtoHex(255, 40, 0);
		blockColorArray[11] = ReikaGuiAPI.RGBtoHex(255, 40, 0);
		blockColorArray[12] = ReikaGuiAPI.RGBtoHex(225, 219, 163);
		blockColorArray[13] = ReikaGuiAPI.RGBtoHex(159, 137, 131);
		blockColorArray[14] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[15] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[16] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[17] = ReikaGuiAPI.RGBtoHex(103, 83, 53);
		blockColorArray[18] = ReikaGuiAPI.RGBtoHex(87, 171, 65);
		blockColorArray[19] = ReikaGuiAPI.RGBtoHex(204, 204, 71);
		blockColorArray[20] = ReikaGuiAPI.RGBtoHex(190, 244, 254);
		blockColorArray[21] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[22] = ReikaGuiAPI.RGBtoHex(21, 52, 188);
		blockColorArray[23] = ReikaGuiAPI.RGBtoHex(119);
		blockColorArray[24] = ReikaGuiAPI.RGBtoHex(212, 205, 153);
		blockColorArray[25] = ReikaGuiAPI.RGBtoHex(147, 90, 64);
		blockColorArray[26] = ReikaGuiAPI.RGBtoHex(136, 27, 27);
		blockColorArray[27] = ReikaGuiAPI.RGBtoHex(220, 182, 47);
		blockColorArray[28] = ReikaGuiAPI.RGBtoHex(134, 0, 0);
		blockColorArray[29] = ReikaGuiAPI.RGBtoHex(122, 190, 111);
		blockColorArray[30] = ReikaGuiAPI.RGBtoHex(220);
		blockColorArray[31] = ReikaGuiAPI.RGBtoHex(104, 167, 65);
		blockColorArray[32] = ReikaGuiAPI.RGBtoHex(146, 99, 44);
		blockColorArray[33] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[34] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[35] = ReikaGuiAPI.RGBtoHex(240);
		blockColorArray[36] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[37] = ReikaGuiAPI.RGBtoHex(255, 255, 0);
		blockColorArray[38] = ReikaGuiAPI.RGBtoHex(255, 0, 0);
		blockColorArray[39] = ReikaGuiAPI.RGBtoHex(202, 151, 119);
		blockColorArray[40] = ReikaGuiAPI.RGBtoHex(225, 24, 25);
		blockColorArray[41] = ReikaGuiAPI.RGBtoHex(255, 240, 69);
		blockColorArray[42] = ReikaGuiAPI.RGBtoHex(232);
		blockColorArray[43] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[44] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[45] = ReikaGuiAPI.RGBtoHex(175, 91, 72);
		blockColorArray[46] = ReikaGuiAPI.RGBtoHex(216, 58, 19);
		blockColorArray[47] = ReikaGuiAPI.RGBtoHex(186, 150, 98);
		blockColorArray[48] = ReikaGuiAPI.RGBtoHex(69, 143, 69);
		blockColorArray[49] = ReikaGuiAPI.RGBtoHex(62, 51, 86);
		blockColorArray[50] = ReikaGuiAPI.RGBtoHex(255, 214, 0);
		blockColorArray[51] = ReikaGuiAPI.RGBtoHex(255, 170, 0);
		blockColorArray[52] = ReikaGuiAPI.RGBtoHex(39, 64, 81);
		blockColorArray[53] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[54] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[55] = ReikaGuiAPI.RGBtoHex(145, 0, 16);
		blockColorArray[56] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[57] = ReikaGuiAPI.RGBtoHex(104, 222, 217);
		blockColorArray[58] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[59] = ReikaGuiAPI.RGBtoHex(4, 189, 18);
		blockColorArray[60] = ReikaGuiAPI.RGBtoHex(96, 55, 27);
		blockColorArray[61] = ReikaGuiAPI.RGBtoHex(119);
		blockColorArray[62] = ReikaGuiAPI.RGBtoHex(119);
		blockColorArray[63] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[64] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[65] = ReikaGuiAPI.RGBtoHex(170, 134, 82);
		blockColorArray[66] = ReikaGuiAPI.RGBtoHex(170, 134, 82);
		blockColorArray[67] = ReikaGuiAPI.RGBtoHex(99);
		blockColorArray[68] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[69] = ReikaGuiAPI.RGBtoHex(123, 98, 64);
		blockColorArray[70] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[71] = ReikaGuiAPI.RGBtoHex(222);
		blockColorArray[72] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[73] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[74] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[75] = ReikaGuiAPI.RGBtoHex(86, 0, 0);
		blockColorArray[76] = ReikaGuiAPI.RGBtoHex(173, 0, 0);
		blockColorArray[77] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[78] = ReikaGuiAPI.RGBtoHex(255);
		blockColorArray[79] = ReikaGuiAPI.RGBtoHex(117, 166, 255);
		blockColorArray[80] = ReikaGuiAPI.RGBtoHex(255);
		blockColorArray[81] = ReikaGuiAPI.RGBtoHex(24, 126, 37);
		blockColorArray[82] = ReikaGuiAPI.RGBtoHex(171, 175, 191);
		blockColorArray[83] = ReikaGuiAPI.RGBtoHex(168, 217, 115);
		blockColorArray[84] = ReikaGuiAPI.RGBtoHex(147, 90, 64);
		blockColorArray[85] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[86] = ReikaGuiAPI.RGBtoHex(226, 142, 34);
		blockColorArray[87] = ReikaGuiAPI.RGBtoHex(163, 66, 66);
		blockColorArray[88] = ReikaGuiAPI.RGBtoHex(92, 74, 63);
		blockColorArray[89] = ReikaGuiAPI.RGBtoHex(248, 210, 154);
		blockColorArray[90] = ReikaGuiAPI.RGBtoHex(128, 0, 255);
		blockColorArray[91] = ReikaGuiAPI.RGBtoHex(226, 142, 34);
		blockColorArray[92] = ReikaGuiAPI.RGBtoHex(165, 83, 37);
		blockColorArray[93] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[94] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[95] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[96] = ReikaGuiAPI.RGBtoHex(141, 106, 55);
		blockColorArray[97] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[98] = ReikaGuiAPI.RGBtoHex(135);
		blockColorArray[99] = ReikaGuiAPI.RGBtoHex(148, 113, 90);
		blockColorArray[100] = ReikaGuiAPI.RGBtoHex(179, 34, 32);
		blockColorArray[101] = ReikaGuiAPI.RGBtoHex(106, 104, 106);
		blockColorArray[102] = ReikaGuiAPI.RGBtoHex(190, 244, 254);
		blockColorArray[103] = ReikaGuiAPI.RGBtoHex(175, 173, 43);
		blockColorArray[104] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[105] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[106] = ReikaGuiAPI.RGBtoHex(26, 139, 40);
		blockColorArray[107] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[108] = ReikaGuiAPI.RGBtoHex(175, 91, 72);
		blockColorArray[109] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[110] = ReikaGuiAPI.RGBtoHex(97, 82, 104);
		blockColorArray[111] = ReikaGuiAPI.RGBtoHex(30, 53, 15);
		blockColorArray[112] = ReikaGuiAPI.RGBtoHex(73, 39, 46);
		blockColorArray[113] = ReikaGuiAPI.RGBtoHex(73, 39, 46);
		blockColorArray[114] = ReikaGuiAPI.RGBtoHex(73, 39, 46);
		blockColorArray[115] = ReikaGuiAPI.RGBtoHex(159, 41, 45);
		blockColorArray[116] = ReikaGuiAPI.RGBtoHex(160, 46, 45);
		blockColorArray[117] = ReikaGuiAPI.RGBtoHex(196, 186, 81);
		blockColorArray[118] = ReikaGuiAPI.RGBtoHex(59);
		blockColorArray[119] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[120] = ReikaGuiAPI.RGBtoHex(67, 114, 102);
		blockColorArray[121] = ReikaGuiAPI.RGBtoHex(234, 247, 180);
		blockColorArray[122] = ReikaGuiAPI.RGBtoHex(48, 5, 54);
		blockColorArray[123] = ReikaGuiAPI.RGBtoHex(222, 147, 71);
		blockColorArray[124] = ReikaGuiAPI.RGBtoHex(222, 147, 71);
		blockColorArray[125] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[126] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[127] = ReikaGuiAPI.RGBtoHex(177, 98, 28);
		blockColorArray[128] = ReikaGuiAPI.RGBtoHex(212, 205, 153);
		blockColorArray[129] = ReikaGuiAPI.RGBtoHex(126);
		blockColorArray[130] = ReikaGuiAPI.RGBtoHex(43, 61, 63);
		blockColorArray[131] = ReikaGuiAPI.RGBtoHex(33); //render tripwires as air
		blockColorArray[132] = ReikaGuiAPI.RGBtoHex(33);
		blockColorArray[133] = ReikaGuiAPI.RGBtoHex(63, 213, 102);
		blockColorArray[134] = ReikaGuiAPI.RGBtoHex(127, 94, 56);
		blockColorArray[135] = ReikaGuiAPI.RGBtoHex(213, 201, 139);
		blockColorArray[136] = ReikaGuiAPI.RGBtoHex(182, 133, 99);
		blockColorArray[137] = ReikaGuiAPI.RGBtoHex(199, 126, 79);
		blockColorArray[138] = ReikaGuiAPI.RGBtoHex(44, 197, 87);
		blockColorArray[139] = ReikaGuiAPI.RGBtoHex(99);
		blockColorArray[140] = ReikaGuiAPI.RGBtoHex(116, 63, 48);
		blockColorArray[141] = ReikaGuiAPI.RGBtoHex(4, 189, 18);
		blockColorArray[142] = ReikaGuiAPI.RGBtoHex(4, 189, 18);
		blockColorArray[143] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[144] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[145] = ReikaGuiAPI.RGBtoHex(67);
		blockColorArray[146] = ReikaGuiAPI.RGBtoHex(178, 142, 90);
		blockColorArray[147] = ReikaGuiAPI.RGBtoHex(255, 240, 69);
		blockColorArray[148] = ReikaGuiAPI.RGBtoHex(232);
		blockColorArray[149] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[150] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[151] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[152] = ReikaGuiAPI.RGBtoHex(255, 100, 0);
		blockColorArray[153] = ReikaGuiAPI.RGBtoHex(163, 66, 66);
		blockColorArray[154] = ReikaGuiAPI.RGBtoHex(75);
		blockColorArray[156] = ReikaGuiAPI.RGBtoHex(-1);
		blockColorArray[157] = ReikaGuiAPI.RGBtoHex(183, 12, 12);
		blockColorArray[158] = ReikaGuiAPI.RGBtoHex(119);
		if (!renderOres)
			return;
		blockColorArray[16] = ReikaGuiAPI.RGBtoHex(70);
		blockColorArray[15] = ReikaGuiAPI.RGBtoHex(214, 173, 145);
		blockColorArray[14] = ReikaGuiAPI.RGBtoHex(251, 237, 76);
		blockColorArray[21] = ReikaGuiAPI.RGBtoHex(40, 98, 175);
		blockColorArray[73] = ReikaGuiAPI.RGBtoHex(215, 0, 0);
		blockColorArray[56] = ReikaGuiAPI.RGBtoHex(93, 235, 244);
		blockColorArray[129] = ReikaGuiAPI.RGBtoHex(23, 221, 98);
		blockColorArray[153] = ReikaGuiAPI.RGBtoHex(203, 191, 177);
	}
}
