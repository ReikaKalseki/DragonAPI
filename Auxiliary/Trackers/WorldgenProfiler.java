package Reika.DragonAPI.Auxiliary.Trackers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
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
	private static HashMap<ProfileKey, GeneratorProfile> profileData = new HashMap();
	private static ArrayList<GeneratorProfile> profileDataDisplay = new ArrayList();
	private static HashSet<ChunkCoordIntPair> profiledChunks = new HashSet();
	private static HashMap<Object, Object> subGenerators = new HashMap();
	//private static long totalProfiledTime;

	public static boolean enableProfiling(World world) { //what about non-IWG time (vanilla & modded WorldGenerators, CC biome smoothing, etc?)
		if (enableProfiling) {
			return false;
		}
		else {
			enableProfiling = true;
			profileData.clear();
			profiledChunks.clear();
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
			total += g.getTotalTime();
		}
		return total;
	}

	public static int getWorld() {
		return currentProfilingWorld;
	}

	public static void startGenerator(int world, IWorldGenerator gen) {
		if (world != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen));
	}

	public static void startGenerator(World world, WorldGenerator gen) {
		if (!enableProfiling || world.provider.dimensionId != currentProfilingWorld)
			return;
		initGenerator(getOrCreateGenerator(gen));
	}

	private static void initGenerator(GeneratorProfile a) {
		a.spillageTimeStart = -1;
		a.startTime = System.nanoTime();
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

	public static void onRunGenerator(int world, IWorldGenerator gen, int cx, int cz) {
		long now = System.nanoTime();

		if (world != currentProfilingWorld)
			return;

		finishGenerator(getOrCreateGenerator(gen), now, cx, cz);

		//totalProfiledTime += dur;
	}

	private static void finishGenerator(GeneratorProfile a, long now, int cx, int cz) {
		long dur = now-a.startTime;
		long spillage = a.spillageTimeStart >= 0 ? now-a.spillageTimeStart : 0;

		a.addValue(dur);

		profiledChunks.add(new ChunkCoordIntPair(cx, cz));
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
		GeneratorProfile a = getOrCreateGenerator(spiller);
		a.addSpilledChunk(cx, cz, cx2, cz2);
		if (a.spillageTimeStart == -1)
			a.spillageTimeStart = System.nanoTime();
	}

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

	/** Use this to prevent a subgenerator from showing as its own entry, instead being merged into its parent. */
	public static void registerGeneratorAsSubGenerator(Object parent, Object sub) {
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
			else if (o instanceof WorldGenerator) {
				value = WorldGenProfile.calcName((WorldGenerator)o);
				type = WorldGenerator.class;
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

	private static final class IWGProfile extends GeneratorProfile implements Comparable<GeneratorProfile> {

		private IWGProfile(IWorldGenerator gen) {
			super("IWG Forge Hook: "+gen.getClass().getName());
		}
	}

	public static abstract class GeneratorProfile implements Comparable<GeneratorProfile> {

		public final String identifier;
		//private final RunningAverage average = new RunningAverage();
		protected long totalTime;
		private final MultiMap<ChunkCoordIntPair, ChunkCoordIntPair> spilledChunks = new MultiMap(new HashSetFactory());
		private int blockChanges;

		//These are all per-chunk
		private long startTime;
		private long spillageTimeStart = -1;

		private GeneratorProfile(String s) {
			identifier = s;
		}

		protected final void addSpilledChunk(int cx, int cz, int cx2, int cz2) {
			ChunkCoordIntPair from = new ChunkCoordIntPair(cx, cz);
			ChunkCoordIntPair to = new ChunkCoordIntPair(cx2, cz2);
			if (spilledChunks.addValue(from, to))
				;//DragonAPICore.log("Generator "+classString+" has spilled from ["+cx+", "+cz+"] into adjacent chunk ["+cx2+", "+cz2+"]!");
		}

		protected final void addValue(long dur) {
			//average.addValue(dur);
			totalTime += dur;
		}

		public final long getTotalTime() {
			return totalTime;
		}

		public long getAverageTime() {
			//return (long)average.getAverage();
			return totalTime/profiledChunks.size();
		}

		public final int getSpilledChunks() {
			return spilledChunks.totalSize();
		}

		public final int getBlockChanges() {
			return blockChanges;
		}

		@Override
		public final String toString() {
			return identifier+" ("+this.getAverageTime()+" ns / "+this.getTotalTime()+" ns)";
		}

		@Override
		public final int compareTo(GeneratorProfile o) {
			return -Long.compare(totalTime, o.totalTime); //negative since most expensive at top
		}

	}
}