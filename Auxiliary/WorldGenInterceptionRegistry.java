package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.ChunkGenerationEvent;
import Reika.DragonAPI.Instantiable.Event.ChunkPopulationEvent;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SinglePlayerLogoutEvent;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;


public class WorldGenInterceptionRegistry {

	public static final WorldGenInterceptionRegistry instance = new WorldGenInterceptionRegistry();

	private int runningChunkDecoration = 0;
	private final HashMap<Coordinate, BlockSetData> data = new HashMap();
	private final ArrayList<BlockSetWatcher> watchers = new ArrayList();
	private final ArrayList<IWGWatcher> IWGwatchers = new ArrayList();
	private final ArrayList<InterceptionException> exceptions = new ArrayList();
	private boolean dispatchingChanges = false;

	private WorldGenInterceptionRegistry() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addWatcher(BlockSetWatcher w) {
		watchers.add(w);
	}

	public void addIWGWatcher(IWGWatcher w) {
		IWGwatchers.add(w);
	}

	public void addException(InterceptionException e) {
		exceptions.add(e);
	}

	@SubscribeEvent
	public void prePopulation(ChunkGenerationEvent evt) {
		if (!watchers.isEmpty())
			runningChunkDecoration++;
	}

	@SubscribeEvent
	public void onSetBlock(SetBlockEvent.Pre evt) {
		if (watchers.isEmpty())
			return;
		if (runningChunkDecoration <= 0)
			return;
		if (dispatchingChanges)
			return;
		for (InterceptionException e : exceptions)
			if (e.doesExceptionApply(evt.world, evt.xCoord, evt.yCoord, evt.zCoord))
				return;
		Coordinate c = new Coordinate(evt.xCoord, evt.yCoord, evt.zCoord);
		data.put(c, new BlockSetData(c, evt.currentBlock, evt.currentMeta, evt.newBlock, evt.newMeta));
	}

	public void postPopulation(World world, int cx, int cz) {
		//ReikaJavaLibrary.pConsole("Watching chunk "+cx+","+cz+" block change set with "+set.size()+" blocks");
		dispatchingChanges = true;
		for (BlockSetWatcher w : watchers) {
			w.onChunkGeneration(world, data);
		}
		dispatchingChanges = false;
		data.clear();
		runningChunkDecoration--;
		if (runningChunkDecoration < 0)
			runningChunkDecoration = 0;
	}

	@SubscribeEvent
	public void onSetBlock(SinglePlayerLogoutEvent evt) {
		data.clear();
		runningChunkDecoration = 0;
		dispatchingChanges = false;
	}

	public static void interceptChunkPopulation(int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader) {
		ChunkPopulationEvent evt = new ChunkPopulationEvent(world, cx, cz, generator, loader);
		if (MinecraftForge.EVENT_BUS.post(evt)) {

		}
		else {
			GameRegistry.generateWorld(cx, cz, world, generator, loader);
		}
		instance.postPopulation(world, cx, cz);
	}

	public static void interceptIWG(IWorldGenerator gen, Random random, int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader) {
		for (IWGWatcher w : instance.IWGwatchers) {
			if (!w.canIWGRun(gen, random, cx, cz, world, generator, loader))
				return;
		}
		gen.generate(random, cx, cz, world, generator, loader);
	}

	public static interface BlockSetWatcher {

		public void onChunkGeneration(World world, Map<Coordinate, BlockSetData> set);

	}

	public static interface InterceptionException {

		public boolean doesExceptionApply(World world, int x, int y, int z);

	}

	public static interface IWGWatcher {

		public boolean canIWGRun(IWorldGenerator gen, Random random, int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader);

	}

	public final class BlockSetData {

		public final Coordinate location;
		public final Block oldBlock;
		public final int oldMetadata;
		public final Block newBlock;
		public final int newMetadata;

		private BlockSetData(Coordinate c, Block old, int oldmeta, Block b, int meta) {
			location = c;
			oldBlock = old;
			oldMetadata = oldmeta;
			newBlock = b;
			newMetadata = meta;
		}

		public TileEntity getTileEntity(World world) {
			return location.getTileEntity(world);
		}

		public void revert(World world) {
			location.setBlock(world, oldBlock, oldMetadata, 2);
		}

	}

}