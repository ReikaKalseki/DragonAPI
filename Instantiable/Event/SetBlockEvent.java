package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.eventhandler.Event;

/** Fired when a setBlock propagates and succeeds inside a chunk. This is fired both client and server side. */
public class SetBlockEvent extends Event {

	private final Chunk chunk;

	public final World world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public final ChunkCoordIntPair chunkLocation;

	public SetBlockEvent(Chunk ch, int x, int y, int z) {
		chunk = ch;
		world = ch.worldObj;
		chunkLocation = new ChunkCoordIntPair(ch.xPosition, ch.zPosition);
		xCoord = ch.xPosition*16+x;
		yCoord = y;
		zCoord = ch.zPosition*16+z;
	}

}
