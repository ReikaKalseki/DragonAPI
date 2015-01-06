/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

public class ReikaBiomeHelper extends DragonAPICore {

	private static final MultiMap<BiomeGenBase, BiomeGenBase> children = new MultiMap();
	private static final MultiMap<BiomeGenBase, BiomeGenBase> similarity = new MultiMap();
	private static final HashMap<BiomeGenBase, BiomeGenBase> parents = new HashMap();

	static {
		addChildBiome(BiomeGenBase.desert, BiomeGenBase.desertHills);

		addChildBiome(BiomeGenBase.forest, BiomeGenBase.forestHills);

		addChildBiome(BiomeGenBase.taiga, BiomeGenBase.taigaHills);
		addChildBiome(BiomeGenBase.taiga, BiomeGenBase.coldTaiga, false);
		addChildBiome(BiomeGenBase.taiga, BiomeGenBase.megaTaiga, false);

		addChildBiome(BiomeGenBase.jungle, BiomeGenBase.jungleHills);
		addChildBiome(BiomeGenBase.jungle, BiomeGenBase.jungleEdge);

		addChildBiome(BiomeGenBase.megaTaiga, BiomeGenBase.megaTaigaHills);

		addChildBiome(BiomeGenBase.coldTaiga, BiomeGenBase.coldTaigaHills);

		addChildBiome(BiomeGenBase.icePlains, BiomeGenBase.iceMountains);

		addChildBiome(BiomeGenBase.birchForest, BiomeGenBase.birchForestHills);

		addChildBiome(BiomeGenBase.extremeHills, BiomeGenBase.extremeHillsEdge);
		addChildBiome(BiomeGenBase.extremeHills, BiomeGenBase.extremeHillsPlus);

		addChildBiome(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau);
		addChildBiome(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau_F);

		addChildBiome(BiomeGenBase.beach, BiomeGenBase.coldBeach, false);
		addChildBiome(BiomeGenBase.beach, BiomeGenBase.stoneBeach);

		addChildBiome(BiomeGenBase.ocean, BiomeGenBase.deepOcean, false);
		addChildBiome(BiomeGenBase.ocean, BiomeGenBase.frozenOcean, false);

		addChildBiome(BiomeGenBase.river, BiomeGenBase.frozenRiver, false);

		addChildBiome(BiomeGenBase.savanna, BiomeGenBase.savannaPlateau);
	}

	private static void addChildBiome(BiomeGenBase parent, BiomeGenBase child) {
		addChildBiome(parent, child, true);
	}

