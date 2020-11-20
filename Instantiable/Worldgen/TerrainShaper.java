package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class TerrainShaper {

	private final Random rand = new Random();

	private Block[] blockColumn;
	private byte[] metaColumn;

	public final void generateColumn(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks, byte[] metas, BiomeGenBase biome) {
		blockColumn = blocks;
		metaColumn = metas;

		long seed = chunkX*341873128712L+chunkZ*132897987541L;
		seed ^= (x%16)*287238347L+(z%16)*8258735447L;
		rand.setSeed(seed);
		rand.nextBoolean();
		rand.nextBoolean();

		if (this.shouldClear()) {
			for (int i = 0; i < 256; i++) {
				this.setBlock(x, i, z, Blocks.air);
			}
		}

		this.generateColumn(world, x, z, rand, biome);
	}

	protected boolean shouldClear() {
		return true;
	}

	protected abstract void generateColumn(World world, int x, int z, Random rand, BiomeGenBase biome);

	protected final void shiftVertical(int x, int z, int delta) {
		this.shiftVertical(x, z, delta, null, -1);
	}

	/** Will copy the lowest layers in the shift if no fill is set! */
	protected final void shiftVertical(int x, int z, int delta, Block fill, int fillm) {
		int pos = this.calcPosIndex(x, z);
		for (int y = 255-delta; y >= 0; y--) {
			int put = y+delta;
			Block b = blockColumn[pos+y];
			byte meta = metaColumn[pos+y];
			blockColumn[pos+put] = b;
			metaColumn[pos+put] = meta;
		}
		if (fill != null) {
			for (int y = 0; y < delta; y++) {
				blockColumn[pos+y] = fill;
				metaColumn[pos+y] = (byte)fillm;
			}
		}
	}

	protected final int calcPosIndex(int x, int z) {
		int dx = x & 15;
		int dz = z & 15;
		int d = 256;//blockColumn.length / 256;
		return (dx * 16 + dz) * d;
	}

	protected final int getTopNonAir(int x, int z) {
		int pos = this.calcPosIndex(x, z);
		for (int y = 255; y >= 0; y--) {
			if (blockColumn[pos+y] != null && blockColumn[pos+y] != Blocks.air)
				return y;
		}
		return -1;
	}

	protected final Block getBlock(int x, int y, int z) {
		return blockColumn[this.calcPosIndex(x, z)+y];
	}

	protected final int getMetadata(int x, int y, int z) {
		return metaColumn[this.calcPosIndex(x, z)+y];
	}

	protected final void setBlock(int x, int y, int z, Block b) {
		this.setBlock(x, y, z, b, 0);
	}

	protected final void setBlock(int x, int y, int z, Block b, int meta) {
		if (meta < 0)
			throw new IllegalArgumentException("Negative metadata @ "+x+", "+y+", "+z+"!");
		//int cx = x-x%16;
		//if (cx < 0)
		//	cx -= 16;
		//int cz = z-z%16;
		//if (cz < 0)
		//	cz -= 16;
		//int dx = ChunkSplicedGenerationCache.modAndAlign(x);//x-cx;
		//int dz = ChunkSplicedGenerationCache.modAndAlign(z);//z-cz;
		//int x = chunkX*16+dx;
		//int z = chunkZ*16+dz;
		//int d = (dx*16+dz);
		//int posIndex = d*blockColumn.length/256;

		int posIndex = this.calcPosIndex(x, z);

		blockColumn[posIndex+y] = b;
		metaColumn[posIndex+y] = (byte)meta;
	}
}
