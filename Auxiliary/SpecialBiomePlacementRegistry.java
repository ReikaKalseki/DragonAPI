/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Maps.PluralMap;


/** See {@link net.minecraft.world.gen.layer.GenLayerBiome#getInts} */
public class SpecialBiomePlacementRegistry {

	public static final SpecialBiomePlacementRegistry instance = new SpecialBiomePlacementRegistry();

	private final PluralMap<Integer> IDs = new PluralMap(2);

	/** See {@link net.minecraft.world.gen.layer.GenLayerEdge#getIntsSpecial} */
	private static final int VARIANTS = 16;

	private SpecialBiomePlacementRegistry() {

	}

	/** Note well: You are REPLACING some of the placement regions of the default biome of the category; each variant is 1/16th of it. */
	public void registerID(DragonAPIMod mod, Category c, int idx, int biomeID) {
		if (idx < 0 || idx >= VARIANTS)
			throw new RegistrationException(mod, "Special biome variant indices can only be within 0-15!");
		if (IDs.containsKeyV(c, idx))
			throw new RegistrationException(mod, "Special biome variant "+idx+" for "+c+" is already occupied!");
		IDs.put(biomeID, c, idx);
	}

	public int getBiomeID(Category c, int variant) {
		Integer get = IDs.get(c, variant);
		return get != null ? get.intValue() : c.defaultBiome.biomeID;
	}

	public static int getBiome_Hot(int variant) {
		return instance.getBiomeID(Category.HOT, variant);
	}

	public static int getBiome_Hot2(int variant) {
		return instance.getBiomeID(Category.HOT2, variant);
	}

	public static int getBiome_Warm(int variant) {
		return instance.getBiomeID(Category.WARM, variant);
	}

	public static int getBiome_Cool(int variant) {
		return instance.getBiomeID(Category.COOL, variant);
	}

	public static int getBiome_Cold(int variant) {
		return instance.getBiomeID(Category.COLD, variant);
	}

	public static enum Category {
		COLD(4, BiomeGenBase.mushroomIsland),
		COOL(3, BiomeGenBase.megaTaiga),
		WARM(2, BiomeGenBase.jungle),
		HOT(1, BiomeGenBase.mesaPlateau), //33% occurrence
		HOT2(1, BiomeGenBase.mesaPlateau_F); //66%

		private final int flag;
		private final BiomeGenBase defaultBiome;

		private Category(int f, BiomeGenBase b) {
			flag = f;
			defaultBiome = b;
		}
	}

}
