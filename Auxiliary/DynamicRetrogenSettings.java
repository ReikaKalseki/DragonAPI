package Reika.DragonAPI.Auxiliary;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Charsets;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;



public class DynamicRetrogenSettings {

	public static final DynamicRetrogenSettings instance = new DynamicRetrogenSettings();

	private final HashMap<String, Generator> generators = new HashMap();

	private DynamicRetrogenSettings() {

	}

	public void loadConfig() {
		File f = new File(DragonAPIInit.config.getConfigFolder(), "DragonAPI_Aux_Retrogen.cfg");
		Set<IWorldGenerator> set = this.getGeneratorList();
		for (IWorldGenerator gen : set) {
			String s = this.getEntry(gen);
			Generator g = this.createGenerator(s, null);
			generators.put(s, g);
		}
		if (f.exists()) {
			this.readConfig(f);
		}
		try {
			if (!f.exists())
				f.createNewFile();
			this.writeConfig(f);
		}
		catch (IOException e) {
			throw new RuntimeException("Could not create config!");
		}
		this.apply();
	}

	private void apply() {
		for (Generator g : generators.values()) {
			if (g.weight != null)
				RetroGenController.instance.addRetroGenerator(g, g.weight.intValue());
		}
	}

	private void readConfig(File f) {
		List<String> li = ReikaFileReader.getFileAsLines(f, true, Charsets.UTF_8);
		for (String s : li) {
			if (s.charAt(0) == '#' || s.startsWith("--") || s.startsWith("//"))
				continue;
			int idx = s.indexOf('=');
			if (idx == -1) {
				DragonAPICore.logError("Unreadable line in retrogen config: '"+s+"'");
			}
			String[] parts = s.split("=");
			if (parts[1].equalsIgnoreCase("null"))
				continue;
			int wt = 0;
			try {
				wt = Integer.parseInt(parts[1]);
			}
			catch (NumberFormatException e) {
				throw new InstallationException(DragonAPIInit.instance, "Invalid weight '"+parts[1]+"' for generator '"+parts[0]+"'!");
			}
			if (RetroGenController.instance.getActiveRetroGenerators().contains(parts[0])) {
				throw new InstallationException(DragonAPIInit.instance, "Retrogen for generator '"+parts[0]+"' already active!");
			}
			Generator gen = this.createGenerator(parts[0], wt);
			if (gen == null) {
				throw new InstallationException(DragonAPIInit.instance, "Generator '"+parts[0]+"' not found!");
			}
			DragonAPICore.log("Registering retrogen for "+gen.identifier);
			generators.put(parts[0], gen);
		}
	}

	private void writeConfig(File f) {
		ArrayList<String> li = new ArrayList();
		for (Generator g : generators.values()) {
			li.add(g.identifier+"="+g.weight);
		}
		Collections.sort(li);
		ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
	}

	private Generator createGenerator(String s, Integer wt) {
		Set<IWorldGenerator> set = this.getGeneratorList();
		for (IWorldGenerator gen : set) {
			String look = this.getEntry(gen);
			if (look.equals(s))
				return new Generator(gen, s, wt);
		}
		return null;
	}

	private String getEntry(IWorldGenerator gen) {
		if (gen instanceof RetroactiveGenerator) {
			RetroactiveGenerator rg = (RetroactiveGenerator)gen;
			return rg.getIDString();
		}
		else {
			return gen.getClass().getName();
		}
	}

	private Set<IWorldGenerator> getGeneratorList() {
		try {
			Field f = GameRegistry.class.getDeclaredField("worldGenerators");
			f.setAccessible(true);
			return (Set<IWorldGenerator>)f.get(null);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not access generator list!");
		}
	}

	private static class Generator implements RetroactiveGenerator {

		public final String identifier;
		public final Integer weight;
		private final IWorldGenerator generator;

		private Generator(IWorldGenerator gen, String id, Integer wt) {
			generator = gen;
			identifier = id;
			weight = wt;
		}

		@Override
		public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
			generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}

		@Override
		public boolean canGenerateAt(World world, int chunkX, int chunkZ) {
			return generator instanceof RetroactiveGenerator ? ((RetroactiveGenerator)generator).canGenerateAt(world, chunkX, chunkZ) : true;
		}

		@Override
		public String getIDString() {
			return identifier;
		}

	}
}
