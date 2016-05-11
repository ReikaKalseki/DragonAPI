/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockSpiral;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;


public class ChunkPregenerator implements TickHandler {

	public static final ChunkPregenerator instance = new ChunkPregenerator();

	private final MultiMap<Integer, Pregenerator> generators = new MultiMap();

	private ChunkPregenerator() {

	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		if (world.isRemote)
			return;
		Collection<Pregenerator> c = generators.get(world.provider.dimensionId);
		Iterator<Pregenerator> it = c.iterator();
		while (it.hasNext()) {
			Pregenerator p = it.next();
			if (p.tick(world))
				it.remove();
			return;
		}
	}

	public void addChunks(WorldServer world, double pertick, int ctrX, int ctrZ, int radius) {
		ArrayList<ChunkCoordIntPair> li = this.spiralSort(ctrX, ctrZ, radius);
		generators.addValue(world.provider.dimensionId, new Pregenerator(pertick, li));
	}

	private ArrayList<ChunkCoordIntPair> spiralSort(int ctrX, int ctrZ, int radius) {
		ArrayList<ChunkCoordIntPair> li = new ArrayList();
		BlockSpiral sp = new BlockSpiral(ctrX, 0, ctrZ, radius/16).setGridSize(16).calculate();
		for (int i = 0; i < sp.getSize(); i++) {
			Coordinate c = sp.getNthBlock(i);
			li.add(c.asChunkPair());
		}
		return li;
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "chunkpregen";
	}

	private static final class Pregenerator {

		private final double numberChunksPerTick;
		private final ArrayList<ChunkCoordIntPair> chunks;
		private final int originalSize;
		private int pregenPercent;
		private final ChunkCoordIntPair origin;

		private Pregenerator(double n, ArrayList<ChunkCoordIntPair> li) {
			numberChunksPerTick = n;
			chunks = li;
			originalSize = li.size();
			origin = li.get(0);
		}

		private boolean tick(World world) {
			if (numberChunksPerTick >= 1 || ReikaRandomHelper.doWithChance(numberChunksPerTick)) {
				int n = numberChunksPerTick >= 1 ? (int)numberChunksPerTick : 1;
				for (int i = 0; i < n; i++) {
					ChunkCoordIntPair c = chunks.remove(0);
					this.generate(world, c);
					if (chunks.isEmpty()) {
						return true;
					}
				}
			}
			return false;
		}

		private void generate(World world, ChunkCoordIntPair c) {
			int x = c.chunkXPos << 4;
			int z = c.chunkZPos << 4;
			try {
				ReikaWorldHelper.forceGenAndPopulate(world, x, z);
				double p = 100*(1D-(chunks.size()/(double)originalSize));
				if (p >= 5+pregenPercent) {
					pregenPercent += 5;
					this.notifyAdmins("Pregeneration of area "+origin+" "+String.format("%.3f", p)+" percent complete.");
				}
				//ReikaJavaLibrary.pConsole("Generated chunk @ "+x+","+z+"; "+String.format("%.3f%% complete", p));
			}
			catch (Exception e) {
				DragonAPICore.logError("Tried and failed generating chunk @ "+x+","+z+";");
				e.printStackTrace();
			}
		}

		private void notifyAdmins(String s) {
			for (EntityPlayer ep : ReikaPlayerAPI.getOps()) {
				ReikaChatHelper.sendChatToPlayer(ep, s);
			}
		}

	}

}
