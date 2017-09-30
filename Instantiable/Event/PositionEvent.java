package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;


public abstract class PositionEvent extends Event {

	public final World world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public PositionEvent(World world, int x, int y, int z) {
		this.world = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

}
