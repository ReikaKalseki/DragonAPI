package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/** Fired right before a block is burned by fire. Cancel it to stop the consumption. */
@Cancelable
public class BlockConsumedByFireEvent extends Event {

	public final World world;
	public final int x;
	public final int y;
	public final int z;

	public BlockConsumedByFireEvent(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
