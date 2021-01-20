package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/** Fired when a world is created for the very first time, NOT simply reloaded from disk. */
@Deprecated
public class WorldCreationEvent extends WorldEvent {

	public WorldCreationEvent(World world) {
		super(world);
	}

}
