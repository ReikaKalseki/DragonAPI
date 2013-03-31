package Reika.DragonAPI;

import java.util.Random;

import Reika.RotaryCraft.mod_RotaryCraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ReikaWorldHelper {

/** A catalogue of all flammable blocks by ID. */
private static boolean[] flammableArray = new boolean[4096];

/** A catalogue of all soft (replaceable, like water, tall grass, etc) blocks by ID. */
private static boolean[] softBlocksArray = new boolean[4096];

/** A catalogue of all nonsolid (no hitbox) blocks by ID. */
private static boolean[] nonSolidArray = new boolean[4096];

/** A catalogue of all block colors by ID. */
private static int[] blockColorArray = new int[4096];

private static Random par5Random = new Random();

public static boolean softBlocks(int id) {
	setSoft();
	return (softBlocksArray[id]);
}

public static boolean flammable(int id) {
	setFlammable();
	return (flammableArray[id]);
}

public static boolean nonSolidBlocks(int id) {
	setNonSolid();
	return (nonSolidArray[id]);
}

private static void setNonSolid() {
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
	nonSolidArray[Block.doorSteel.blockID] = true;
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
	nonSolidArray[mod_RotaryCraft.canola.blockID] = true;
	nonSolidArray[mod_RotaryCraft.lightblock.blockID] = true;
	nonSolidArray[mod_RotaryCraft.lightbridge.blockID] = true;
	//nonSolidArray[mod_RotaryCraft.sprinkler.blockID] = true;
	
}

private static void setSoft() {
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

private static void setFlammable() {
	flammableArray[Block.planks.blockID] = true;
	flammableArray[Block.wood.blockID] = true;
	flammableArray[Block.leaves.blockID] = true;
	flammableArray[Block.music.blockID] = true;
	flammableArray[Block.tallGrass.blockID] = true;
	flammableArray[Block.deadBush.blockID] = true;
	flammableArray[Block.cloth.blockID] = true;
	flammableArray[Block.tnt.blockID] = true;
	flammableArray[Block.bookShelf.blockID] = true;
	flammableArray[Block.stairCompactPlanks.blockID] = true;
	flammableArray[Block.jukebox.blockID] = true;
	flammableArray[Block.vine.blockID] = true;
	flammableArray[Block.woodSingleSlab.blockID] = true;
	flammableArray[Block.woodDoubleSlab.blockID] = true;
	flammableArray[Block.stairsWoodSpruce.blockID] = true;
	flammableArray[Block.stairsWoodBirch.blockID] = true;
	flammableArray[Block.stairsWoodJungle.blockID] = true;
}

private static void setBlockColors(boolean renderOres) {
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
	blockColorArray[153] = ReikaGuiAPI.RGBtoHex(92, 74, 63);
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
	blockColorArray[153] = ReikaGuiAPI.RGBtoHex(-1);
}

/** Converts the given block ID to a hex color. Renders ores (or disguises as stone) as requested.
 * Args: Block ID, Ore Rendering */
public static int blockColors(int id, boolean renderOres) {
	setBlockColors(renderOres);
	if (blockColorArray[id] == 0)
		return 0xffD47EFF;
	return blockColorArray[id];
}

/** Converts the given coordinates to an RGB representation of those coordinates' biome's color, for the given material type.
 * Args: World, x, z, material (String) */
public static int[] biomeToRGB(World world, int x, int z, String material) {
	BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
	int color = ReikaWorldHelper.biomeToHex(biome, material);
	return ReikaGuiAPI.HexToRGB(color);
}

/** Converts the given coordinates to a hex representation of those coordinates' biome's color, for the given material type.
 * Args: World, x, z, material (String) */
public static int biomeToHexColor(World world, int x, int z, String material) {
	BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
	int color = ReikaWorldHelper.biomeToHex(biome, material);
	return color;
}

private static int biomeToHex(BiomeGenBase biome, String mat) {
	int color = 0;
	if (mat == "Leaves")
		color = biome.getBiomeFoliageColor();
	if (mat == "Grass")
		color = biome.getBiomeGrassColor();
	if (mat == "Water")
		color = biome.getWaterColorMultiplier();
	if (mat == "Sky")
		color = biome.getSkyColorByTemp(biome.getIntTemperature());
	return color;
}

/** Caps the metadata at a certain value (eg, for leaves, metas are from 0-11, but there are only 4 types, and each type has 3 metas).
 * Args: Initial metadata, cap (# of types) */
public static int capMetadata(int meta, int cap) {
	while (meta >= cap)
		meta -= cap;
	return meta;
}

/** Finds the top edge of the top solid (nonair) block in the column. Args: World, this.x,y,z */
public static double findSolidSurface(World world, double x, double y, double z) { //Returns double y-coord of top surface of top block
    	
    	int xp = (int)x;
    	int zp = (int)z;
    	boolean lowestsolid = false;
    	boolean solidup = false;
    	boolean soliddown = false;
    	
    	while (!(!solidup && soliddown)) {
    		solidup = (world.getBlockMaterial(xp, (int)y, zp) != Material.air);
    		soliddown = (world.getBlockMaterial(xp, (int)y-1, zp) != Material.air);
    		if (solidup && soliddown) //Both blocks are solid -> below surface
    			y++;
    		if (solidup && !soliddown) //Upper only is solid -> should never happen
    			y += 2;					// Fix attempt
    		if (!solidup && soliddown) // solid lower only
    			;						// the case we want
    		if (!solidup && !soliddown) //Neither solid -> above surface
    			y--;
    	}
    	return (double)y;
    }
    
/** Finds the top edge of the top water block in the column. Args: World, this.x,y,z */
    public static double findWaterSurface(World world, double x, double y, double z) { //Returns double y-coord of top surface of top block
    	
    	int xp = (int)x;
    	int zp = (int)z;
    	boolean lowestwater = false;
    	boolean waterup = false;
    	boolean waterdown = false;
    	
    	while (!(!waterup && waterdown)) {
    		waterup = (world.getBlockMaterial(xp, (int)y, zp) == Material.water);
    		waterdown = (world.getBlockMaterial(xp, (int)y-1, zp) == Material.water);
    		if (waterup && waterdown) //Both blocks are water -> below surface
    			y++;
    		if (waterup && !waterdown) //Upper only is water -> should never happen
    			return (double)255;		//Return top of chunk and exit function
    		if (!waterup && waterdown) // Water lower only
    			;						// the case we want
    		if (!waterup && !waterdown) //Neither water -> above surface
    			y--;
    	}
    	return (double)y;
    }
    
    /** Search for a specific block in a range. Returns true if found. Cannot identify if
     * found more than one, or where the found one(s) is/are. May be CPU-intensive. Args: World, this.x,y,z, search range, target id */
    public static boolean findNearBlock(World world, int x, int y, int z, int range, int id) {
    	x -= range/2;
    	y -= range/2;
    	z -= range/2;
    	for (int i = 0; i < range; i++) {
    		for (int j = 0; j < range; j++) {
    			for (int k = 0; k < range; k++) {
    				if (world.getBlockId(x+i, y+j, z+k) == id)
    					return true;
    			}
    		}
    	}
    	return false;
    }
    
    /** Search for a specific block in a range. Returns number found. Cannot identify where they
     * are. May be CPU-intensive. Args: World, this.x,y,z, search range, target id */
    public static int findNearBlocks(World world, int x, int y, int z, int range, int id) {
    	int count = 0;
    	x -= range/2;
    	y -= range/2;
    	z -= range/2;
    	for (int i = 0; i < range; i++) {
    		for (int j = 0; j < range; j++) {
    			for (int k = 0; k < range; k++) {
    				if (world.getBlockId(x+i, y+j, z+k) == id)
    					count++;
    			}
    		}
    	}
    	return count;
    }
    
    /** Tests for if a block of a certain id is in the "sights" of a directional block (eg dispenser).
     * Returns the number of blocks away it is. If not found, returns 0 (an impossibility).
     * Args: World, this.x,y,z, search range, target id, direction "f" */
    public static int isLookingAt(World world, int x, int y, int z, int range, int id, int f) {
    	int idfound = 0;
    	
    	switch (f) {
    	case 0:		//facing north (-z);
    		for (int i = 0; i < range; i++) {
    			idfound = world.getBlockId(x, y, z-i);
    			if (idfound == id)
    				return i;
    		}
    	break;
    	case 1:		//facing east (-x);
    		for (int i = 0; i < range; i++) {
    			idfound = world.getBlockId(x-i, y, z);
    			if (idfound == id)
    				return i;
    		}
    	break;
    	case 2:		//facing south (+z);
    		for (int i = 0; i < range; i++) {
    			idfound = world.getBlockId(x, y, z+i);
    			if (idfound == id)
    				return i;
    		}
    	break;
    	case 3:		//facing west (+x);
    		for (int i = 0; i < range; i++) {
    			idfound = world.getBlockId(x+i, y, z);
    			if (idfound == id)
    				return i;
    		}
    	break;
    	}
    	return 0;
    }
    
    /** Returns the direction in which a block of the specified ID was found.
     * Returns -1 if not found. Args: World, x,y,z, id to search.
     * Convention: 0 up 1 down 2 x+ 3 x- 4 z+ 5 z- */
    public static int checkForAdjBlock(World world, int x, int y, int z, int id) {
    	if (world.getBlockId(x,y+1,z) == id)
    		return 0;
    	if (world.getBlockId(x,y-1,z) == id)
    		return 1;
    	if (world.getBlockId(x+1,y,z) == id)
    		return 2;
    	if (world.getBlockId(x-1,y,z) == id)
    		return 3;
    	if (world.getBlockId(x,y,z+1) == id)
    		return 4;
    	if (world.getBlockId(x,y,z-1) == id)
    		return 5;
    	return -1;
    }
    
    /** Returns the direction in which a block of the specified material was found.
     * Returns -1 if not found. Args: World, x,y,z, material to search.
     * Convention: 0 up 1 down 2 x+ 3 x- 4 z+ 5 z- */
    public static int checkForAdjMaterial(World world, int x, int y, int z, Material mat) {
    	if (world.getBlockMaterial(x,y+1,z) == mat)
    		return 0;
    	if (world.getBlockMaterial(x,y-1,z) == mat)
    		return 1;
    	if (world.getBlockMaterial(x+1,y,z) == mat)
    		return 2;
    	if (world.getBlockMaterial(x-1,y,z) == mat)
    		return 3;
    	if (world.getBlockMaterial(x,y,z+1) == mat)
    		return 4;
    	if (world.getBlockMaterial(x,y,z-1) == mat)
    		return 5;
    	return -1;
    }
    
    /** Returns the direction in which a source block of the specified liquid was found.
     * Returns -1 if not found. Args: World, x,y,z, material (water/lava) to search.
     * Convention: 0 up 1 down 2 x+ 3 x- 4 z+ 5 z- */
    public static int checkForAdjSourceBlock(World world, int x, int y, int z, Material mat) {
    	if (world.getBlockMaterial(x, y+1, z) == mat && world.getBlockMetadata(x, y+1, z) == 0)
    		return 0;
    	if (world.getBlockMaterial(x, y-1, z) == mat && world.getBlockMetadata(x, y-1, z) == 0)
    		return 1;
    	if (world.getBlockMaterial(x+1, y, z) == mat && world.getBlockMetadata(x+1, y, z) == 0)
    		return 2;
    	if (world.getBlockMaterial(x-1, y, z) == mat && world.getBlockMetadata(x-1, y, z) == 0)
    		return 3;
    	if (world.getBlockMaterial(x, y, z+1) == mat && world.getBlockMetadata(x, y, z+1) == 0)
    		return 4;
    	if (world.getBlockMaterial(x, y, z-1) == mat && world.getBlockMetadata(x, y, z-1) == 0)
    		return 5;
    	return -1;
    }
    
    /** Edits a block adjacent to the passed arguments, on the specified side.
     * Args: World, x, y, z, side, id to change to */
    public static void changeAdjBlock(World world, int x, int y, int z, int side, int id) {
    	switch(side) {
    	case 0:
    		world.setBlockWithNotify(x, y+1, z, id);
    	break;
    	case 1:
    		world.setBlockWithNotify(x, y-1, z, id);
    	break;
    	case 2:
    		world.setBlockWithNotify(x+1, y, z, id);
    	break;
    	case 3:
    		world.setBlockWithNotify(x-1, y, z, id);
    	break;
    	case 4:
    		world.setBlockWithNotify(x, y, z+1, id);
    	break;
    	case 5:
    		world.setBlockWithNotify(x, y, z-1, id);
    	break;
    	}
    }
    
    /** Returns true if the passed biome is a snow biome.  Args: Biome*/
    public static boolean isSnowBiome(BiomeGenBase biome) {
    	if (biome == BiomeGenBase.frozenOcean)
    		return true;
    	if (biome == BiomeGenBase.frozenRiver)
    		return true;
    	if (biome == BiomeGenBase.iceMountains)
    		return true;
    	if (biome == BiomeGenBase.icePlains)
    		return true;
    	if (biome == BiomeGenBase.taiga)
    		return true;
    	if (biome == BiomeGenBase.taigaHills)
    		return true;
    	
    	return false;
    }
    
    /** Returns true if the passed biome is a hot biome.  Args: Biome*/
    public static boolean isHotBiome(BiomeGenBase biome) {
    	if (biome == BiomeGenBase.desert)
    		return true;
    	if (biome == BiomeGenBase.desertHills)
    		return true;
    	if (biome == BiomeGenBase.hell)
    		return true;
    	if (biome == BiomeGenBase.jungle)
    		return true;
    	if (biome == BiomeGenBase.jungleHills)
    		return true;
    	
    	return false;
    }
    
    /** Applies temperature effects to the environment. Args: World, x, y, z, temperature */
    public static void temperatureEnvironment(World world, int x, int y, int z, int temperature) {
    	if (temperature < 0) {
    		for (int i = 0; i < 6; i++) {
    			int side = (ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water));
    			if (side != -1)
    				ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.ice.blockID);
    		}
    	}
    	if (temperature > 450)	{ // Wood autoignition
    		for (int i = 0; i < 4; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.wood)
    				ignite(world, x-i, y, z);
    			if (world.getBlockMaterial(x+i, y, z) == Material.wood)
    				ignite(world, x+i, y, z);
    			if (world.getBlockMaterial(x, y-i, z) == Material.wood)
    				ignite(world, x, y-i, z);
    			if (world.getBlockMaterial(x, y+i, z) == Material.wood)
    				ignite(world, x, y+i, z);
    			if (world.getBlockMaterial(x, y, z-i) == Material.wood)
    				ignite(world, x, y, z-i);
    			if (world.getBlockMaterial(x, y, z+i) == Material.wood)
    				ignite(world, x, y, z+i);
    		}
    	}
    	if (temperature > 600)	{ // Wool autoignition
    		for (int i = 0; i < 4; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.cloth)
    				ignite(world, x-i, y, z);
    			if (world.getBlockMaterial(x+i, y, z) == Material.cloth)
    				ignite(world, x+i, y, z);
    			if (world.getBlockMaterial(x, y-i, z) == Material.cloth)
    				ignite(world, x, y-i, z);
    			if (world.getBlockMaterial(x, y+i, z) == Material.cloth)
    				ignite(world, x, y+i, z);
    			if (world.getBlockMaterial(x, y, z-i) == Material.cloth)
    				ignite(world, x, y, z-i);
    			if (world.getBlockMaterial(x, y, z+i) == Material.cloth)
    				ignite(world, x, y, z+i);
    		}
    	}
    	if (temperature > 300)	{ // TNT autoignition
    		for (int i = 0; i < 4; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.tnt)
    				ignite(world, x-i, y, z);
    			if (world.getBlockMaterial(x+i, y, z) == Material.tnt)
    				ignite(world, x+i, y, z);
    			if (world.getBlockMaterial(x, y-i, z) == Material.tnt)
    				ignite(world, x, y-i, z);
    			if (world.getBlockMaterial(x, y+i, z) == Material.tnt)
    				ignite(world, x, y+i, z);
    			if (world.getBlockMaterial(x, y, z-i) == Material.tnt)
    				ignite(world, x, y, z-i);
    			if (world.getBlockMaterial(x, y, z+i) == Material.tnt)
    				ignite(world, x, y, z+i);
    		}
    	}
    	if (temperature > 230)	{ // Grass/leaves/plant autoignition
    		for (int i = 0; i < 4; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.leaves || world.getBlockMaterial(x-i, y, z) == Material.vine || world.getBlockMaterial(x-i, y, z) == Material.plants || world.getBlockMaterial(x-i, y, z) == Material.web)
    				ignite(world, x-i, y, z);
    			if (world.getBlockMaterial(x+i, y, z) == Material.leaves || world.getBlockMaterial(x+i, y, z) == Material.vine || world.getBlockMaterial(x+i, y, z) == Material.plants || world.getBlockMaterial(x+i, y, z) == Material.web)
    				ignite(world, x+i, y, z);
    			if (world.getBlockMaterial(x, y-i, z) == Material.leaves || world.getBlockMaterial(x, y-i, z) == Material.vine || world.getBlockMaterial(x, y-i, z) == Material.plants || world.getBlockMaterial(x, y-i, z) == Material.web)
    				ignite(world, x, y-i, z);
    			if (world.getBlockMaterial(x, y+i, z) == Material.leaves || world.getBlockMaterial(x, y+i, z) == Material.vine || world.getBlockMaterial(x, y+i, z) == Material.plants || world.getBlockMaterial(x, y+i, z) == Material.web)
    				ignite(world, x, y+i, z);
    			if (world.getBlockMaterial(x, y, z-i) == Material.leaves || world.getBlockMaterial(x, y, z-i) == Material.vine || world.getBlockMaterial(x, y, z-i) == Material.plants || world.getBlockMaterial(x, y, z-i) == Material.web)
    				ignite(world, x, y, z-i);
    			if (world.getBlockMaterial(x, y, z+i) == Material.leaves || world.getBlockMaterial(x, y, z+i) == Material.vine || world.getBlockMaterial(x, y, z+i) == Material.plants || world.getBlockMaterial(x, y, z+i) == Material.web)
    				ignite(world, x, y, z+i);
    		}
    	}
    	
    	if (temperature > 0)	{ // Melting snow/ice
    		for (int i = 0; i < 3; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.ice)
    				world.setBlockWithNotify(x-i, y, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x+i, y, z) == Material.ice)
    				world.setBlockWithNotify(x+i, y, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y-i, z) == Material.ice)
    				world.setBlockWithNotify(x, y-i, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y+i, z) == Material.ice)
    				world.setBlockWithNotify(x, y+i, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y, z-i) == Material.ice)
    				world.setBlockWithNotify(x, y, z-i, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y, z+i) == Material.ice)
    				world.setBlockWithNotify(x, y, z+i, Block.waterMoving.blockID);
    		}
    	}
    }
    
    /** Surrounds the block with fire. Args: World, x, y, z */
    public static void ignite(World world, int x, int y, int z) {
    	if (world.getBlockId		(x-1, y, z) == 0)
    		world.setBlockWithNotify(x-1, y, z, Block.fire.blockID);
    	if (world.getBlockId		(x+1, y, z) == 0)
    		world.setBlockWithNotify(x+1, y, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y-1, z) == 0)
    		world.setBlockWithNotify(x, y-1, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y+1, z) == 0)
    		world.setBlockWithNotify(x, y+1, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y, z-1) == 0)
    		world.setBlockWithNotify(x, y, z-1, Block.fire.blockID);
    	if (world.getBlockId		(x, y, z+1) == 0)
    		world.setBlockWithNotify(x, y, z+1, Block.fire.blockID);
    }
    
    /** Returns the number of water blocks directly and continuously above the passed coordinates.
     * Returns -1 if invalid liquid specified. Args: World, x, y, z */
    public static int getDepth(World world, int x, int y, int z, String liq) {
    	int i = 1;
    	if (liq == "water") {
	    	while (world.getBlockId(x, y+i, z) == Block.waterMoving.blockID || world.getBlockId(x, y+i, z) == Block.waterStill.blockID) {
	    		i++;
	    	}
	    	return (i-1);
    	}
    	if (liq == "lava") {
	    	while (world.getBlockId(x, y+i, z) == Block.lavaMoving.blockID || world.getBlockId(x, y+i, z) == Block.lavaStill.blockID) {
	    		i++;
	    	}
	    	return (i-1);
    	}
    	return -1;
    }
    
    /** Returns true if the block ID is one associated with caves, like air, cobwebs,
     * spawners, mushrooms, etc. Args: Block ID */
    public static boolean caveBlock(int id) {
    	if (id == 0 || id == Block.waterMoving.blockID || id == Block.waterStill.blockID || id == Block.lavaMoving.blockID ||
    		id == Block.lavaStill.blockID || id == Block.web.blockID || id == Block.mobSpawner.blockID || id == Block.mushroomRed.blockID ||
    		id == Block.mushroomBrown.blockID)
    		return true;
    	return false;
    }
    
    /** Returns a broad-stroke biome temperature in degrees centigrade.
     * Args: biome */
    public static int getBiomeTemp(BiomeGenBase biome) {
    	int Tamb = 25; //Most biomes = 25C
    	if (ReikaWorldHelper.isSnowBiome(biome))
    		Tamb = -20; //-20C
    	if (ReikaWorldHelper.isHotBiome(biome))
    		Tamb = 40;
    	if (biome == BiomeGenBase.hell)
    		Tamb = 300;	//boils water, so 300C (3 x 100)
    	return Tamb;
    }
    
    /** Returns a broad-stroke biome temperature in degrees centigrade.
     * Args: World, x, z */
    public static int getBiomeTemp(World world, int x, int z) {
    	int Tamb = 25; //Most biomes = 25C
    	BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
    	if (ReikaWorldHelper.isSnowBiome(biome))
    		Tamb = -20; //-20C
    	if (ReikaWorldHelper.isHotBiome(biome))
    		Tamb = 40;
    	if (biome == BiomeGenBase.hell)
    		Tamb = 300;	//boils water, so 300C (3 x 100)
    	return Tamb;
    }
    
    /** Performs machine overheat effects (primarily intended for RotaryCraft).
     * Args: World, x, y, z, item drop id, item drop metadata, min drops, max drops,
     * spark particles yes/no, number-of-sparks multiplier (default 20-40),
     * flaming explosion yes/no, smoking explosion yes/no, explosion force (0 for none) */
    public static void overheat(World world, int x, int y, int z, int id, int meta, int mindrops, int maxdrops, boolean sparks, float sparkmultiplier, boolean flaming, boolean smoke, float force) {
    	if (force > 0) {
	    	if (flaming)
	    		world.newExplosion(null, x, y, z, force, true, smoke);
	    	else
	    		world.createExplosion(null, x, y, z, force, smoke);
    	}
		int numsparks = par5Random.nextInt(20)+20;
		numsparks *= sparkmultiplier;
		if (sparks)
		for (int i = 0; i < numsparks; i++)
			world.spawnParticle("lava", x+par5Random.nextFloat(), y+1, z+par5Random.nextFloat(), 0, 0, 0);
		ItemStack scrap = new ItemStack(id, 1, meta);
		int numdrops = par5Random.nextInt(maxdrops)+mindrops;
		if (!world.isRemote || id <= 0) {
			for (int i = 0; i < numdrops; i++) {
				EntityItem ent = new EntityItem(world, x+par5Random.nextFloat(), y+0.5, z+par5Random.nextFloat(), scrap);
				ent.setVelocity(-0.2+0.4*par5Random.nextFloat(), 0.5*par5Random.nextFloat(), -0.2+0.4*par5Random.nextFloat());
				//world.spawnEntityInWorld(ent);
				ent.velocityChanged = true;
			}
		}
    }
    
    /** Takes a specified amount of XP and splits it randomly among a bunch of orbs. 
     * Args: World, x, y, z, amount */
    public static void splitAndSpawnXP(World world, float x, float y, float z, int xp) {
    	int max = xp/5+1;
    	
    	while (xp > 0) {
	    	int value = par5Random.nextInt(max)+1;
	    	while (value > xp)
	    		value = par5Random.nextInt(max)+1;
	    	xp -= value;
	    	EntityXPOrb orb = new EntityXPOrb(world, x, y, z, value);
	    	orb.setVelocity(-0.2+0.4*par5Random.nextFloat(), 0.3*par5Random.nextFloat(), -0.2+0.4*par5Random.nextFloat());
	    	if (world.isRemote)
	    		return;
	    	orb.velocityChanged = true;
	    	world.spawnEntityInWorld(orb);
    	}
    }
}