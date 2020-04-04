package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class LeafDecayEvent extends Event {

	public final Block query;

	public final IBlockAccess world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public LeafDecayEvent(IBlockAccess world, int x, int y, int z, Block b) {
		this.world = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;

		query = b;
	}

	public static boolean fire(Block b, IBlockAccess world, int x, int y, int z) {
		LeafDecayEvent evt = new LeafDecayEvent(world, x, y, z, b);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return false;
			default:
			case DEFAULT:
				return b.canSustainLeaves(world, x, y, z);
			case DENY:
				return true;
		}
	}

}
