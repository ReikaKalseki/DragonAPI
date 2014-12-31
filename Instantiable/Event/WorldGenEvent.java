package Reika.DragonAPI.Instantiable.Event;

import java.util.Random;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public abstract class WorldGenEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;
	private final Random rand;

	public WorldGenEvent(World world, int x, int y, int z, Random r) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		rand = r;
	}

	public final int getRandomInt(int n) {
		return rand.nextInt(n);
	}

	public final float getRandomFloat() {
		return rand.nextFloat();
	}

	public final double getRandomDouble() {
		return rand.nextDouble();
	}

}
