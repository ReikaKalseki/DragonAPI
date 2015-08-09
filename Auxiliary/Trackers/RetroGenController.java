/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameRegistry;

public class RetroGenController {

	public static final RetroGenController instance = new RetroGenController();

	private static final String NBT_TAG = "DRAGONAPI_RETROGEN";

	private static final GenComparator genComparator = new GenComparator();

	private ArrayList<RetroactiveGenerator> retrogens = new ArrayList();
	private HashMap<String, Integer> retrogenOrder = new HashMap();

	private Collection<ChunkData> chunks = new ArrayList();
	private Collection<ChunkData> scheduledAdds = new ArrayList();

	/** To prevent CMEs */
	private boolean allowAdd = true;
	private boolean allowTick = true;

	private RetroGenController() {
		MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.instance.registerTickHandler(new RetrogenTicker());
	}

	/** Adds a retroactive generator to the API's retro-gen registry. Larger numbers run later, much like IWorldGenerator. */
	public void addRetroGenerator(RetroactiveGenerator gen, int weight) {
		retrogens.add(gen);
		retrogenOrder.put(gen.getIDString(), weight);
		Collections.sort(retrogens, genComparator);
	}

	public void addHybridGenerator(RetroactiveGenerator gen, int weight, boolean retro) {
		GameRegistry.registerWorldGenerator(gen, weight);
		if (retro)
			this.addRetroGenerator(gen, weight);
	}

	@SubscribeEvent
	public void loadChunk(ChunkDataEvent.Load event)
	{
		if (retrogens.isEmpty())
			return;
		ChunkData cd = new ChunkData(event);
		//DragonAPIInit.instance.getModLogger().debug("Queueing chunk ["+event.getChunk().xPosition+", "+event.getChunk().zPosition+"] for retrogen.");
		if (allowAdd) {
			allowTick = false;
			chunks.add(cd);
			chunks.addAll(scheduledAdds);
			scheduledAdds.clear();
			allowTick = true;
		}
		else {
			scheduledAdds.add(cd);
		}
	}

	private boolean generate(World world, ChunkCoordIntPair pos, RetroactiveGenerator gen) {
		if (!gen.canGenerateAt(world.rand, world, pos.chunkXPos, pos.chunkZPos))
			return false;
		IChunkProvider prov = world.getChunkProvider();
		IChunkProvider generator = ((ChunkProviderServer)prov).currentChunkProvider;
		gen.generate(world.rand, pos.chunkXPos, pos.chunkZPos, world, generator, prov);
		return true;
	}

	/** Needed because gen on load causes a CME */
	private static class RetrogenTicker implements TickHandler {

		@Override
		public void tick(TickType type, Object... tickData) {
			if (!instance.allowTick)
				return;
			World world = (World)tickData[0];
			instance.allowAdd = false;
			for (ChunkData dat : instance.chunks) {
				boolean genned = false;
				for (RetroactiveGenerator gen : instance.retrogens) {
					if (this.shouldRun(gen, dat)) {
						boolean flag = instance.generate(world, dat.position, gen);
						this.markChunkGenned(dat.data, gen, flag);
						if (flag) {
							//DragonAPIInit.instance.getModLogger().debug("Running retrogen '"+gen.getIDString()+"' at Chunk "+dat.position);
							genned = true;
						}
					}
				}
				if (genned) {
					int dx = dat.position.chunkXPos*16;
					int dz = dat.position.chunkZPos*16;
					//ReikaJavaLibrary.pConsole(instance.retrogens.size()+" gens SET AT "+dx+", "+dz);
					world.setBlock(dx, 128, dz, Blocks.brick_block);
					world.setBlock(dx+15, 128, dz, Blocks.brick_block);
					world.setBlock(dx+15, 128, dz+15, Blocks.brick_block);
					world.setBlock(dx, 128, dz+15, Blocks.brick_block);
				}
			}
			instance.chunks.clear();
			instance.allowAdd = true;
		}

		private boolean shouldRun(RetroactiveGenerator gen, ChunkData dat) {
			return !dat.data.getCompoundTag(NBT_TAG).getBoolean(gen.getIDString());
		}

		private void markChunkGenned(NBTTagCompound data, RetroactiveGenerator gen, boolean flag) {
			NBTTagCompound base = data.getCompoundTag(NBT_TAG);
			base.setBoolean(gen.getIDString(), flag);
			data.setTag(NBT_TAG, base);
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.WORLD);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return "DragonAPI Retrogen";
		}

	}

	private static class GenComparator implements Comparator<RetroactiveGenerator> {

		@Override
		public int compare(RetroactiveGenerator o1, RetroactiveGenerator o2) {
			int n1 = instance.retrogenOrder.get(o1.getIDString());
			int n2 = instance.retrogenOrder.get(o2.getIDString());
			return n1 == n2 ? 0 : n1 < n2 ? -1 : 1;
		}

	}

	private static class ChunkData {

		private final ChunkCoordIntPair position;
		private final NBTTagCompound data;

		private ChunkData(ChunkDataEvent.Load evt) {
			position = new ChunkCoordIntPair(evt.getChunk().xPosition, evt.getChunk().zPosition);
			data = evt.getData();
		}

	}

}
