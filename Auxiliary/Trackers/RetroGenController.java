/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.ResettableRandom;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Event.ChunkGenerationEvent;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class RetroGenController {

	public static final RetroGenController instance = new RetroGenController();

	private final HashSet<Integer> worldExclusions = new HashSet();
	private final HashMap<String, GeneratorEntry> retrogens = new HashMap();
	private final HashMap<Integer, DataCache> worldData = new HashMap();

	private RetroGenController() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/** Adds a retroactive generator to the API's retro-gen registry. Larger numbers run later, much like IWorldGenerator. */
	public void addRetroGenerator(RetroactiveGenerator gen, int weight) {
		GeneratorEntry e = new GeneratorEntry(gen, weight);
		retrogens.put(e.id, e);
	}

	public void addHybridGenerator(RetroactiveGenerator gen, int weight, boolean retro) {
		GameRegistry.registerWorldGenerator(gen, weight);
		if (retro)
			this.addRetroGenerator(gen, weight);
	}

	public Set<String> getActiveRetroGenerators() {
		return Collections.unmodifiableSet(retrogens.keySet());
	}

	public void excludeWorld(int dim) {
		worldExclusions.add(dim);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load evt) {
		for (GeneratorEntry e : retrogens.values())
			this.loadFile(evt.world, e);
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save evt) {
		for (GeneratorEntry e : retrogens.values())
			this.updateFile(evt.world, e);
	}

	@SubscribeEvent
	public void generateChunk(ChunkGenerationEvent event) {
		Chunk c = event.getChunk();
		DataCache cache = this.getOrCreateCache(c.worldObj);
		if (cache == null)
			return;
		for (GeneratorEntry e : retrogens.values()) {
			cache.generatedChunks.addValue(e.id, new ChunkCoordIntPair(c.xPosition, c.zPosition));
		}
	}

	@SubscribeEvent
	public void loadChunk(ChunkEvent.Load event) {
		if (retrogens.isEmpty())
			return;
		Chunk c = event.getChunk();
		World world = c.worldObj;
		if (world.isRemote)
			return;
		if (worldExclusions.contains(world.provider.dimensionId))
			return;
		DataCache cache = this.getOrCreateCache(world);
		ChunkCoordIntPair p = new ChunkCoordIntPair(c.xPosition, c.zPosition);
		ArrayList<GeneratorEntry> toGen = new ArrayList();
		for (String s : retrogens.keySet()) {
			GeneratorEntry e = retrogens.get(s);
			Collection<ChunkCoordIntPair> exclude = cache.generatedChunks.get(s);
			if (exclude.contains(p))
				continue;
			//ReikaJavaLibrary.pConsole(p);
			if (!e.generator.canGenerateAt(world, c.xPosition, c.zPosition))
				continue;
			toGen.add(e);
		}
		if (toGen.isEmpty())
			return;
		Collections.sort(toGen);
		IChunkProvider loader = world.getChunkProvider();
		IChunkProvider gen = ((ChunkProviderServer)loader).currentChunkProvider;
		ResettableRandom rand = ReikaWorldHelper.getModdedGeneratorChunkRand(c.xPosition, c.zPosition, world);
		for (GeneratorEntry e : toGen) {
			e.generator.generate(rand, c.xPosition, c.zPosition, world, gen, loader);
			rand.resetSeed();
			cache.generatedChunks.addValue(e.id, p);
		}
	}

	private File getFile(World world, GeneratorEntry e) {
		File f = world.getSaveHandler().getWorldDirectory();
		if (f != null) {
			return new File(f, "/DragonAPI_Retrogen/DIM"+world.provider.dimensionId+"/"+e.id+".dat");
		}
		return null;
	}

	private void loadFile(World world, GeneratorEntry e) {
		DataCache cache = this.getOrCreateCache(world);
		if (cache == null)
			return;
		//generatedChunks.remove(e.id); DO NOT CLEAR
		File f = this.getFile(world, e);
		if (f != null && f.exists()) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			for (String s : li) {
				ChunkCoordIntPair p = this.parseCoordPair(s);
				cache.generatedChunks.addValue(e.id, p);
			}
		}
	}

	private void updateFile(World world, GeneratorEntry e) {
		DataCache cache = this.getOrCreateCache(world);
		if (cache == null)
			return;
		File f = this.getFile(world, e);
		if (f == null)
			return;
		try {
			f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		ArrayList<String> li = new ArrayList();
		for (ChunkCoordIntPair p : cache.generatedChunks.get(e.id)) {
			li.add(this.toString(p));
		}
		ReikaFileReader.writeLinesToFile(f, li, true);
	}

	private DataCache getOrCreateCache(World world) {
		if (world.isRemote)
			return null;
		DataCache cache = worldData.get(world.provider.dimensionId);
		if (cache == null || world.getSeed() != cache.worldSeed) {
			cache = new DataCache(world);
			worldData.put(world.provider.dimensionId, cache);
		}
		return cache;
	}

	private ChunkCoordIntPair parseCoordPair(String s) {
		String[] parts = s.split(":");
		return new ChunkCoordIntPair(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	private String toString(ChunkCoordIntPair p) {
		return p.chunkXPos+":"+p.chunkZPos;
	}

	private static class GeneratorEntry implements Comparable<GeneratorEntry> {

		private final String id;
		private final RetroactiveGenerator generator;
		private final int weight;

		private GeneratorEntry(RetroactiveGenerator gen, int w) {
			id = gen.getIDString();
			generator = gen;
			weight = w;
		}

		private boolean regenerate(World world, ChunkCoordIntPair pos, IChunkProvider gen, IChunkProvider loader) {

			generator.generate(world.rand, pos.chunkXPos, pos.chunkZPos, world, gen, loader);
			return true;
		}

		@Override
		public int compareTo(GeneratorEntry o) {
			return Integer.compare(weight, o.weight);
		}

		@Override
		public String toString() {
			return id+" @ "+weight+": "+generator.toString();
		}

	}

	private static class DataCache {

		private final int dimensionID;
		private final long worldSeed;
		private final MultiMap<String, ChunkCoordIntPair> generatedChunks = new MultiMap(CollectionType.HASHSET);

		private DataCache(World world) {
			dimensionID = world.provider.dimensionId;
			worldSeed = world.getSeed();
		}

		@Override
		public String toString() {
			return dimensionID+" in "+worldSeed+": "+generatedChunks;
		}
	}

}
