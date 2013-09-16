package Reika.DragonAPI.Instantiable;

import java.util.Random;

import net.minecraft.world.World;
import cpw.mods.fml.common.IWorldGenerator;

public class Retrogenner {

	private final IWorldGenerator gen;
	private static final Random rand = new Random();

	public Retrogenner(IWorldGenerator generator) {
		gen = generator;
	}

	public void callOnChunk(World world, int chunkX, int chunkZ) {
		//gen.generate(rand, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
	}

}
