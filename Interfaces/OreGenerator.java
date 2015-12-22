package Reika.DragonAPI.Interfaces;

import java.util.Random;

import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;


public interface OreGenerator {

	public void generateOre(OreEnum ore, Random random, World world, int chunkX, int chunkZ);

}
