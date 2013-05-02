package Reika.DragonAPI;

import java.util.Random;

import net.minecraft.world.World;

public final class ReikaMediaHelper {
	
	private ReikaMediaHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}
	
	static Random par5Random = new Random();
	
	public static void playSoundVarying(World world, int x, int y, int z, String name) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, name, par5Random.nextFloat(), par5Random.nextFloat());
	}
	
}
