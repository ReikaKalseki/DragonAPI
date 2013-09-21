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

import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.DragonAPICore;

public class ReikaBiomeHelper extends DragonAPICore {

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
}
