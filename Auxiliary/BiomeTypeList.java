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

import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.ModList;

public enum BiomeTypeList {

	OCEAN(BiomeGenBase.ocean, BiomeGenBase.frozenOcean),
	PLAINS(BiomeGenBase.plains),
	DESERT(BiomeGenBase.desert, BiomeGenBase.desertHills),
	MOUNTAIN(BiomeGenBase.extremeHills, BiomeGenBase.extremeHillsEdge),
	FOREST(BiomeGenBase.forest, BiomeGenBase.forestHills),
	TAIGA(BiomeGenBase.taiga, BiomeGenBase.taigaHills),
	SWAMP(BiomeGenBase.swampland),
	RIVER(BiomeGenBase.river, BiomeGenBase.frozenRiver, BiomeGenBase.beach),
	ARCTIC(BiomeGenBase.icePlains, BiomeGenBase.iceMountains),
	MUSHROOM(BiomeGenBase.mushroomIsland, BiomeGenBase.mushroomIslandShore),
	JUNGLE(BiomeGenBase.jungle, BiomeGenBase.jungleHills),
	RAINBOW("Rainbow Forest", ModList.DYETREES, "Reika.DyeTrees.World.BiomeRainbowForest"),
	ENDER("Ender Forest", ModList.ENDERFOREST, "Reika.EnderForest.World.BiomeEnderForest");

	private String[] biomes;
	private ModList dependency;
	public final String displayName;

	public static final BiomeTypeList[] biomeList = values();

	private BiomeTypeList(BiomeGenBase... biomes) {
		this.biomes = new String[biomes.length];
		for (int i = 0; i < biomes.length; i++) {
			this.biomes[i] = biomes[i].getClass().getCanonicalName();
		}
		displayName = biomes[0].biomeName;
	}

	private BiomeTypeList(String name, ModList mod, String... biomes) {
		dependency = mod;
		if (!dependency.isLoaded()) {
			displayName = name+" (Unloaded)";
			return;
		}
		this.biomes = biomes;
		displayName = name;
	}

	public boolean isAvailable() {
		return dependency != null ? dependency.isLoaded() : true;
	}

	public static BiomeTypeList getEntry(BiomeGenBase biome) {
		for (int i = 0; i < biomeList.length; i++) {
			BiomeTypeList type = biomeList[i];
			if (type.isAvailable()) {
				String[] biomes = type.biomes;
				for (int j = 0; j < biomes.length; j++) {
					if (biome.getClass().getCanonicalName().equals(biomes[j]))
						return biomeList[i];
				}
			}
		}
		return null;
	}
}
