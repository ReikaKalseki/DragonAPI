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

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class ReikaBiomeHelper extends DragonAPICore {

	/** Returns the first empty biome index. */
	public static int getFirstEmptyBiomeIndex() {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			if (BiomeGenBase.biomeList[i] == null)
				return i;
		}
		throw new RuntimeException("Error: Biome Limit Exceeded!");
	}

	/** Note that this is affected by other mods, so exclusive calls on this will end up including mod biomes */
	public static List<BiomeGenBase> getAllBiomes() {
		List<BiomeGenBase> li = new ArrayList<BiomeGenBase>();
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			li.add(BiomeGenBase.biomeList[i]);
		}
		return li;
	}

	/** Returns the biome supplied and any associated ones (eg Desert+DesertHills). Args: Biome */
	public static List<BiomeGenBase> getAllAssociatedBiomes(BiomeGenBase biome) {
		List<BiomeGenBase> li = new ArrayList<BiomeGenBase>();
		li.add(biome);

		if (biome == BiomeGenBase.desert)
			li.add(BiomeGenBase.desertHills);

		if (biome == BiomeGenBase.extremeHills)
			li.add(BiomeGenBase.extremeHillsEdge);

		if (biome == BiomeGenBase.forest)
			li.add(BiomeGenBase.forestHills);

		if (biome == BiomeGenBase.taiga)
			li.add(BiomeGenBase.taigaHills);

		if (biome == BiomeGenBase.icePlains)
			li.add(BiomeGenBase.iceMountains);

		if (biome == BiomeGenBase.mushroomIsland)
			li.add(BiomeGenBase.mushroomIslandShore);

		if (biome == BiomeGenBase.jungle)
			li.add(BiomeGenBase.jungleHills);

		return li;
	}

	/** Returns the biome's parent. Args: Biome */
	public static BiomeGenBase getParentBiomeType(BiomeGenBase biome) {
		if (biome == BiomeGenBase.desertHills)
			return (BiomeGenBase.desert);

		if (biome == BiomeGenBase.extremeHillsEdge)
			return (BiomeGenBase.extremeHills);

		if (biome == BiomeGenBase.forestHills)
			return (BiomeGenBase.forest);

		if (biome == BiomeGenBase.taigaHills)
			return (BiomeGenBase.taiga);

		if (biome == BiomeGenBase.iceMountains)
			return (BiomeGenBase.icePlains);

		if (biome == BiomeGenBase.mushroomIslandShore)
			return (BiomeGenBase.mushroomIsland);

		if (biome == BiomeGenBase.jungleHills)
			return (BiomeGenBase.jungle);
		return biome;
	}

	/** Returns whether the biome is a variant of a parent. Args: Biome */
	public static boolean isChildBiome(BiomeGenBase biome) {
		if (biome == BiomeGenBase.desertHills)
			return true;
		if (biome == BiomeGenBase.extremeHillsEdge)
			return true;
		if (biome == BiomeGenBase.forestHills)
			return true;
		if (biome == BiomeGenBase.taigaHills)
			return true;
		if (biome == BiomeGenBase.iceMountains)
			return true;
		if (biome == BiomeGenBase.mushroomIslandShore)
			return true;
		if (biome == BiomeGenBase.jungleHills)
			return true;
		return false;
	}

	/** Converts the given coordinates to an RGB representation of those coordinates' biome's color, for the given material type.
	 * Args: World, x, z, material (String) */
	public static int[] biomeToRGB(World world, int x, int z, String material) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = biomeToHex(biome, material);
		return ReikaColorAPI.HexToRGB(color);
	}

	public static int[] biomeToRGB(IBlockAccess world, int x, int z, String material) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = biomeToHex(biome, material);
		return ReikaColorAPI.HexToRGB(color);
	}

	/** Converts the given coordinates to a hex representation of those coordinates' biome's color, for the given material type.
	 * Args: World, x, z, material (String) */
	public static int biomeToHexColor(World world, int x, int z, String material) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = biomeToHex(biome, material);
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

	/** Returns a broad-stroke biome temperature in degrees centigrade.
	 * Args: biome */
	public static int getBiomeTemp(BiomeGenBase biome) {
		int Tamb = 25; //Most biomes = 25C
		if (isSnowBiome(biome))
			Tamb = -20; //-20C
		if (isHotBiome(biome))
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

	public static float getBiomeHumidity(BiomeGenBase biome) {
		biome = getParentBiomeType(biome);
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
}
