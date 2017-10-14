package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class ChunkPopulationEvent extends Event {

	public final World world;
	public final int chunkX;
	public final int chunkZ;
	public final IChunkProvider generator;
	public final IChunkProvider loader;

	public ChunkPopulationEvent(World world, int cx, int cz, IChunkProvider gen, IChunkProvider load) {
		this.world = world;
		chunkX = cx;
		chunkZ = cz;
		generator = gen;
		loader = load;
	}

}
