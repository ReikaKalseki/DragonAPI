package Reika.DragonAPI.Extras;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import Reika.DragonAPI.Instantiable.Event.GenLayerBeachEvent;

public class GenLayerControllableShore extends GenLayer {

	public GenLayerControllableShore(long seed, GenLayer p) {
		super(seed);
		parent = p;
	}

	@Override
	/** When genning, args are usually chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10 */
	public int[] getInts(int permutedX, int permutedZ, int width, int height) {
		int[] data = parent.getInts(permutedX - 1, permutedZ - 1, width + 2, height + 2);
		int[] cache = IntCache.getIntCache(width * height);

		for (int bx = 0; bx < height; ++bx) {
			for (int bz = 0; bz < width; ++bz) {
				this.initChunkSeed(bz + permutedX, bx + permutedZ);
				int originalID = data[bz + 1 + (bx + 1) * (width + 2)];
				BiomeGenBase original = BiomeGenBase.getBiome(originalID);
				final int neighbor1 = data[bz + 1 + (bx + 1 - 1) * (width + 2)];
				final int neighbor2 = data[bz + 1 + 1 + (bx + 1) * (width + 2)];
				final int neighbor3 = data[bz + 1 - 1 + (bx + 1) * (width + 2)];
				final int neighbor4 = data[bz + 1 + (bx + 1 + 1) * (width + 2)];

				if (originalID == BiomeGenBase.mushroomIsland.biomeID) {

					if (neighbor1 != BiomeGenBase.ocean.biomeID && neighbor2 != BiomeGenBase.ocean.biomeID && neighbor3 != BiomeGenBase.ocean.biomeID && neighbor4 != BiomeGenBase.ocean.biomeID)
						cache[bz + bx * width] = originalID;
					else
						cache[bz + bx * width] = this.getHookedShore(original, BiomeGenBase.mushroomIslandShore.biomeID);
				}
				else if (original != null && original.getBiomeClass() == BiomeGenJungle.class) {
					if (this.func_151631_c(neighbor1) && this.func_151631_c(neighbor2) && this.func_151631_c(neighbor3) && this.func_151631_c(neighbor4)) {
						if (this.hasNoAdjacentOcean(neighbor1, neighbor2, neighbor3, neighbor4))
							cache[bz + bx * width] = originalID;
						else
							cache[bz + bx * width] = this.getHookedShore(original, BiomeGenBase.beach.biomeID);
					}
					else {
						cache[bz + bx * width] = BiomeGenBase.jungleEdge.biomeID;
					}
				}
				else if (originalID != BiomeGenBase.extremeHills.biomeID && originalID != BiomeGenBase.extremeHillsPlus.biomeID && originalID != BiomeGenBase.extremeHillsEdge.biomeID) {
					if (original != null && original.func_150559_j()) {
						this.applyBeachIfValid(data, cache, bz, bx, width, originalID, this.getHookedShore(original, BiomeGenBase.coldBeach.biomeID));
					}
					else if (originalID != BiomeGenBase.mesa.biomeID && originalID != BiomeGenBase.mesaPlateau_F.biomeID) {
						if (originalID != BiomeGenBase.ocean.biomeID && originalID != BiomeGenBase.deepOcean.biomeID && originalID != BiomeGenBase.river.biomeID && originalID != BiomeGenBase.swampland.biomeID) {
							if (this.hasNoAdjacentOcean(neighbor1, neighbor2, neighbor3, neighbor4))
								cache[bz + bx * width] = originalID;
							else
								cache[bz + bx * width] = this.getHookedShore(original, BiomeGenBase.beach.biomeID);
						}
						else {
							cache[bz + bx * width] = originalID;
						}
					}
					else {
						if (this.hasNoAdjacentOcean(neighbor1, neighbor2, neighbor3, neighbor4)) {
							if (this.isMesa(neighbor1) && this.isMesa(neighbor2) && this.isMesa(neighbor3) && this.isMesa(neighbor4))
								cache[bz + bx * width] = originalID;
							else
								cache[bz + bx * width] = BiomeGenBase.desert.biomeID;
						}
						else {
							cache[bz + bx * width] = originalID;
						}
					}
				}
				else {
					this.applyBeachIfValid(data, cache, bz, bx, width, originalID, this.getHookedShore(original, BiomeGenBase.stoneBeach.biomeID));
				}
			}
		}

		return cache;
	}

	private int getHookedShore(BiomeGenBase base, int beach) {
		return GenLayerBeachEvent.fire(base, beach);
	}

	private boolean hasNoAdjacentOcean(int n1, int n2, int n3, int n4) {
		return !isBiomeOceanic(n1) && !isBiomeOceanic(n2) && !isBiomeOceanic(n3) && !isBiomeOceanic(n4);
	}

	private void applyBeachIfValid(int[] data, int[] cache, int permutedX, int permutedZ, int width, int original, int beach) {
		if (isBiomeOceanic(original)) {
			cache[permutedX + permutedZ * width] = original;
		}
		else {
			int j1 = data[permutedX + 1 + (permutedZ + 1 - 1) * (width + 2)];
			int k1 = data[permutedX + 1 + 1 + (permutedZ + 1) * (width + 2)];
			int l1 = data[permutedX + 1 - 1 + (permutedZ + 1) * (width + 2)];
			int i2 = data[permutedX + 1 + (permutedZ + 1 + 1) * (width + 2)];

			if (this.hasNoAdjacentOcean(j1, k1, l1, i2))
				cache[permutedX + permutedZ * width] = original;
			else
				cache[permutedX + permutedZ * width] = beach;
		}
	}

	private boolean func_151631_c(int id) {
		return BiomeGenBase.getBiome(id) != null && BiomeGenBase.getBiome(id).getBiomeClass() == BiomeGenJungle.class ? true : id == BiomeGenBase.jungleEdge.biomeID || id == BiomeGenBase.jungle.biomeID || id == BiomeGenBase.jungleHills.biomeID || id == BiomeGenBase.forest.biomeID || id == BiomeGenBase.taiga.biomeID || isBiomeOceanic(id);
	}

	private boolean isMesa(int id) {
		return BiomeGenBase.getBiome(id) != null && BiomeGenBase.getBiome(id) instanceof BiomeGenMesa;
	}
}