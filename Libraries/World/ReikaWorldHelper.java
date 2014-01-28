/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.BlockProperties;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;

public final class ReikaWorldHelper extends DragonAPICore {

	public static boolean softBlocks(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == 0)
			return true;
		if (id == 36)
			return false;
		Block b = Block.blocksList[id];
		if (b instanceof BlockFluid)
			return true;
		if (b.isBlockReplaceable(world, x, y, z))
			return true;
		if (b.isAirBlock(world, x, y, z))
			return true;
		if (id == Block.vine.blockID)
			return true;
		return (BlockProperties.softBlocksArray[id]);
	}

	public static boolean softBlocks(int id) {
		if (id == 0)
			return true;
		return (BlockProperties.softBlocksArray[id]);
	}

	public static boolean flammable(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == 0)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		Block b = Block.blocksList[id];
		if (b.getFlammability(world, x, y, z, meta, ForgeDirection.UP) > 0)
			return true;
		return (BlockProperties.flammableArray[id]);
	}

	public static boolean flammable(int id) {
		if (id == 0)
			return false;
		return (BlockProperties.flammableArray[id]);
	}

	public static boolean nonSolidBlocks(int id) {
		return (BlockProperties.nonSolidArray[id]);
	}

	/** Converts the given coordinates to an RGB representation of those coordinates' biome's color, for the given material type.
	 * Args: World, x, z, material (String) */
	public static int[] biomeToRGB(World world, int x, int z, String material) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = ReikaWorldHelper.biomeToHex(biome, material);
		return ReikaColorAPI.HexToRGB(color);
	}

	public static int[] biomeToRGB(IBlockAccess world, int x, int z, String material) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = ReikaWorldHelper.biomeToHex(biome, material);
		return ReikaColorAPI.HexToRGB(color);
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

	/** Finds the top edge of the top water block in the column. Args: World, this.x,y,z.
	 * DO NOT CALL if there is no water there, as there is a possibility of infinite loop. */
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
				return y+3;		//Return and exit function
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
	 * Returns -1 if not found. Args: World, x,y,z, id to search. */
	public static ForgeDirection checkForAdjBlock(World world, int x, int y, int z, int id) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				int id2 = world.getBlockId(dx, dy, dz);
				if (id == id2)
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a block of the specified material was found.
	 * Returns -1 if not found. Args: World, x,y,z, material to search. */
	public static ForgeDirection checkForAdjMaterial(World world, int x, int y, int z, Material mat) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Material mat2 = world.getBlockMaterial(dx, dy, dz);
				if (mat == mat2)
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a source block of the specified liquid was found.
	 * Returns -1 if not found. Args: World, x,y,z, material (water/lava) to search. */
	public static ForgeDirection checkForAdjSourceBlock(World world, int x, int y, int z, Material mat) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Material mat2 = world.getBlockMaterial(dx, dy, dz);
				if (mat == mat2 && world.getBlockMetadata(dx, dy, dz) == 0)
					return dir;
			}
		}
		return null;
	}

	/** Edits a block adjacent to the passed arguments, on the specified side.
	 * Args: World, x, y, z, side, id to change to, metadata to change to */
	public static void changeAdjBlock(World world, int x, int y, int z, ForgeDirection side, int id, int meta) {
		int dx = x+side.offsetX;
		int dy = y+side.offsetY;
		int dz = z+side.offsetZ;
		if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
			world.setBlock(dx, dy, dz, id, meta, 3);
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
		if (biome.getEnableSnow())
			return true;
		if (biome.biomeName.toLowerCase().contains("arctic"))
			return true;
		if (biome.biomeName.toLowerCase().contains("tundra"))
			return true;
		if (biome.biomeName.toLowerCase().contains("alpine"))
			return true;
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.FROZEN)
				return true;
		}
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
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.WASTELAND)
				return true;
			if (types[i] == BiomeDictionary.Type.DESERT)
				return true;
			if (types[i] == BiomeDictionary.Type.JUNGLE)
				return true;
		}
		return false;
	}

	/** Applies temperature effects to the environment. Args: World, x, y, z, temperature */
	public static void temperatureEnvironment(World world, int x, int y, int z, int temperature) {
		if (temperature < 0) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection side = (ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.water));
				if (side != null)
					ReikaWorldHelper.changeAdjBlock(world, x, y, z, side, Block.ice.blockID, 0);
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
				if (flammable(world, x-i, y, z))
					if (world.getBlockMaterial(x-i, y, z) == Material.leaves || world.getBlockMaterial(x-i, y, z) == Material.vine || world.getBlockMaterial(x-i, y, z) == Material.plants || world.getBlockMaterial(x-i, y, z) == Material.web)
						ignite(world, x-i, y, z);
				if (flammable(world, x+i, y, z))
					if (world.getBlockMaterial(x+i, y, z) == Material.leaves || world.getBlockMaterial(x+i, y, z) == Material.vine || world.getBlockMaterial(x+i, y, z) == Material.plants || world.getBlockMaterial(x+i, y, z) == Material.web)
						ignite(world, x+i, y, z);
				if (flammable(world, x, y-i, z))
					if (world.getBlockMaterial(x, y-i, z) == Material.leaves || world.getBlockMaterial(x, y-i, z) == Material.vine || world.getBlockMaterial(x, y-i, z) == Material.plants || world.getBlockMaterial(x, y-i, z) == Material.web)
						ignite(world, x, y-i, z);
				if (flammable(world, x, y+i, z))
					if (world.getBlockMaterial(x, y+i, z) == Material.leaves || world.getBlockMaterial(x, y+i, z) == Material.vine || world.getBlockMaterial(x, y+i, z) == Material.plants || world.getBlockMaterial(x, y+i, z) == Material.web)
						ignite(world, x, y+i, z);
				if (flammable(world, x, y, z-i))
					if (world.getBlockMaterial(x, y, z-i) == Material.leaves || world.getBlockMaterial(x, y, z-i) == Material.vine || world.getBlockMaterial(x, y, z-i) == Material.plants || world.getBlockMaterial(x, y, z-i) == Material.web)
						ignite(world, x, y, z-i);
				if (flammable(world, x, y, z+i))
					if (world.getBlockMaterial(x, y, z+i) == Material.leaves || world.getBlockMaterial(x, y, z+i) == Material.vine || world.getBlockMaterial(x, y, z+i) == Material.plants || world.getBlockMaterial(x, y, z+i) == Material.web)
						ignite(world, x, y, z+i);
			}
		}

		if (temperature > 0)	{ // Melting snow/ice
			for (int i = 0; i < 3; i++) {
				if (world.getBlockMaterial(x-i, y, z) == Material.ice)
					world.setBlock(x-i, y, z, Block.waterMoving.blockID);
				if (world.getBlockMaterial(x+i, y, z) == Material.ice)
					world.setBlock(x+i, y, z, Block.waterMoving.blockID);
				if (world.getBlockMaterial(x, y-i, z) == Material.ice)
					world.setBlock(x, y-i, z, Block.waterMoving.blockID);
				if (world.getBlockMaterial(x, y+i, z) == Material.ice)
					world.setBlock(x, y+i, z, Block.waterMoving.blockID);
				if (world.getBlockMaterial(x, y, z-i) == Material.ice)
					world.setBlock(x, y, z-i, Block.waterMoving.blockID);
				if (world.getBlockMaterial(x, y, z+i) == Material.ice)
					world.setBlock(x, y, z+i, Block.waterMoving.blockID);
			}
		}
		if (temperature > 0)	{ // Melting snow/ice
			for (int i = 0; i < 3; i++) {
				if (world.getBlockMaterial(x-i, y, z) == Material.snow)
					world.setBlock(x-i, y, z, 0);
				if (world.getBlockMaterial(x+i, y, z) == Material.snow)
					world.setBlock(x+i, y, z, 0);
				if (world.getBlockMaterial(x, y-i, z) == Material.snow)
					world.setBlock(x, y-i, z, 0);
				if (world.getBlockMaterial(x, y+i, z) == Material.snow)
					world.setBlock(x, y+i, z, 0);
				if (world.getBlockMaterial(x, y, z-i) == Material.snow)
					world.setBlock(x, y, z-i, 0);
				if (world.getBlockMaterial(x, y, z+i) == Material.snow)
					world.setBlock(x, y, z+i, 0);

				if (world.getBlockMaterial(x-i, y, z) == Material.craftedSnow)
					world.setBlock(x-i, y, z, 0);
				if (world.getBlockMaterial(x+i, y, z) == Material.craftedSnow)
					world.setBlock(x+i, y, z, 0);
				if (world.getBlockMaterial(x, y-i, z) == Material.craftedSnow)
					world.setBlock(x, y-i, z, 0);
				if (world.getBlockMaterial(x, y+i, z) == Material.craftedSnow)
					world.setBlock(x, y+i, z, 0);
				if (world.getBlockMaterial(x, y, z-i) == Material.craftedSnow)
					world.setBlock(x, y, z-i, 0);
				if (world.getBlockMaterial(x, y, z+i) == Material.craftedSnow)
					world.setBlock(x, y, z+i, 0);
			}
		}
		if (temperature > 900)	{ // Melting sand, ground
			for (int i = 0; i < 3; i++) {
				if (world.getBlockMaterial(x-i, y, z) == Material.sand)
					world.setBlock(x-i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x+i, y, z) == Material.sand)
					world.setBlock(x+i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y-i, z) == Material.sand)
					world.setBlock(x, y-i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y+i, z) == Material.sand)
					world.setBlock(x, y+i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z-i) == Material.sand)
					world.setBlock(x, y, z-i, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z+i) == Material.sand)
					world.setBlock(x, y, z+i, Block.glass.blockID);

				if (world.getBlockMaterial(x-i, y, z) == Material.ground)
					world.setBlock(x-i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x+i, y, z) == Material.ground)
					world.setBlock(x+i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y-i, z) == Material.ground)
					world.setBlock(x, y-i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y+i, z) == Material.ground)
					world.setBlock(x, y+i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z-i) == Material.ground)
					world.setBlock(x, y, z-i, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z+i) == Material.ground)
					world.setBlock(x, y, z+i, Block.glass.blockID);

				if (world.getBlockMaterial(x-i, y, z) == Material.grass)
					world.setBlock(x-i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x+i, y, z) == Material.grass)
					world.setBlock(x+i, y, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y-i, z) == Material.grass)
					world.setBlock(x, y-i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y+i, z) == Material.grass)
					world.setBlock(x, y+i, z, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z-i) == Material.grass)
					world.setBlock(x, y, z-i, Block.glass.blockID);
				if (world.getBlockMaterial(x, y, z+i) == Material.grass)
					world.setBlock(x, y, z+i, Block.glass.blockID);
			}
		}
		if (temperature > 1500)	{ // Melting rock
			for (int i = 0; i < 3; i++) {
				if (world.getBlockMaterial(x-i, y, z) == Material.rock)
					world.setBlock(x-i, y, z, Block.lavaMoving.blockID);
				if (world.getBlockMaterial(x+i, y, z) == Material.rock)
					world.setBlock(x+i, y, z, Block.lavaMoving.blockID);
				if (world.getBlockMaterial(x, y-i, z) == Material.rock)
					world.setBlock(x, y-i, z, Block.lavaMoving.blockID);
				if (world.getBlockMaterial(x, y+i, z) == Material.rock)
					world.setBlock(x, y+i, z, Block.lavaMoving.blockID);
				if (world.getBlockMaterial(x, y, z-i) == Material.rock)
					world.setBlock(x, y, z-i, Block.lavaMoving.blockID);
				if (world.getBlockMaterial(x, y, z+i) == Material.rock)
					world.setBlock(x, y, z+i, Block.lavaMoving.blockID);
			}
		}
	}

	/** Surrounds the block with fire. Args: World, x, y, z */
	public static void ignite(World world, int x, int y, int z) {
		if (world.getBlockId		(x-1, y, z) == 0)
			world.setBlock(x-1, y, z, Block.fire.blockID);
		if (world.getBlockId		(x+1, y, z) == 0)
			world.setBlock(x+1, y, z, Block.fire.blockID);
		if (world.getBlockId		(x, y-1, z) == 0)
			world.setBlock(x, y-1, z, Block.fire.blockID);
		if (world.getBlockId		(x, y+1, z) == 0)
			world.setBlock(x, y+1, z, Block.fire.blockID);
		if (world.getBlockId		(x, y, z-1) == 0)
			world.setBlock(x, y, z-1, Block.fire.blockID);
		if (world.getBlockId		(x, y, z+1) == 0)
			world.setBlock(x, y, z+1, Block.fire.blockID);
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
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.NETHER)
				Tamb = 300;
		}
		return Tamb;
	}

	/** Returns a broad-stroke biome temperature in degrees centigrade.
	 * Args: World, x, z */
	public static int getBiomeTemp(World world, int x, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return getBiomeTemp(biome);
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
		int numsparks = rand.nextInt(20)+20;
		numsparks *= sparkmultiplier;
		if (sparks)
			for (int i = 0; i < numsparks; i++)
				world.spawnParticle("lava", x+rand.nextFloat(), y+1, z+rand.nextFloat(), 0, 0, 0);
		ItemStack scrap = new ItemStack(id, 1, meta);
		int numdrops = rand.nextInt(maxdrops)+mindrops;
		if (!world.isRemote || id <= 0) {
			for (int i = 0; i < numdrops; i++) {
				EntityItem ent = new EntityItem(world, x+rand.nextFloat(), y+0.5, z+rand.nextFloat(), scrap);
				ent.motionX = -0.2+0.4*rand.nextFloat();
				ent.motionY = 0.5*rand.nextFloat();
				ent.motionZ = -0.2+0.4*rand.nextFloat();
				world.spawnEntityInWorld(ent);
				ent.velocityChanged = true;
			}
		}
	}

	/** Takes a specified amount of XP and splits it randomly among a bunch of orbs.
	 * Args: World, x, y, z, amount */
	public static void splitAndSpawnXP(World world, double x, double y, double z, int xp) {
		int max = xp/5+1;

		while (xp > 0) {
			int value = rand.nextInt(max)+1;
			while (value > xp)
				value = rand.nextInt(max)+1;
			xp -= value;
			EntityXPOrb orb = new EntityXPOrb(world, x, y, z, value);
			orb.motionX = -0.2+0.4*rand.nextFloat();
			orb.motionY = 0.3*rand.nextFloat();
			orb.motionZ = -0.2+0.4*rand.nextFloat();
			if (!world.isRemote) {
				orb.velocityChanged = true;
				world.spawnEntityInWorld(orb);
			}
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
		int id = world.getBlockId(x, y, z);
		if (id == 0)
			return false;
		Block b = Block.blocksList[id];
		return b instanceof BlockFluid || b instanceof BlockFluidBase;
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
		ReikaItemHelper.dropItems(world, x, y, z, Block.blocksList[id].getBlockDropped(world, x, y, z, metad, 0));
		if (id != 0)
			ReikaSoundHelper.playBreakSound(world, x, y, z, Block.blocksList[id]);
		world.setBlock(x, y, z, 0);
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
		ReikaItemHelper.dropItems(world, x, y, z, Block.blocksList[id].getBlockDropped(world, x, y, z, metad, 0));
		if (id != 0)
			ReikaSoundHelper.playBreakSound(world, x, y, z, Block.blocksList[id]);
		world.setBlock(x, y, z, 0);
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
		ReikaItemHelper.dropItems(world, x, y, z, Block.blocksList[id].getBlockDropped(world, x, y, z, metad, 0));
		if (id != 0)
			ReikaSoundHelper.playBreakSound(world, x, y, z, Block.blocksList[id]);
		world.setBlock(x, y, z, 0);
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
		/*ReikaJavaLibrary.pConsole(world.getBlockId(x, y, z)+" & "+id+" @ "+x0+", "+y0+", "+z0);
		ReikaJavaLibrary.pConsole(world.getBlockMetadata(x, y, z)+" & "+meta+" @ "+x0+", "+y0+", "+z0);
		ReikaJavaLibrary.pConsole(ReikaMathLibrary.py3d(x-x0, y-y0, z-z0)+" & "+r+" @ "+x0+", "+y0+", "+z0);*/
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
		return (world.clip(v1, v2) == null);
	}

	/** Returns true if there is a clear line of sight between two entites. Args: World, Entity 1, Entity 2 */
	public static boolean lineOfSight(World world, Entity e1, Entity e2) {
		if (world.isRemote)
			return false;
		Vec3 v1 = Vec3.fakePool.getVecFromPool(e1.posX, e1.posY+e1.getEyeHeight(), e1.posZ);
		Vec3 v2 = Vec3.fakePool.getVecFromPool(e2.posX, e2.posY+e2.getEyeHeight(), e2.posZ);
		return (world.clip(v1, v2) == null);
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
				//ReikaColorAPI.write(String.format("%f -->  %.3f,  %.3f, %.3f", i, vec2.xCoord, vec2.yCoord, vec2.zCoord));
				int id = world.getBlockId((int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord);
				if ((int)Math.floor(vec2.xCoord) == x && (int)Math.floor(vec2.yCoord) == y && (int)Math.floor(vec2.zCoord) == z) {
					//ReikaColorAPI.writeCoords(world, (int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord);
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
				//ReikaColorAPI.write(var11);
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
		boolean flag = world.setBlock(x, y, z, id, 0, 3);
		world.markBlockForUpdate(x, y, z);
		return flag;
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
				ei = new EntityItem(world, x+rand.nextFloat(), y+rand.nextFloat(), z+rand.nextFloat(), is);
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
				EntityItem ei = new EntityItem(world, x+rand.nextFloat(), y+rand.nextFloat(), z+rand.nextFloat(), s);
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

	/** Updates all blocks adjacent to the coordinate given. Args: World, x, y, z */
	public static void causeAdjacentUpdates(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		world.notifyBlocksOfNeighborChange(x, y, z, id);
	}

	/** Tests if a block is a dirt-type one, such that non-farm plants can grow on it. Args: id, metadata, material */
	public static boolean isDirtType(int id, int meta, Material mat) {
		if (id == Block.dirt.blockID)
			return true;
		if (id == Block.grass.blockID)
			return true;
		if (id == Block.gravel.blockID)
			return false;
		return false;
	}

	/** Tests if a block is a liquid block. Args: ID */
	public static boolean isLiquid(int id) {
		if (id == 0)
			return false;
		Block b = Block.blocksList[id];
		Material mat = b.blockMaterial;
		if (mat == Material.lava || mat == Material.water)
			return true;
		return b instanceof BlockFluid;
	}

	/** Drops all items from a given block. Args: World, x, y, z, fortune level */
	public static void dropBlockAt(World world, int x, int y, int z, int fortune) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ArrayList<ItemStack> li = Block.blocksList[id].getBlockDropped(world, x, y, z, meta, fortune);
		ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, li);
	}

	/** Drops all items from a given block with no fortune effect. Args: World, x, y, z */
	public static void dropBlockAt(World world, int x, int y, int z) {
		dropBlockAt(world, x, y, z, 0);
	}

	/** Sets the biome type at an xz column. Args: World, x, z, biome */
	public static void setBiomeForXZ(World world, int x, int z, BiomeGenBase biome) {
		Chunk ch = world.getChunkFromBlockCoords(x, z);

		int ax = x-ch.xPosition*16;
		int az = z-ch.zPosition*16;

		int index = az*16+ax;

		byte[] biomes = ch.getBiomeArray();
		biomes[index] = (byte)biome.biomeID;
		ch.setBiomeArray(biomes);
	}

	/** Sets the biome type at an xz column and mimics its generation. Args: World, x, z, biome */
	public static void setBiomeAndBlocksForXZ(World world, int x, int z, BiomeGenBase biome) {
		Chunk ch = world.getChunkFromBlockCoords(x, z);

		int ax = x-ch.xPosition*16;
		int az = z-ch.zPosition*16;

		int index = az*16+ax;

		byte[] biomes = ch.getBiomeArray();

		BiomeGenBase from = BiomeGenBase.biomeList[biomes[index]];

		biomes[index] = (byte)biome.biomeID;
		ch.setBiomeArray(biomes);

		int fillerID = from.fillerBlock;
		int topID = from.topBlock;

		for (int y = 30; y < world.provider.getHeight(); y++) {
			int id = world.getBlockId(x, y, z);
			if (id == fillerID) {
				world.setBlock(x, y, z, biome.fillerBlock);
			}
			if (id == topID && y == world.getTopSolidOrLiquidBlock(x, z)-1) {
				world.setBlock(x, y, z, biome.topBlock);
			}

			if (biome.getEnableSnow()) {
				if (world.canBlockFreeze(x, y, z, false))
					world.setBlock(x, y, z, Block.ice.blockID);
				else if (world.canBlockSeeTheSky(x, y+1, z) && world.isAirBlock(x, y+1, z))
					world.setBlock(x, y+1, z, Block.snow.blockID);
			}
			else {
				if (id == Block.snow.blockID)
					world.setBlock(x, y, z, 0);
				if (id == Block.ice.blockID)
					world.setBlock(x, y, z, Block.waterMoving.blockID);
			}
		}

		if (world.isRemote)
			return;

		BiomeDecorator dec = biome.theBiomeDecorator;

		int trees = dec.treesPerChunk;
		int grass = dec.grassPerChunk;
		int flowers = dec.flowersPerChunk;
		int cactus = dec.cactiPerChunk;
		int bushes = dec.deadBushPerChunk;
		int sugar = dec.reedsPerChunk;
		int mushrooms = dec.mushroomsPerChunk;
		int lily = dec.waterlilyPerChunk;
		int bigmush = dec.bigMushroomsPerChunk;

		double fac = 1/3D;
		int top = world.getTopSolidOrLiquidBlock(x, z);

		if (ReikaRandomHelper.doWithChance(fac*trees/96D)) {
			WorldGenerator gen = biome.getRandomWorldGenForTrees(rand);
			if (ReikaPlantHelper.SAPLING.canPlantAt(world, x, top, z)) {
				if (softBlocks(world, x, top, z))
					world.setBlock(x, top, z, 0);
				gen.generate(world, rand, x, top, z);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*grass/64D)) {
			WorldGenerator gen = biome.getRandomWorldGenForGrass(rand);
			if (softBlocks(world, x, top, z))
				world.setBlock(x, top, z, 0);
			gen.generate(world, rand, x, top, z);
		}

		if (ReikaRandomHelper.doWithChance(fac*bigmush/96D)) {
			if (softBlocks(world, x, top, z))
				world.setBlock(x, top, z, 0);
			biome.theBiomeDecorator.bigMushroomGen.generate(world, rand, x, top, z);
		}

		if (ReikaRandomHelper.doWithChance(fac*cactus/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.CACTUS.canPlantAt(world, x, y, z)) {
				int h = 1+rand.nextInt(3);
				for (int i = 0; i < h; i++)
					world.setBlock(x, y+i, z, Block.cactus.blockID);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*sugar/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.SUGARCANE.canPlantAt(world, x, y, z)) {
				int h = 1+rand.nextInt(3);
				for (int i = 0; i < h; i++)
					world.setBlock(x, y+i, z, Block.reed.blockID);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*bushes/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.BUSH.canPlantAt(world, x, y, z)) {
				world.setBlock(x, y, z, Block.deadBush.blockID);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*lily/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.LILYPAD.canPlantAt(world, x, y, z)) {
				world.setBlock(x, y, z, Block.waterlily.blockID);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*flowers/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.FLOWER.canPlantAt(world, x, y, z)) {
				if (rand.nextInt(3) == 0)
					world.setBlock(x, y, z, Block.plantRed.blockID);
				else
					world.setBlock(x, y, z, Block.plantYellow.blockID);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*128*mushrooms/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.MUSHROOM.canPlantAt(world, x, y, z)) {
				if (rand.nextInt(4) == 0)
					world.setBlock(x, y, z, Block.mushroomRed.blockID);
				else
					world.setBlock(x, y, z, Block.mushroomBrown.blockID);
			}
		}

		for (int i = 40; i < 80; i++) {
			world.markBlockForUpdate(x, i, z);
			causeAdjacentUpdates(world, x, i, z);
		}
	}

	/** Get the sun brightness as a fraction from 0-1. Args: World */
	public static float getSunIntensity(World world) {
		float ang = world.getCelestialAngle(0);
		float base = 1.0F - (MathHelper.cos(ang * (float)Math.PI * 2.0F) * 2.0F + 0.2F);

		if (base < 0.0F)
			base = 0.0F;

		if (base > 1.0F)
			base = 1.0F;

		base = 1.0F - base;
		base = (float)(base * (1.0D - world.getRainStrength(0) * 5.0F / 16.0D));
		base = (float)(base * (1.0D - world.getWeightedThunderStrength(0) * 5.0F / 16.0D));
		return base * 0.8F + 0.2F;
	}

	/** Returns the sun's declination, clamped to 0-90. Args: World */
	public static float getSunAngle(World world) {
		int time = (int)(world.getWorldTime()%12000);
		float suntheta = 0.5F*(float)(90*Math.sin(Math.toRadians(time*90D/6000D)));
		return suntheta;
	}

	/** Tests if a block is nearby, yes/no. Args: World, x, y, z, id to test, meta to test, range */
	public static boolean testBlockProximity(World world, int x, int y, int z, int id, int meta, int r) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int rx = x+i;
					int ry = y+j;
					int rz = z+k;
					int rid = world.getBlockId(rx, ry, rz);
					int rmeta = world.getBlockMetadata(rx, ry, rz);
					if (rid == id && (meta == -1 || rmeta == meta))
						return true;
				}
			}
		}
		return false;
	}

	/** Tests if a liquid is nearby, yes/no. Args: World, x, y, z, liquid material, range */
	public static boolean testLiquidProximity(World world, int x, int y, int z, Material mat, int r) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int rx = x+i;
					int ry = y+j;
					int rz = z+k;
					Material rmat = world.getBlockMaterial(rx, ry, rz);
					if (rmat == mat)
						return true;
				}
			}
		}
		return false;
	}

	/** A less intensive but less accurate block proximity test. Args: World, x, y, z, range */
	public static boolean testBlockProximityLoose(World world, int x, int y, int z, int id, int meta, int r) {
		int total = r*r*r*8; //(2r)^3
		int frac = total/16;
		for (int i = 0; i < frac; i++) {
			int rx = ReikaRandomHelper.getRandomPlusMinus(x, r);
			int ry = ReikaRandomHelper.getRandomPlusMinus(y, r);
			int rz = ReikaRandomHelper.getRandomPlusMinus(z, r);
			int rid = world.getBlockId(rx, ry, rz);
			int rmeta = world.getBlockMetadata(rx, ry, rz);
			if (rid == id && (meta == -1 || rmeta == meta))
				return true;
		}
		return false;
	}

	public static float getBiomeHumidity(BiomeGenBase biome) {
		biome = ReikaBiomeHelper.getParentBiomeType(biome);
		if (biome == BiomeGenBase.jungle)
			return 1F;
		if (biome == BiomeGenBase.ocean)
			return 1F;
		if (biome == BiomeGenBase.swampland)
			return 0.85F;
		if (biome == BiomeGenBase.forest)
			return 0.6F;
		if (biome == BiomeGenBase.plains)
			return 0.4F;
		if (biome == BiomeGenBase.desert)
			return 0.2F;
		if (biome == BiomeGenBase.hell)
			return 0.1F;
		if (biome == BiomeGenBase.beach)
			return 0.95F;
		if (biome == BiomeGenBase.icePlains)
			return 0.4F;
		if (biome == BiomeGenBase.mushroomIsland)
			return 0.75F;
		return 0.5F;
	}

	public static float getBiomeHumidity(World world, int x, int z) {
		return getBiomeHumidity(world.getBiomeGenForCoords(x, z));
	}

	public static EntityLivingBase getClosestLivingEntity(World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (!e.isDead && e.getHealth() > 0) {
				double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
				if (dd < d) {
					index = i;
					d = dd;
				}
			}
		}
		return index >= 0 ? li.get(index) : null;
	}

	public static EntityLivingBase getClosestLivingEntityNoPlayers(World world, double x, double y, double z, AxisAlignedBB box, boolean excludeCreativeOnly) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (!(e instanceof EntityPlayer) || (excludeCreativeOnly && !((EntityPlayer)e).capabilities.isCreativeMode)) {
				if (!e.isDead && e.getHealth() > 0) {
					double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
					if (dd < d) {
						index = i;
						d = dd;
					}
				}
			}
		}
		return index >= 0 ? li.get(index) : null;
	}

	public static EntityLivingBase getClosestHostileEntity(World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (ReikaEntityHelper.isHostile(e)) {
				if (!e.isDead && e.getHealth() > 0) {
					double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
					if (dd < d) {
						index = i;
						d = dd;
					}
				}
			}
		}
		return index >= 0 ? li.get(index) : null;
	}

	public static EntityLivingBase getClosestLivingEntityOfClass(Class<? extends EntityLivingBase> c, World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(c, box);
		double d = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < li.size(); i++) {
			EntityLivingBase e = li.get(i);
			if (!e.isDead && e.getHealth() > 0) {
				double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
				if (dd < d) {
					index = i;
					d = dd;
				}
			}
		}
		return index >= 0 ? li.get(index) : null;
	}

	public static EntityLivingBase getClosestLivingEntityOfClass(Class<? extends EntityLivingBase> c, World world, double x, double y, double z, double range) {
		AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x, y, z).expand(range, range, range);
		return getClosestLivingEntityOfClass(c, world, x, y, z, box);
	}

	public static boolean otherDimensionsExist() {
		return ModList.MYSTCRAFT.isLoaded() || ModList.TWILIGHT.isLoaded() || ModList.EXTRAUTILS.isLoaded();
	}
}