	private static void addChildBiome(BiomeGenBase parent, BiomeGenBase child, boolean isChild) {
		similarity.addValue(parent, child);
		if (isChild) {
			children.addValue(parent, child);
			parents.put(child, parent);
		}
	}

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
			if (BiomeGenBase.biomeList[i] != null)
				li.add(BiomeGenBase.biomeList[i]);
		}
		return li;
	}

	/** Returns any associated biomes (eg Desert+DesertHills) to the one supplied. Args: Biome, Whether to match "loosely". Loose matching
	 * is defined as similarity between two biomes where one is not a parent of the other but both are similar in nature, eg Taiga+Cold Taiga */
	public static Collection<BiomeGenBase> getAllAssociatedBiomes(BiomeGenBase biome, boolean loose) {
		return Collections.unmodifiableCollection(loose ? similarity.get(biome) : children.get(biome));
	}

	public static Collection<BiomeGenBase> getChildBiomes(BiomeGenBase biome) {
		return getAllAssociatedBiomes(biome, false);
	}

	/** Returns the biome's parent. Args: Biome */
	public static BiomeGenBase getParentBiomeType(BiomeGenBase biome) {
		return parents.containsKey(biome) ? parents.get(biome) : biome;
	}

	/** Returns whether the biome is a variant of a parent. Args: Biome */
	public static boolean isChildBiome(BiomeGenBase biome) {
		return parents.containsKey(biome);
	}

	/** Converts the given coordinates to an RGB representation of those coordinates' biome's color, for the given material type.
	 * Args: World, x, z, material (String) */
	public static int[] biomeToRGB(IBlockAccess world, int x, int y, int z, String material) {
		int color = biomeToHex(world, x, y, z, material);
		return ReikaColorAPI.HexToRGB(color);
	}

	/** Converts the given coordinates to a hex representation of those coordinates' biome's color, for the given material type.
	 * Args: World, x, z, material (String) */
	public static int biomeToHexColor(IBlockAccess world, int x, int y, int z, String material) {
		int color = biomeToHex(world, x, y, z, material);
		return color;
	}

	private static int biomeToHex(IBlockAccess world, int x, int y, int z, String mat) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		int color = 0;
		if (mat == "Leaves")
			color = biome.getBiomeFoliageColor(x, y, z);
		if (mat == "Grass")
			color = biome.getBiomeGrassColor(x, y, z);
		if (mat == "Water")
			color = biome.getWaterColorMultiplier();
		if (mat == "Sky")
			color = biome.getSkyColorByTemp(biome.getFloatTemperature(x, y, z));
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
		if (biome == BiomeGenBase.coldTaiga)
			return true;
		if (biome == BiomeGenBase.coldTaigaHills)
			return true;
		if (biome.getEnableSnow())
			return true;
		if (biome.biomeName.toLowerCase().contains("arctic"))
			return true;
		if (biome.biomeName.toLowerCase().contains("tundra"))
			return true;
		if (biome.biomeName.toLowerCase().contains("alpine"))
			return true;
		if (biome.biomeName.toLowerCase().contains("frozen"))
			return true;
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.FROZEN)
				return true;
			if (types[i] == BiomeDictionary.Type.COLD)
				return true;
			if (types[i] == BiomeDictionary.Type.SNOWY)
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
			if (types[i] == BiomeDictionary.Type.HOT)
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
		if (biome == BiomeGenBase.sky)
			Tamb = -100;
		if (biome == BiomeGenBase.hell)
			Tamb = 300;	//boils water, so 300C (3 x 100)
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.NETHER)
				Tamb = 300;
			if (types[i] == BiomeDictionary.Type.END)
				Tamb = -100;
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
			return 0.95F;
		if (biome == BiomeGenBase.ocean)
			return 1F;
		if (biome == BiomeGenBase.deepOcean)
			return 1F;
		if (biome == BiomeGenBase.swampland)
			return 0.85F;
		if (biome == BiomeGenBase.forest)
			return 0.6F;
		if (biome == BiomeGenBase.birchForest)
			return 0.55F;
		if (biome == BiomeGenBase.roofedForest)
			return 0.7F;
		if (biome == BiomeGenBase.plains)
			return 0.4F;
		if (biome == BiomeGenBase.savanna)
			return 0.3F;
		if (biome == BiomeGenBase.desert)
			return 0.2F;
		if (biome == BiomeGenBase.mesa)
			return 0.2F;
		if (biome == BiomeGenBase.hell)
			return 0.1F;
		if (biome == BiomeGenBase.sky)
			return 0.1F;
		if (biome == BiomeGenBase.beach)
			return 0.98F;
		if (biome == BiomeGenBase.icePlains)
			return 0.4F;
		if (biome == BiomeGenBase.mushroomIsland)
			return 0.75F;
		BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			if (types[i] == BiomeDictionary.Type.BEACH)
				return 0.95F;
			if (types[i] == BiomeDictionary.Type.OCEAN || types[i] == BiomeDictionary.Type.RIVER || types[i] == BiomeDictionary.Type.WATER)
				return 1F;
			if (types[i] == BiomeDictionary.Type.JUNGLE)
				return 1;
			if (types[i] == BiomeDictionary.Type.DESERT || types[i] == BiomeDictionary.Type.SANDY)
				return 0.2F;
			if (types[i] == BiomeDictionary.Type.NETHER || types[i] == BiomeDictionary.Type.END)
				return 1;
			if (types[i] == BiomeDictionary.Type.WASTELAND)
				return 0.1F;
			if (types[i] == BiomeDictionary.Type.WET)
				return 0.7F;
			if (types[i] == BiomeDictionary.Type.DRY)
				return 0.3F;
		}
		return 0.5F;
	}

	public static float getBiomeHumidity(World world, int x, int z) {
		return getBiomeHumidity(world.getBiomeGenForCoords(x, z));
	}

	public static boolean isOcean(BiomeGenBase biome) {
		if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.frozenOcean || biome == BiomeGenBase.deepOcean)
			return true;
		if (BiomeDictionary.isBiomeOfType(biome, Type.OCEAN))
			return true;
		return ReikaStringParser.containsWord(biome.biomeName.toLowerCase(), "ocean");
	}

	public static void removeBiomeWithAssociates(BiomeGenBase biome) {
		BiomeManager.removeSpawnBiome(biome);
		Collection<BiomeGenBase> c = getChildBiomes(biome);
		for (BiomeGenBase b : c)
			BiomeManager.removeSpawnBiome(b);
	}

	public static void removeAllBiomesBut(Collection<BiomeGenBase> biomes) {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (!biomes.contains(b))
				BiomeManager.removeSpawnBiome(b);
		}
	}

	public static void removeAllBiomesBut(BiomeGenBase... biomes) {
		removeAllBiomesBut(ReikaJavaLibrary.makeListFromArray(biomes));
	}

	public static void removeAllBiomesBut(BiomeGenBase biome) {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != biome)
				BiomeManager.removeSpawnBiome(b);
		}
	}
}
