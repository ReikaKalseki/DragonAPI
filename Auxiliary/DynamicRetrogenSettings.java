package Reika.DragonAPI.Auxiliary;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

	private final HashSet<Generator> generators = new HashSet();

	private DynamicRetrogenSettings() {

	}

	public void loadConfig() {
		File f = new File(DragonAPIInit.config.getConfigFolder(), "DragonAPI_Aux_Retrogen.cfg");
		if (f.exists()) {
			this.readConfig(f);
			this.apply();
		}
		else {
			try {
				f.createNewFile();
				this.writeConfig(f);
			}
			catch (IOException e) {
				throw new RuntimeException("Could not create config!");
			}
		}
	}

	private void apply() {
		for (Generator g : generators) {
			RetroGenController.instance.addRetroGenerator(g, g.weight);
		}
	}

	private void readConfig(File f) {
		ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
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
			generators.add(gen);
		}
	}

	private void writeConfig(File f) {
		ArrayList<String> li = new ArrayList();
		Set<IWorldGenerator> set = this.getGeneratorList();
		for (IWorldGenerator gen : set) {
			li.add(this.getEntry(gen)+"=null");
		}
		Collections.sort(li);
		ReikaFileReader.writeLinesToFile(f, li, true);
	}

	private Generator createGenerator(String s, int wt) {
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
		public final int weight;
		private final IWorldGenerator generator;

		private Generator(IWorldGenerator gen, String id, int wt) {
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
