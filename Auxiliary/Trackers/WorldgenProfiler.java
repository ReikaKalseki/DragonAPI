package Reika.DragonAPI.Auxiliary.Trackers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.ChunkProviderEvent.ReplaceBiomeBlocks;
import Reika.DragonAPI.Auxiliary.Trackers.EventProfiler.EventProfile;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import cpw.mods.fml.common.IWorldGenerator;


public class WorldgenProfiler {

	private static int currentProfilingWorld;
	private static boolean enableProfiling;
	private static final HashMap<ProfileKey, GeneratorProfile> profileData = new HashMap();
	private static final ArrayList<GeneratorProfile> profileDataDisplay = new ArrayList();
	private static final HashSet<ChunkCoordIntPair> profiledChunks = new HashSet();
	private static final HashMap<Object, WorldProfilerParent> subGenerators = new HashMap();
	//private static long totalProfiledTime;

	private static final LinkedList<GeneratorProfile> currentlyRunning = new LinkedList();

	public static boolean enableProfiling(World world) { //what about non-IWG time (vanilla & modded WorldGenerators, CC biome smoothing, etc?)
		if (enableProfiling) {
			return false;
		}
		else {
			enableProfiling = true;
			profileData.clear();
			profiledChunks.clear();
			SpillageProfile.instance.reset();
			InitProfile.instance.reset();
			//totalProfiledTime = 0;
			currentProfilingWorld = world.provider.dimensionId;
			EventProfiler.finishProfiling();
			EventProfiler.startProfiling(ReplaceBiomeBlocks.class);
			return true;
		}
	}

	public static void finishProfiling() {
		if (enableProfiling) {
			enableProfiling = false;
			EventProfiler.finishProfiling();
		}
	}

	private static void buildDisplay() {
		profileDataDisplay.clear();
		profileDataDisplay.addAll(profileData.values());
		for (EventProfile e : EventProfiler.getProfilingData()) {
			profileDataDisplay.add(new BiomeBlocksProfile(e));
		}
		profileDataDisplay.add(SpillageProfile.instance);
		profileDataDisplay.add(InitProfile.instance);
		Collections.sort(profileDataDisplay);
	}

	public static List<GeneratorProfile> getProfilingData() {
		buildDisplay();
		return Collections.unmodifiableList(profileDataDisplay);
	}

	public static Collection<ChunkCoordIntPair> getProfiledChunks() {
		return Collections.unmodifiableCollection(profiledChunks);
	}

	public static long getTotalProfilingTime() {
		//return totalProfiledTime;
		long total = 0;
		for (GeneratorProfile g : profileData.values()) {
			//ReikaJavaLibrary.pConsole(total+" + "+g.getTotalTime()+" = "+(total+g.getTotalTime()));
			total += g.getTotalTime();
		}
		return total;
	}

	public static int getWorld() {
		return currentProfilingWorld;
	}

	public static void startChunkInit(IChunkProvider prov, int cx, int cz) {
		if (!enableProfiling)
			return;
		initGenerator(InitProfile.instance, cx, cz);
	}

	public static void finishChunkInit(IChunkProvider prov, int cx, int cz) {
		if (!enableProfiling)
			return;

		long now = System.nanoTime();

		finishGenerator(InitProfile.instance, now, cx, cz);

		//totalProfiledTime += dur;
	}

