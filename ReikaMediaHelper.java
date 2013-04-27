package Reika.DragonAPI;

import java.util.Random;

import net.minecraft.world.World;

public abstract class ReikaMediaHelper {
	
	static Random par5Random = new Random();
	
	public static void playSoundVarying(World world, int x, int y, int z, String name) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, name, par5Random.nextFloat(), par5Random.nextFloat());
	}
	
}
