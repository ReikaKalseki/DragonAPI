package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class IceFreezeEvent extends WorldEvent {

	public final int x;
	public final int y;
	public final int z;
	public final boolean needsEdge;

	public IceFreezeEvent(World world, int x, int y, int z, boolean edge) {
		super(world);
		this.x = x;
		this.y = y;
		this.z = z;
		needsEdge = edge;
	}

	public boolean wouldFreezeNaturally() {
		return world.provider.canBlockFreeze(x, y, z, needsEdge);
	}

}