	public static void startBiomeTerrain(World world, BiomeGenBase b, int x, int z) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(b), x >> 4, z >> 4);
	}

	public static void finishBiomeTerrain(World world, BiomeGenBase b, int x, int z) {
		if (!enableProfiling)
			return;

		long now = System.nanoTime();

		if (world.provider.dimensionId != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(b), now, x >> 4, z >> 4);

		//totalProfiledTime += dur;
	}

	public static void startGenerator(int world, IWorldGenerator gen, int cx, int cz) {
		if (world != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen), cx, cz);
	}

	public static void startGenerator(World world, MapGenBase gen, int cx, int cz) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen), cx, cz);
	}

	public static void startGenerator(World world, WorldGenerator gen, int x, int z) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen), x >> 4, z >> 4);
	}

	public static void startGenerator(World world, WorldProfilerParent gen, int x, int z) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen), x >> 4, z >> 4);
	}

	public static void startGenerator(World world, String id, int cx, int cz) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(id), cx, cz);
	}

	private static void initGenerator(GeneratorProfile a, int cx, int cz) {
		if (!currentlyRunning.isEmpty())
			currentlyRunning.getLast().pause(System.nanoTime());
		currentlyRunning.add(a);
		a.start(cx, cz);
	}

	public static void onRunGenerator(World world, WorldProfilerParent gen, int cx, int cz) {
		long now = System.nanoTime();

		if (world.provider.dimensionId != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(gen), now, cx, cz);

		//totalProfiledTime += dur;
	}

	public static void onRunGenerator(World world, String id, int cx, int cz) {
		long now = System.nanoTime();

		if (world.provider.dimensionId != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(id), now, cx, cz);

		//totalProfiledTime += dur;
	}

	public static void onRunGenerator(World world, WorldGenerator gen, int x, int z) {
		if (!enableProfiling)
			return;

		long now = System.nanoTime();

		if (world.provider.dimensionId != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(gen), now, x >> 4, z >> 4);

		//totalProfiledTime += dur;
	}

	public static void onRunGenerator(World world, MapGenBase gen, int cx, int cz) {
		if (!enableProfiling)
			return;

		long now = System.nanoTime();

		if (world.provider.dimensionId != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(gen), now, cx, cz);

		//totalProfiledTime += dur;
	}

	public static void onRunGenerator(int world, IWorldGenerator gen, int cx, int cz) {
		long now = System.nanoTime();

		if (world != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(gen), now, cx, cz);

		//totalProfiledTime += dur;
	}

	private static void finishGenerator(GeneratorProfile a, long now, int cx, int cz) {
		a.finish(now);

		currentlyRunning.removeLast();
		profiledChunks.add(new ChunkCoordIntPair(cx, cz));
		if (!currentlyRunning.isEmpty()) {
			currentlyRunning.getLast().resume(System.nanoTime()); //not 'now', since that would include some of the above code
		}
	}

	private static GeneratorProfile getOrCreateGenerator(WorldProfilerParent gen) {
		ProfileKey key = new ProfileKey(gen);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new StringIDProfile(gen.getWorldgenProfilerID());
			profileData.put(key, a);
		}
		return a;
	}

	private static GeneratorProfile getOrCreateGenerator(String id) {
		ProfileKey key = new ProfileKey(id);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new StringIDProfile(id);
			profileData.put(key, a);
		}
		return a;
	}

	private static GeneratorProfile getOrCreateGenerator(BiomeGenBase gen) {
		ProfileKey key = new ProfileKey(gen);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new BiomeTerrainProfile(gen);
			profileData.put(key, a);
		}
		return a;
	}

	private static GeneratorProfile getOrCreateGenerator(WorldGenerator gen) {
		ProfileKey key = new ProfileKey(gen);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new WorldGenProfile(gen);
			profileData.put(key, a);
		}
		return a;
	}

	private static GeneratorProfile getOrCreateGenerator(MapGenBase gen) {
		ProfileKey key = new ProfileKey(gen);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new MapGenProfile(gen);
			profileData.put(key, a);
		}
		return a;
	}

	private static GeneratorProfile getOrCreateGenerator(IWorldGenerator gen) {
		ProfileKey key = new ProfileKey(gen);
		GeneratorProfile a = profileData.get(key);
		if (a == null) {
			a = new IWGProfile(gen);
			profileData.put(key, a);
		}
		return a;
	}

	public static void onChunkSpills(IWorldGenerator spiller, int cx, int cz, int cx2, int cz2) {
		long now = System.nanoTime();
		GeneratorProfile a = getOrCreateGenerator(spiller);
		if (a.addSpilledChunk(cx, cz, cx2, cz2))
			initGenerator(SpillageProfile.instance, cx, cz);
	}

	public static void onChunkFinished(int cx, int cz) {
		if (SpillageProfile.instance.isRunning)
			finishGenerator(SpillageProfile.instance, System.nanoTime(), cx, cz);
	}

	@Deprecated
	public static void registerBlockChanges(IWorldGenerator gen, int number) {
		GeneratorProfile a = getOrCreateGenerator(gen);
		a.blockChanges += number;
	}

	/** Used for cases where in the middle of profiling another function is run that does not 'count'. */
	public static void subtractTime(IWorldGenerator gen, long time) {
		GeneratorProfile a = getOrCreateGenerator(gen);
		a.totalTime -= time;
		//totalProfiledTime -= time;
	}

	/** Use this to prevent a subgenerator from showing as its own entry (eg a WorldGenerator object used inside an IWorldGenerator)
	 * so that it is instead merged into its parent. */
	public static void registerGeneratorAsSubGenerator(WorldProfilerParent parent, Object sub) {
		subGenerators.put(sub, parent);
	}

	public static boolean profilingEnabled() {
		return enableProfiling;
	}

	private static class ProfileKey {

		private Class type;
		private Object value;

		private ProfileKey(Object o) {
			if (subGenerators.containsKey(o))
				o = subGenerators.get(o);

			if (o instanceof IWorldGenerator) {
				value = o;
				type = IWorldGenerator.class;
			}
			else if (o instanceof MapGenBase) {
				value = o;
				type = MapGenBase.class;
			}
			else if (o instanceof WorldGenerator) {
				value = WorldGenProfile.calcName((WorldGenerator)o);
				type = WorldGenerator.class;
			}
			else if (o instanceof BiomeGenBase) {
				value = ((BiomeGenBase)o).biomeName;
				type = BiomeGenBase.class;
			}
			else if (o instanceof String) {
				value = o;
				type = String.class;
			}
			else if (o instanceof WorldProfilerParent) {
				value = ((WorldProfilerParent)o).getWorldgenProfilerID();
				type = WorldProfilerParent.class;
			}
		}

		@Override
		public int hashCode() {
			return type.hashCode() ^ value.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof ProfileKey && ((ProfileKey)o).type == type && value.equals(((ProfileKey)o).value);
		}

	}

	private static final class BiomeBlocksProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private final EventProfile reference;

		private BiomeBlocksProfile(EventProfile e) {
			super("BiomeBlockEvent: "+e.identifier);
			reference = e;

			totalTime = e.getTotalTime();
		}

		@Override
		public long getAverageTime() {
			return reference.getAverageTime();
		}
	}

	private static final class WorldGenProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private WorldGenProfile(WorldGenerator gen) {
			super(calcName(gen));
		}

		private static String calcName(WorldGenerator gen) {
			String s = gen.getClass().getName();
			String pre = "WorldGenerator: ";
			if (gen instanceof WorldGenMinable) {
				WorldGenMinable gm = (WorldGenMinable)gen;
				String type = "";
				try {
					Field f = WorldGenMinable.class.getDeclaredField("field_150519_a");
					f.setAccessible(true);
					type = ((Block)f.get(gm)).getLocalizedName();
				}
				catch (Exception e) {
					type = "Error: "+e.toString();
				}
				pre = "Ore Generator: "+type+" ";
			}
			else if (gen instanceof WorldGenLiquids) {
				WorldGenLiquids gl = (WorldGenLiquids)gen;
				String type = "";
				try {
					Field f = WorldGenLiquids.class.getDeclaredField("field_150521_a");
					f.setAccessible(true);
					type = ((Block)f.get(gl)).getLocalizedName();
				}
				catch (Exception e) {
					type = "Error: "+e.toString();
				}
				pre = "Liquid Generator: "+type+" ";
			}
			return pre+s;
		}
	}

	private static final class BiomeTerrainProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private BiomeTerrainProfile(BiomeGenBase gen) {
			super("Biome Terrain "+gen.biomeName);
		}
	}

	private static final class MapGenProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private MapGenProfile(MapGenBase gen) {
			super("MapGen Object: "+gen.getClass().getName());
		}
	}

	private static final class StringIDProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private StringIDProfile(String s) {
			super("Defined Hook: "+s);
		}
	}

	private static final class IWGProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private IWGProfile(IWorldGenerator gen) {
			super("IWG Forge Hook: "+gen.getClass().getName());
		}
	}

	private static final class SpillageProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private static final SpillageProfile instance = new SpillageProfile();

		private SpillageProfile() {
			super("Chunk Spillage");
		}

	}

	private static final class InitProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private static final InitProfile instance = new InitProfile();

		private InitProfile() {
			super("Chunk Initialization");
		}

	}

	public static abstract class GeneratorProfile implements Comparable<GeneratorProfile> {

		public final String identifier;
		//private final RunningAverage average = new RunningAverage();
		protected long totalTime;
		private final MultiMap<ChunkCoordIntPair, ChunkCoordIntPair> spilledChunks = new MultiMap(new HashSetFactory());
		@Deprecated
		private int blockChanges;

		private long lastPauseStart;
		private long totalPause;

		//per-chunk
		private long startTime;
		boolean isRunning;
		private int currentChunkX = Integer.MIN_VALUE;
		private int currentChunkZ = Integer.MIN_VALUE;

		private GeneratorProfile(String s) {
			identifier = s;
		}

		protected void start(int cx, int cz) {
			if (currentChunkX == cx && currentChunkZ == cz)
				return;
			if (isRunning)
				;//throw new IllegalStateException("GeneratorProfile '"+identifier+"' is already running on chunk "+currentChunkX+", "+currentChunkZ+", but was started for chunk "+cx+", "+cz+"!");
			currentChunkX = cx;
			currentChunkZ = cz;
			isRunning = true;
			//DragonAPICore.log("Starting "+identifier);
			startTime = System.nanoTime();
		}

		protected void finish(long time) {
			//DragonAPICore.log("Finishing "+identifier);
			if (!isRunning)
				;//throw new IllegalStateException("GeneratorProfile '"+identifier+"' is not running!");
			isRunning = false;
			currentChunkX = Integer.MIN_VALUE;
			currentChunkZ = Integer.MIN_VALUE;

			long dur = time-startTime;
			this.addValue(dur);
		}

		protected void pause(long time) {
			//DragonAPICore.log("Pausing "+identifier);
			lastPauseStart = time;
			isRunning = false;
		}

		protected void resume(long now) {
			long dur = now-lastPauseStart;
			//DragonAPICore.log("Resuming "+identifier+" after "+dur+" ns");
			totalPause += dur;
			lastPauseStart = -1;
			isRunning = true;
		}

		protected final boolean addSpilledChunk(int cx, int cz, int cx2, int cz2) {
			ChunkCoordIntPair from = new ChunkCoordIntPair(cx, cz);
			ChunkCoordIntPair to = new ChunkCoordIntPair(cx2, cz2);
			if (spilledChunks.addValue(from, to)) {
				//DragonAPICore.log("Generator "+classString+" has spilled from ["+cx+", "+cz+"] into adjacent chunk ["+cx2+", "+cz2+"]!");
				return true;
			}
			return false;
		}

		protected final void addValue(long dur) {
			//average.addValue(dur);
			totalTime += dur;
		}

		public final long getTotalTime() {
			return totalTime-totalPause;
		}

		public long getAverageTime() {
			//return (long)average.getAverage();
			return this.getTotalTime()/profiledChunks.size();
		}

		public final int getSpilledChunks() {
			return spilledChunks.totalSize();
		}

		@Deprecated
		public final int getBlockChanges() {
			return blockChanges;
		}

		@Override
		public final String toString() {
			return identifier+" ("+this.getAverageTime()+" ns / "+this.getTotalTime()+" ns)";
		}

		@Override
		public final int compareTo(GeneratorProfile o) {
			return -Long.compare(this.getTotalTime(), o.getTotalTime()); //negative since most expensive at top
		}

		protected final void reset() {
			totalTime = 0;
			spilledChunks.clear();
			blockChanges = 0;

			lastPauseStart = 0;
			totalPause = 0;

			//per-chunk
			startTime = 0;
			isRunning = false;
			currentChunkX = Integer.MIN_VALUE;
			currentChunkZ = Integer.MIN_VALUE;
		}

	}

	public static interface WorldProfilerParent {

		public String getWorldgenProfilerID();

	}
}