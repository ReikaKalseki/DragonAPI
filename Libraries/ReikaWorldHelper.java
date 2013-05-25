package Reika.DragonAPI.Libraries;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class ReikaWorldHelper {

	private ReikaWorldHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

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
	//nonSolidArray[mod_RotaryCraft.canola.blockID] = true;
	//nonSolidArray[mod_RotaryCraft.lightblock.blockID] = true;
	//nonSolidArray[mod_RotaryCraft.lightbridge.blockID] = true;
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
	flammableArray[Block.stairsWoodOak.blockID] = true;
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
    	return y;
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
    			return 255;		//Return top of chunk and exit function
    		if (!waterup && waterdown) // Water lower only
    			;						// the case we want
    		if (!waterup && !waterdown) //Neither water -> above surface
    			y--;
    	}
    	return y;
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
    		legacySetBlockWithNotify(world, x, y+1, z, id);
    	break;
    	case 1:
    		legacySetBlockWithNotify(world, x, y-1, z, id);
    	break;
    	case 2:
    		legacySetBlockWithNotify(world, x+1, y, z, id);
    	break;
    	case 3:
    		legacySetBlockWithNotify(world, x-1, y, z, id);
    	break;
    	case 4:
    		legacySetBlockWithNotify(world, x, y, z+1, id);
    	break;
    	case 5:
    		legacySetBlockWithNotify(world, x, y, z-1, id);
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
    				legacySetBlockWithNotify(world, x-i, y, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x+i, y, z) == Material.ice)
    				legacySetBlockWithNotify(world, x+i, y, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y-i, z) == Material.ice)
    				legacySetBlockWithNotify(world, x, y-i, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y+i, z) == Material.ice)
    				legacySetBlockWithNotify(world, x, y+i, z, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y, z-i) == Material.ice)
    				legacySetBlockWithNotify(world, x, y, z-i, Block.waterMoving.blockID);
    			if (world.getBlockMaterial(x, y, z+i) == Material.ice)
    				legacySetBlockWithNotify(world, x, y, z+i, Block.waterMoving.blockID);
    		}
    	}
    	if (temperature > 0)	{ // Melting snow/ice
    		for (int i = 0; i < 3; i++) {
    			if (world.getBlockMaterial(x-i, y, z) == Material.snow)
    				legacySetBlockWithNotify(world, x-i, y, z, 0);
    			if (world.getBlockMaterial(x+i, y, z) == Material.snow)
    				legacySetBlockWithNotify(world, x+i, y, z, 0);
    			if (world.getBlockMaterial(x, y-i, z) == Material.snow)
    				legacySetBlockWithNotify(world, x, y-i, z, 0);
    			if (world.getBlockMaterial(x, y+i, z) == Material.snow)
    				legacySetBlockWithNotify(world, x, y+i, z, 0);
    			if (world.getBlockMaterial(x, y, z-i) == Material.snow)
    				legacySetBlockWithNotify(world, x, y, z-i, 0);
    			if (world.getBlockMaterial(x, y, z+i) == Material.snow)
    				legacySetBlockWithNotify(world, x, y, z+i, 0);

    			if (world.getBlockMaterial(x-i, y, z) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x-i, y, z, 0);
    			if (world.getBlockMaterial(x+i, y, z) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x+i, y, z, 0);
    			if (world.getBlockMaterial(x, y-i, z) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x, y-i, z, 0);
    			if (world.getBlockMaterial(x, y+i, z) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x, y+i, z, 0);
    			if (world.getBlockMaterial(x, y, z-i) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x, y, z-i, 0);
    			if (world.getBlockMaterial(x, y, z+i) == Material.craftedSnow)
    				legacySetBlockWithNotify(world, x, y, z+i, 0);
    		}
    	}
    }

    /** Surrounds the block with fire. Args: World, x, y, z */
    public static void ignite(World world, int x, int y, int z) {
    	if (world.getBlockId		(x-1, y, z) == 0)
    		legacySetBlockWithNotify(world, x-1, y, z, Block.fire.blockID);
    	if (world.getBlockId		(x+1, y, z) == 0)
    		legacySetBlockWithNotify(world, x+1, y, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y-1, z) == 0)
    		legacySetBlockWithNotify(world, x, y-1, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y+1, z) == 0)
    		legacySetBlockWithNotify(world, x, y+1, z, Block.fire.blockID);
    	if (world.getBlockId		(x, y, z-1) == 0)
    		legacySetBlockWithNotify(world, x, y, z-1, Block.fire.blockID);
    	if (world.getBlockId		(x, y, z+1) == 0)
    		legacySetBlockWithNotify(world, x, y, z+1, Block.fire.blockID);
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
    	if (force > 0 && !world.isRemote) {
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

    /** Returns true if the coordinate specified is a lava source block and would be recreated according to the lava-duplication rules
     * that existed for a short time in Beta 1.9. Args: World, x, y, z */
    public static boolean is1p9InfiniteLava(World world, int x, int y, int z) {
    	if (world.getBlockMaterial(x, y, z) != Material.lava || world.getBlockMetadata(x, y, z) != 0)
    		return false;
    	if (world.getBlockMaterial(x+1, y, z) != Material.lava || world.getBlockMetadata(x+1, y, z) != 0)
    		return false;
    	if (world.getBlockMaterial(x, y, z+1) != Material.lava || world.getBlockMetadata(x, y, z+1) != 0)
    		return false;
    	if (world.getBlockMaterial(x-1, y, z) != Material.lava || world.getBlockMetadata(x-1, y, z) != 0)
    		return false;
    	if (world.getBlockMaterial(x, y, z-1) != Material.lava || world.getBlockMetadata(x, y, z-1) != 0)
    		return false;
    	return true;
    }

    /** Returns the y-coordinate of the top non-air block at the given xz coordinates, at or
     * below the specified y-coordinate. Returns -1 if none. Args: World, x, z, y */
    public static int findTopBlockBelowY(World world, int x, int z, int y) {
    	int id = world.getBlockId(x, y, z);
    	while ((id == 0) && y >= 0) {
    		y--;
    		id = world.getBlockId(x, y, z);
    	}
    	return y;
    }

    /** Returns true if the coordinate is a liquid source block. Args: World, x, y, z */
    public static boolean isLiquidSourceBlock(World world, int x, int y, int z) {
    	if (world.getBlockMetadata(x, y, z) != 0)
    		return false;
    	if (world.getBlockMaterial(x, y, z) != Material.lava && world.getBlockMaterial(x, y, z) != Material.water)
    		return false;
    	return true;
    }

    /** Returns true if the Block ID corresponds to an ore block. Args: ID */
    public static boolean isOre(int id) {
    	if (id == Block.oreCoal.blockID)
    		return true;
    	if (id == Block.oreIron.blockID)
    		return true;
    	if (id == Block.oreGold.blockID)
    		return true;
    	if (id == Block.oreRedstone.blockID)
    		return true;
    	if (id == Block.oreLapis.blockID)
    		return true;
    	if (id == Block.oreDiamond.blockID)
    		return true;
    	if (id == Block.oreEmerald.blockID)
    		return true;
    	if (id == Block.oreRedstoneGlowing.blockID)
    		return true;
    	if (id == Block.oreNetherQuartz.blockID)
    		return true;
    	return false;
    }

    /** Breaks a contiguous area of blocks recursively (akin to a fill tool in image editors).
     * Args: World, start x, start y, start z, id, metadata (-1 for any) */
    public static void recursiveBreak(World world, int x, int y, int z, int id, int meta) {
    	if (id == 0)
    		return;
    	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	Block.blocksList[id].dropBlockAsItem(world, x, y, z, id, metad);
    	legacySetBlockWithNotify(world, x, y, z, 0);
    	world.markBlockForUpdate(x, y, z);
    	recursiveBreak(world, x+1, y, z, id, meta);
    	recursiveBreak(world, x-1, y, z, id, meta);
    	recursiveBreak(world, x, y+1, z, id, meta);
    	recursiveBreak(world, x, y-1, z, id, meta);
    	recursiveBreak(world, x, y, z+1, id, meta);
    	recursiveBreak(world, x, y, z-1, id, meta);
    }

    /** Like the ordinary recursive break but with a spherical bounded volume. Args: World, x, y, z,
     * id to replace, metadata to replace (-1 for any), origin x,y,z, max radius */
    public static void recursiveBreakWithinSphere(World world, int x, int y, int z, int id, int meta, int x0, int y0, int z0, double r) {
    	if (id == 0)
    		return;
    	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	Block.blocksList[id].dropBlockAsItem(world, x, y, z, id, metad);
    	legacySetBlockWithNotify(world, x, y, z, 0);
    	world.markBlockForUpdate(x, y, z);
    	recursiveBreakWithinSphere(world, x+1, y, z, id, meta, x0, y0, z0, r);
    	recursiveBreakWithinSphere(world, x-1, y, z, id, meta, x0, y0, z0, r);
    	recursiveBreakWithinSphere(world, x, y+1, z, id, meta, x0, y0, z0, r);
    	recursiveBreakWithinSphere(world, x, y-1, z, id, meta, x0, y0, z0, r);
    	recursiveBreakWithinSphere(world, x, y, z+1, id, meta, x0, y0, z0, r);
    	recursiveBreakWithinSphere(world, x, y, z-1, id, meta, x0, y0, z0, r);
    }

    /** Like the ordinary recursive break but with a bounded volume. Args: World, x, y, z,
     * id to replace, metadata to replace (-1 for any), min x,y,z, max x,y,z */
    public static void recursiveBreakWithBounds(World world, int x, int y, int z, int id, int meta, int x1, int y1, int z1, int x2, int y2, int z2) {
    	if (id == 0)
    		return;
    	if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
    		return;
    	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	Block.blocksList[id].dropBlockAsItem(world, x, y, z, id, metad);
    	legacySetBlockWithNotify(world, x, y, z, 0);
    	world.markBlockForUpdate(x, y, z);
    	recursiveBreakWithBounds(world, x+1, y, z, id, meta, x1, y1, z1, x2, y2, z2);
    	recursiveBreakWithBounds(world, x-1, y, z, id, meta, x1, y1, z1, x2, y2, z2);
    	recursiveBreakWithBounds(world, x, y+1, z, id, meta, x1, y1, z1, x2, y2, z2);
    	recursiveBreakWithBounds(world, x, y-1, z, id, meta, x1, y1, z1, x2, y2, z2);
    	recursiveBreakWithBounds(world, x, y, z+1, id, meta, x1, y1, z1, x2, y2, z2);
    	recursiveBreakWithBounds(world, x, y, z-1, id, meta, x1, y1, z1, x2, y2, z2);
    }

    /** Recursively fills a contiguous area of one block type with another, akin to a fill tool.
     * Args: World, start x, start y, start z, id to replace, id to fill with,
     * metadata to replace (-1 for any), metadata to fill with */
    public static void recursiveFill(World world, int x, int y, int z, int id, int idto, int meta, int metato) {
     	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	legacySetBlockAndMetadataWithNotify(world, x, y, z, idto, metato);
    	world.markBlockForUpdate(x, y, z);
    	recursiveFill(world, x+1, y, z, id, idto, meta, metato);
    	recursiveFill(world, x-1, y, z, id, idto, meta, metato);
    	recursiveFill(world, x, y+1, z, id, idto, meta, metato);
    	recursiveFill(world, x, y-1, z, id, idto, meta, metato);
    	recursiveFill(world, x, y, z+1, id, idto, meta, metato);
    	recursiveFill(world, x, y, z-1, id, idto, meta, metato);
    }

    /** Like the ordinary recursive fill but with a bounded volume. Args: World, x, y, z,
     * id to replace, id to fill with, metadata to replace (-1 for any),
     * metadata to fill with, min x,y,z, max x,y,z */
    public static void recursiveFillWithBounds(World world, int x, int y, int z, int id, int idto, int meta, int metato, int x1, int y1, int z1, int x2, int y2, int z2) {
    	if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
    		return;
    	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	legacySetBlockAndMetadataWithNotify(world, x, y, z, idto, metato);
    	world.markBlockForUpdate(x, y, z);
    	recursiveFillWithBounds(world, x+1, y, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    	recursiveFillWithBounds(world, x-1, y, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    	recursiveFillWithBounds(world, x, y+1, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    	recursiveFillWithBounds(world, x, y-1, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    	recursiveFillWithBounds(world, x, y, z+1, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    	recursiveFillWithBounds(world, x, y, z-1, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
    }

    /** Like the ordinary recursive fill but with a spherical bounded volume. Args: World, x, y, z,
     * id to replace, id to fill with, metadata to replace (-1 for any),
     * metadata to fill with, origin x,y,z, max radius */
    public static void recursiveFillWithinSphere(World world, int x, int y, int z, int id, int idto, int meta, int metato, int x0, int y0, int z0, double r) {
    	//ReikaGuiAPI.write(world.getBlockId(x, y, z)+" & "+id+" @ "+x0+", "+y0+", "+z0);
    	if (world.getBlockId(x, y, z) != id)
    		return;
    	if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
    		return;
    	if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
    		return;
    	int metad = world.getBlockMetadata(x, y, z);
    	legacySetBlockAndMetadataWithNotify(world, x, y, z, idto, metato);
    	world.markBlockForUpdate(x, y, z);
    	recursiveFillWithinSphere(world, x+1, y, z, id, idto, meta, metato, x0, y0, z0, r);
    	recursiveFillWithinSphere(world, x-1, y, z, id, idto, meta, metato, x0, y0, z0, r);
    	recursiveFillWithinSphere(world, x, y+1, z, id, idto, meta, metato, x0, y0, z0, r);
    	recursiveFillWithinSphere(world, x, y-1, z, id, idto, meta, metato, x0, y0, z0, r);
    	recursiveFillWithinSphere(world, x, y, z+1, id, idto, meta, metato, x0, y0, z0, r);
    	recursiveFillWithinSphere(world, x, y, z-1, id, idto, meta, metato, x0, y0, z0, r);
    }

    /** Returns true if there is a clear line of sight between two points. Args: World, Start x,y,z, End x,y,z
     * NOTE: If one point is a block, use canBlockSee instead, as this method will always return false. */
    public static boolean lineOfSight(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
    	if (world.isRemote)
    		return false;
    	Vec3 v1 = Vec3.fakePool.getVecFromPool(x1, y1, z1);
    	Vec3 v2 = Vec3.fakePool.getVecFromPool(x2, y2, z2);
    	return (world.rayTraceBlocks(v1, v2) == null);
    }

    /** Returns true if there is a clear line of sight between two entites. Args: World, Entity 1, Entity 2 */
    public static boolean lineOfSight(World world, Entity e1, Entity e2) {
    	if (world.isRemote)
    		return false;
    	Vec3 v1 = Vec3.fakePool.getVecFromPool(e1.posX, e1.posY+e1.getEyeHeight(), e1.posZ);
    	Vec3 v2 = Vec3.fakePool.getVecFromPool(e2.posX, e2.posY+e2.getEyeHeight(), e2.posZ);
    	return (world.rayTraceBlocks(v1, v2) == null);
    }

    /** Returns true if a block can see an point. Args: World, block x,y,z, Point x,y,z, Max Range */
    public static boolean canBlockSee(World world, int x, int y, int z, double x0, double y0, double z0, double range) {
    	int locid = world.getBlockId(x, y, z);
    	range += 2;
    	for (int k = 0; k < 10; k++) {
    		float a = 0; float b = 0; float c = 0;
    		switch(k) {
    		case 1:
    			a = 1;
    		break;
    		case 2:
    			b = 1;
    		break;
    		case 3:
    			a = 1;
    			b = 1;
    		break;
    		case 4:
    			c = 1;
    		break;
    		case 5:
    			a = 1;
    			c = 1;
    		break;
    		case 6:
    			b = 1;
    			c = 1;
    		break;
    		case 7:
    			a = 1;
    			b = 1;
    			c = 1;
    		break;
    		case 8:
    			a = 0.5F;
    			b = 0.5F;
    			c = 0.5F;
    		break;
    		case 9:
    			b = 0.5F;
    		break;
    		}
	    	for (float i = 0; i <= range; i += 0.25) {
		    	Vec3 vec2 = ReikaVectorHelper.getVec2Pt(x+a, y+b, z+c, x0, y0, z0).normalize();
		    	vec2.xCoord *= i;
		    	vec2.yCoord *= i;
		    	vec2.zCoord *= i;
		    	vec2.xCoord += x0;
		    	vec2.yCoord += y0;
		    	vec2.zCoord += z0;
		    	//ReikaGuiAPI.write(String.format("%f -->  %.3f,  %.3f, %.3f", i, vec2.xCoord, vec2.yCoord, vec2.zCoord));
		    	int id = world.getBlockId((int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord);
		    	if ((int)vec2.xCoord == x && (int)vec2.yCoord == y && (int)vec2.zCoord == z) {
		    		//ReikaGuiAPI.writeCoords(world, (int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord);
		    		return true;
		    	}
		    	else if (id != 0 && id != locid && (isCollideable(world, (int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord) && !softBlocks(id))) {
		    		i = (float)(range + 1); //Hard loop break
		    	}
	    	}
    	}
    	return false;
    }

    /** Returns true if the entity can see a block, or if it could be moved to a position where it could see the block.
     * Args: World, Block x,y,z, Entity, Max Move Distance
     * DO NOT USE THIS - CPU INTENSIVE TO ALL HELL! */
    public static boolean canSeeOrMoveToSeeBlock(World world, int x, int y, int z, Entity ent, double r) {
    	double d = 4;//+ReikaMathLibrary.py3d(x-ent.posX, y-ent.posY, z-ent.posZ);
    	if (canBlockSee(world, x, y, z, ent.posX, ent.posY, ent.posZ, d))
    		return true;
    	double xmin; double ymin; double zmin;
    	double xmax; double ymax; double zmax;
    	double[] pos = new double[3];
    	boolean[] signs = new boolean[3];
    	boolean[] signs2 = new boolean[3];
    	signs[0] = (ReikaMathLibrary.isSameSign(ent.posX, x));
    	signs[1] = (ReikaMathLibrary.isSameSign(ent.posY, y));
    	signs[2] = (ReikaMathLibrary.isSameSign(ent.posZ, z));
    	for (double i = ent.posX-r; i <= ent.posX+r; i += 0.5) {
        	for (double j = ent.posY-r; j <= ent.posY+r; j += 0.5) {
            	for (double k = ent.posZ-r; k <= ent.posZ+r; k += 0.5) {
            		if (canBlockSee(world, x, y, z, ent.posX+i, ent.posY+j, ent.posZ+k, d))
            			return true;
            	}
        	}
    	}
    	/*
    	for (double i = ent.posX; i > ent.posX-r; i -= 0.5) {
    		int id = world.getBlockId((int)i, (int)ent.posY, (int)ent.posZ);
    		if (isCollideable(world, (int)i, (int)ent.posY, (int)ent.posZ)) {
    			xmin = i+Block.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posX; i < ent.posX+r; i += 0.5) {
    		int id = world.getBlockId((int)i, (int)ent.posY, (int)ent.posZ);
    		if (isCollideable(world, (int)i, (int)ent.posY, (int)ent.posZ)) {
    			xmax = i+Block.blocksList[id].getBlockBoundsMinX();
    		}
    	}
    	for (double i = ent.posY; i > ent.posY-r; i -= 0.5) {
    		int id = world.getBlockId((int)ent.posX, (int)i, (int)ent.posZ);
    		if (isCollideable(world, (int)ent.posX, (int)i, (int)ent.posZ)) {
    			ymin = i+Block.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posY; i < ent.posY+r; i += 0.5) {
    		int id = world.getBlockId((int)ent.posX, (int)i, (int)ent.posZ);
    		if (isCollideable(world, (int)ent.posX, (int)i, (int)ent.posZ)) {
    			ymax = i+Block.blocksList[id].getBlockBoundsMinX();
    		}
    	}
    	for (double i = ent.posZ; i > ent.posZ-r; i -= 0.5) {
    		int id = world.getBlockId((int)ent.posX, (int)ent.posY, (int)i);
    		if (isCollideable(world, (int)ent.posX, (int)ent.posY, (int)i)) {
    			zmin = i+Block.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posZ; i < ent.posZ+r; i += 0.5) {
    		int id = world.getBlockId((int)ent.posX, (int)ent.posY, (int)i);
    		if (isCollideable(world, (int)ent.posX, (int)ent.posY, (int)i)) {
    			zmax = i+Block.blocksList[id].getBlockBoundsMinX();
    		}
    	}*/
    	signs2[0] = (ReikaMathLibrary.isSameSign(pos[0], x));
    	signs2[1] = (ReikaMathLibrary.isSameSign(pos[1], y));
    	signs2[2] = (ReikaMathLibrary.isSameSign(pos[2], z));
    	if (signs[0] != signs2[0] || signs[1] != signs2[1] || signs[2] != signs2[2]) //Cannot pull the item "Across" (so that it moves away)
    		return false;
    	return false;
    }

    public static boolean lenientSeeThrough(World world, double x, double y, double z, double x0, double y0, double z0) {
    	MovingObjectPosition pos;
    	Vec3 par1Vec3 = Vec3.fakePool.getVecFromPool(x, y, z);
    	Vec3 par2Vec3 = Vec3.fakePool.getVecFromPool(x0, y0, z0);
        if (!Double.isNaN(par1Vec3.xCoord) && !Double.isNaN(par1Vec3.yCoord) && !Double.isNaN(par1Vec3.zCoord)) {
            if (!Double.isNaN(par2Vec3.xCoord) && !Double.isNaN(par2Vec3.yCoord) && !Double.isNaN(par2Vec3.zCoord)) {
                int var5 = MathHelper.floor_double(par2Vec3.xCoord);
                int var6 = MathHelper.floor_double(par2Vec3.yCoord);
                int var7 = MathHelper.floor_double(par2Vec3.zCoord);
                int var8 = MathHelper.floor_double(par1Vec3.xCoord);
                int var9 = MathHelper.floor_double(par1Vec3.yCoord);
                int var10 = MathHelper.floor_double(par1Vec3.zCoord);
                int var11 = world.getBlockId(var8, var9, var10);
                int var12 = world.getBlockMetadata(var8, var9, var10);
                Block var13 = Block.blocksList[var11];
                //ReikaGuiAPI.write(var11);
                if (var13 != null && (var11 > 0 && !ReikaWorldHelper.softBlocks(var11) && (var11 != Block.leaves.blockID) && (var11 != Block.web.blockID)) && var13.canCollideCheck(var12, false)) {
                    MovingObjectPosition var14 = var13.collisionRayTrace(world, var8, var9, var10, par1Vec3, par2Vec3);
                    if (var14 != null)
                        pos = var14;
                }
                var11 = 200;
                while (var11-- >= 0) {
                    if (Double.isNaN(par1Vec3.xCoord) || Double.isNaN(par1Vec3.yCoord) || Double.isNaN(par1Vec3.zCoord))
                        pos = null;
                    if (var8 == var5 && var9 == var6 && var10 == var7)
                        pos = null;
                    boolean var39 = true;
                    boolean var40 = true;
                    boolean var41 = true;
                    double var15 = 999.0D;
                    double var17 = 999.0D;
                    double var19 = 999.0D;
                    if (var5 > var8)
                        var15 = var8 + 1.0D;
                    else if (var5 < var8)
                        var15 = var8 + 0.0D;
                    else
                        var39 = false;
                    if (var6 > var9)
                        var17 = var9 + 1.0D;
                    else if (var6 < var9)
                        var17 = var9 + 0.0D;
                    else
                        var40 = false;
                    if (var7 > var10)
                        var19 = var10 + 1.0D;
                    else if (var7 < var10)
                        var19 = var10 + 0.0D;
                    else
                        var41 = false;
                    double var21 = 999.0D;
                    double var23 = 999.0D;
                    double var25 = 999.0D;
                    double var27 = par2Vec3.xCoord - par1Vec3.xCoord;
                    double var29 = par2Vec3.yCoord - par1Vec3.yCoord;
                    double var31 = par2Vec3.zCoord - par1Vec3.zCoord;
                    if (var39)
                        var21 = (var15 - par1Vec3.xCoord) / var27;
                    if (var40)
                        var23 = (var17 - par1Vec3.yCoord) / var29;
                    if (var41)
                        var25 = (var19 - par1Vec3.zCoord) / var31;
                    boolean var33 = false;
                    byte var42;
                    if (var21 < var23 && var21 < var25) {
                        if (var5 > var8)
                            var42 = 4;
                        else
                            var42 = 5;
                        par1Vec3.xCoord = var15;
                        par1Vec3.yCoord += var29 * var21;
                        par1Vec3.zCoord += var31 * var21;
                    }
                    else if (var23 < var25) {
                        if (var6 > var9)
                            var42 = 0;
                        else
                            var42 = 1;
                        par1Vec3.xCoord += var27 * var23;
                        par1Vec3.yCoord = var17;
                        par1Vec3.zCoord += var31 * var23;
                    }
                    else {
                        if (var7 > var10)
                            var42 = 2;
                        else
                            var42 = 3;

                        par1Vec3.xCoord += var27 * var25;
                        par1Vec3.yCoord += var29 * var25;
                        par1Vec3.zCoord = var19;
                    }
                    Vec3 var34 = world.getWorldVec3Pool().getVecFromPool(par1Vec3.xCoord, par1Vec3.yCoord, par1Vec3.zCoord);
                    var8 = (int)(var34.xCoord = MathHelper.floor_double(par1Vec3.xCoord));
                    if (var42 == 5) {
                        --var8;
                        ++var34.xCoord;
                    }
                    var9 = (int)(var34.yCoord = MathHelper.floor_double(par1Vec3.yCoord));
                    if (var42 == 1) {
                        --var9;
                        ++var34.yCoord;
                    }
                    var10 = (int)(var34.zCoord = MathHelper.floor_double(par1Vec3.zCoord));
                    if (var42 == 3) {
                        --var10;
                        ++var34.zCoord;
                    }
                    int var35 = world.getBlockId(var8, var9, var10);
                    int var36 = world.getBlockMetadata(var8, var9, var10);
                    Block var37 = Block.blocksList[var35];
                    if (var35 > 0 && var37.canCollideCheck(var36, false)) {
                        MovingObjectPosition var38 = var37.collisionRayTrace(world, var8, var9, var10, par1Vec3, par2Vec3);
                        if (var38 != null)
                            pos = var38;
                    }
                }
                pos = null;
            }
            else
                pos = null;
        }
        else
            pos = null;
        return (pos == null);
    }

    /** Returns true if the block has a hitbox. Args: World, x, y, z */
    public static boolean isCollideable(World world, int x, int y, int z) {
    	if (world.getBlockId(x, y, z) == 0)
    		return false;
    	Block b = Block.blocksList[world.getBlockId(x, y, z)];
    	return (b.getCollisionBoundingBoxFromPool(world, x, y, z) != null);
    }

	public static boolean legacySetBlockMetadataWithNotify(World world, int x, int y, int z, int meta) {
		return world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	public static boolean legacySetBlockAndMetadataWithNotify(World world, int x, int y, int z, int id, int meta) {
		return world.setBlock(x, y, z, id, meta, 3);
	}

	public static boolean legacySetBlockWithNotify(World world, int x, int y, int z, int id) {
		return world.setBlock(x, y, z, id, 0, 3);
	}

	/** Returns true if the specified corner has at least one air block adjacent to it,
	 * but is not surrounded by air on all sides or in the void. Args: World, x, y, z */
	public static boolean cornerHasAirAdjacent(World world, int x, int y, int z) {
		if (y <= 0)
			return false;
		int airs = 0;
		if (world.getBlockId(x, y, z) == 0)
			airs++;
		if (world.getBlockId(x-1, y, z) == 0)
			airs++;
		if (world.getBlockId(x, y, z-1) == 0)
			airs++;
		if (world.getBlockId(x-1, y, z-1) == 0)
			airs++;
		if (world.getBlockId(x, y-1, z) == 0)
			airs++;
		if (world.getBlockId(x-1, y-1, z) == 0)
			airs++;
		if (world.getBlockId(x, y-1, z-1) == 0)
			airs++;
		if (world.getBlockId(x-1, y-1, z-1) == 0)
			airs++;
		return (airs > 0 && airs != 8);
	}

	/** Returns true if the specified corner has at least one nonopaque block adjacent to it,
	 * but is not surrounded by air on all sides or in the void. Args: World, x, y, z */
	public static boolean cornerHasTransAdjacent(World world, int x, int y, int z) {
		if (y <= 0)
			return false;
		int id;
		int airs = 0;
		boolean nonopq = false;
		id = world.getBlockId(x, y, z);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x-1, y, z);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x, y, z-1);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x-1, y, z-1);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x, y-1, z);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x-1, y-1, z);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x, y-1, z-1);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		id = world.getBlockId(x-1, y-1, z-1);
		if (id == 0)
			airs++;
		else if (!Block.blocksList[id].isOpaqueCube())
			nonopq = true;
		return (airs != 8 && nonopq);
	}

	/** Spills the entire inventory of an ItemStack[] at the specified coordinates with a 1-block spread.
	 * Args: World, x, y, z, inventory */
	public static void spillAndEmptyInventory(World world, int x, int y, int z, ItemStack[] inventory) {
		EntityItem ei;
		ItemStack is;
		for (int i = 0; i < inventory.length; i++) {
			is = inventory[i];
			inventory[i] = null;
			if (is != null && !world.isRemote) {
				ei = new EntityItem(world, x+par5Random.nextFloat(), y+par5Random.nextFloat(), z+par5Random.nextFloat(), is);
				ReikaEntityHelper.addRandomDirVelocity(ei, 0.2);
				world.spawnEntityInWorld(ei);
			}
		}
	}

	/** Spills the entire inventory of an ItemStack[] at the specified coordinates with a 1-block spread.
	 * Args: World, x, y, z, IInventory */
	public static void spillAndEmptyInventory(World world, int x, int y, int z, IInventory ii) {
		int size = ii.getSizeInventory();
		for (int i = 0; i < size; i++) {
			ItemStack s = ii.getStackInSlot(i);
			if (s != null) {
				ii.setInventorySlotContents(i, null);
				EntityItem ei = new EntityItem(world, x+par5Random.nextFloat(), y+par5Random.nextFloat(), z+par5Random.nextFloat(), s);
				ReikaEntityHelper.addRandomDirVelocity(ei, 0.2);
				ei.delayBeforeCanPickup = 10;
				if (!world.isRemote)
					world.spawnEntityInWorld(ei);
			}
		}
	}

	/** Spawns a line of particles between two points. Args: World, start x,y,z, end x,y,z, particle type, particle speed x,y,z, number of particles */
	public static void spawnParticleLine(World world, double x1, double y1, double z1, double x2, double y2, double z2, String name, double vx, double vy, double vz, int spacing) {
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double sx = dx/spacing;
		double sy = dy/spacing;
		double sz = dy/spacing;
		double[][] parts = new double[spacing+1][3];
		for (int i = 0; i <= spacing; i++) {
			parts[i][0] = i*sx+x1;
			parts[i][1] = i*sy+y1;
			parts[i][2] = i*sz+z1;
		}
		for (int i = 0; i < parts.length; i++) {
			world.spawnParticle(name, parts[i][0], parts[i][1], parts[i][2], vx, vy, vz);
		}
	}

	/** Checks if a liquid block is part of a column (has same liquid above and below and none of them are source blocks).
	 * Args: World, x, y, z */
	public static boolean isLiquidAColumn(World world, int x, int y, int z) {
		Material mat = world.getBlockMaterial(x, y, z);
		if (isLiquidSourceBlock(world, x, y, z))
			return false;
		if (world.getBlockMaterial(x, y+1, z) != mat)
			return false;
		if (isLiquidSourceBlock(world, x, y+1, z))
			return false;
		if (world.getBlockMaterial(x, y-1, z) != mat)
			return false;
		if (isLiquidSourceBlock(world, x, y-1, z))
			return false;
		return true;
	}

	public static void causeAdjacentUpdates(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		world.notifyBlocksOfNeighborChange(x, y, z, id);
	}
}