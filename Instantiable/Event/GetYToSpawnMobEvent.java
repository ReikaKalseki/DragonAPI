package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

public class GetYToSpawnMobEvent extends WorldPositionEvent {

	public int yToTry;

	public GetYToSpawnMobEvent(World world, int x, int y, int z) {
		super(world, x, y, z);

		yToTry = yCoord;
	}

	public static int fire(World world, int x, int y, int z) {
		GetYToSpawnMobEvent e = new GetYToSpawnMobEvent(world, x, y, z);
		MinecraftForge.EVENT_BUS.post(e);
		return e.yToTry;
	}

}
