/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Interfaces.TreeType;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ProgressiveRecursiveBreaker implements ITickHandler {

	public static final ProgressiveRecursiveBreaker instance = new ProgressiveRecursiveBreaker();

	private static final int MAX_DEPTH = 4;
	private static final int MAX_SIZE = 32000;
	private static final ForgeDirection[] dirs = ForgeDirection.values();
	private final HashMap<Integer, ArrayList<ProgressiveBreaker>> breakers = new HashMap(); //IS CURRENTLY PERSISTENT BETWEEN WORLDS!

	public static final class ProgressiveBreaker {
		private final BlockArray start = new BlockArray();
		private final World world;
		private final int maxDepth;
		private int depth = 0;
		private boolean isDone = false;
		private final ArrayList<List<Integer>> ids = new ArrayList();
		public boolean extraSpread = false;
		public int tickRate = 1;
		private int tick;
		public int fortune;
		public boolean silkTouch;
		private static final ArrayList<int[]> extraDirs;

		static {
			extraDirs = new ArrayList();
			extraDirs.add(new int[]{-1, -1, -1});
			extraDirs.add(new int[]{-1, -1, 0});
			extraDirs.add(new int[]{-1, -1, 1});
			extraDirs.add(new int[]{-1, 0, -1});
			//extraDirs.add(new int[]{-1, 0, 0});
			extraDirs.add(new int[]{-1, 0, 1});
			extraDirs.add(new int[]{-1, 1, -1});
			extraDirs.add(new int[]{-1, 1, 0});
			extraDirs.add(new int[]{-1, 1, 1});

			extraDirs.add(new int[]{0, -1, -1});
			//extraDirs.add(new int[]{0, -1, 0});
			extraDirs.add(new int[]{0, -1, 1});
			//extraDirs.add(new int[]{0, 0, -1});
			//extraDirs.add(new int[]{0, 0, 0});
			//extraDirs.add(new int[]{0, 0, 1});
			extraDirs.add(new int[]{0, 1, -1});
			//extraDirs.add(new int[]{0, 1, 0});
			extraDirs.add(new int[]{0, 1, 1});

			extraDirs.add(new int[]{1, -1, -1});
			extraDirs.add(new int[]{1, -1, 0});
			extraDirs.add(new int[]{1, -1, 1});
			extraDirs.add(new int[]{1, 0, -1});
			//extraDirs.add(new int[]{1, 0, 0});
			extraDirs.add(new int[]{1, 0, 1});
			extraDirs.add(new int[]{1, 1, -1});
			extraDirs.add(new int[]{1, 1, 0});
			extraDirs.add(new int[]{1, 1, 1});
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int depth, List<List<Integer>> ids) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			for (int i = 0; i < ids.size(); i++) {
				List<Integer> a = ids.get(i);
				this.ids.add(Arrays.asList(a.get(0), a.get(1)));
			}
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int depth, int[]... ids) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			for (int i = 0; i < ids.length; i++) {
				int[] a = ids[i];
				this.ids.add(Arrays.asList(a[0], a[1]));
			}
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int id, int depth) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			for (int i = 0; i < 16; i++)
				ids.add(Arrays.asList(id, i));
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int id, int meta, int depth) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			ids.add(Arrays.asList(id, meta));
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int depth) {
			this(world, x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z), depth);
		}

		private void tick() {
			tick++;
			if (tick < tickRate)
				return;
			tick = 0;
			if (depth < maxDepth) {
				BlockArray next = new BlockArray();
				for (int i = 0; i < start.getSize(); i++) {
					int[] xyz = start.getNthBlock(i);
					int x = xyz[0];
					int y = xyz[1];
					int z = xyz[2];
					int id = world.getBlockId(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);
					for (int k = 0; k < 6; k++) {
						ForgeDirection dir = dirs[k];
						int dx = x+dir.offsetX;
						int dy = y+dir.offsetY;
						int dz = z+dir.offsetZ;
						int id2 = world.getBlockId(dx, dy, dz);
						int meta2 = world.getBlockMetadata(dx, dy, dz);
						if (id2 != 0 && ids.contains(Arrays.asList(id2, meta2))) {
							next.addBlockCoordinate(dx, dy, dz);
						}
					}
					if (extraSpread) {
						for (int n = 0; n < extraDirs.size(); n++) {
							int[] d = extraDirs.get(n);
							int dx = x+d[0];
							int dy = y+d[1];
							int dz = z+d[2];
							int id2 = world.getBlockId(dx, dy, dz);
							int meta2 = world.getBlockMetadata(dx, dy, dz);
							if (id2 != 0 && ids.contains(Arrays.asList(id2, meta2))) {
								next.addBlockCoordinate(dx, dy, dz);
							}
						}
					}
					this.dropBlock(world, x, y, z);
				}
				start.clear();
				for (int i = 0; i < next.getSize() && i < MAX_SIZE; i++) {
					int[] xyz = next.getNthBlock(i);
					int x = xyz[0];
					int y = xyz[1];
					int z = xyz[2];
					start.addBlockCoordinate(x, y, z);
				}
				depth++;
				if (start.isEmpty())
					isDone = true;
			}
			else {
				isDone = true;
			}
		}

		private void dropBlock(World world, int x, int y, int z) {
			int id = world.getBlockId(x, y, z);
			if (silkTouch)
				ReikaItemHelper.dropItem(world, x, y, z, new ItemStack(id, 1, world.getBlockMetadata(x, y, z)));
			else
				ReikaWorldHelper.dropBlockAt(world, x, y, z, fortune);
			world.setBlock(x, y, z, 0);
			ReikaSoundHelper.playBreakSound(world, x, y, z, id);
			world.markBlockForUpdate(x, y, z);
		}
	}

	private ProgressiveRecursiveBreaker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void unloadWorld(WorldEvent.Unload evt) {
		//breakers.clear();
	}

	public void addCoordinate(World world, int x, int y, int z) {
		this.addCoordinate(world, x, y, z, Integer.MAX_VALUE);
	}

	public void addCoordinate(World world, int x, int y, int z, TreeType tree, int fortune, boolean silk) {
		int log = tree.getLogID();
		int leaf = tree.getLeafID();
		List<Integer> logmetas = tree.getLogMetadatas();
		List<Integer> leafmetas = tree.getLeafMetadatas();
		ArrayList<List<Integer>> ids = new ArrayList();
		for (int i = 0; i < logmetas.size(); i++) {
			ids.add(Arrays.asList(log, logmetas.get(i)));
		}
		for (int i = 0; i < leafmetas.size(); i++) {
			ids.add(Arrays.asList(leaf, leafmetas.get(i)));
		}
		//if (ModList.DYETREES.isLoaded()) {
		//	int id = TreeGetter.getNaturalDyeLeafID();
		//	for (int i = 0; i < 16; i++) {
		//		ids.add(Arrays.asList(id, i));
		//	}
		//	//ids.add(Arrays.asList(TreeGetter.getRainbowLeafID(), 0)); //LAG
		//}
		ArrayList<ProgressiveBreaker> li = this.getOrCreateList(world);
		int depth = 30;
		if (tree == ModWoodList.SEQUOIA)
			depth = 350;
		if (tree == ModWoodList.TWILIGHTOAK)
			depth = 200;
		if (tree == ModWoodList.DARKWOOD)
			depth = 32;
		ProgressiveBreaker b = new ProgressiveBreaker(world, x, y, z, depth, ids);
		b.extraSpread = true;
		b.fortune = fortune;
		b.silkTouch = silk;
		li.add(b);
	}

	public void addCoordinate(World world, int x, int y, int z, List<List<Integer>> ids) {
		ArrayList<ProgressiveBreaker> b = this.getOrCreateList(world);
		b.add(new ProgressiveBreaker(world, x, y, z, Integer.MAX_VALUE, ids));
	}

	public void addCoordinate(World world, int x, int y, int z, int maxDepth) {
		ArrayList<ProgressiveBreaker> li = this.getOrCreateList(world);
		li.add(new ProgressiveBreaker(world, x, y, z, maxDepth));
	}

	private ArrayList<ProgressiveBreaker> getOrCreateList(World world) {
		ArrayList<ProgressiveBreaker> li = breakers.get(world.provider.dimensionId);
		if (li == null) {
			li = new ArrayList();
			breakers.put(world.provider.dimensionId, li);
		}
		return li;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = (World)tickData[0];
		ArrayList<ProgressiveBreaker> li = breakers.get(world.provider.dimensionId);
		if (li != null) {
			if (!world.isRemote) {
				Iterator<ProgressiveBreaker> it = li.iterator();
				while (it.hasNext()) {
					ProgressiveBreaker b = it.next();
					if (b.isDone) {
						it.remove();
					}
					else {
						b.tick();
					}
				}
			}
			else {
				li.clear();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Progressive Recursive Breaker";
	}

}
