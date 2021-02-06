/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ExtraUtilsHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;

public final class RayTracer {

	private double originX;
	private double originY;
	private double originZ;
	private double targetX;
	private double targetY;
	private double targetZ;

	public boolean airOnly = false;
	public boolean softBlocksOnly = false;
	public boolean allowFluids = true;
	public boolean uniDirectionalChecks = false;

	public boolean cacheBlockRay = false;

	private final ArrayList<BlockKey> forbiddenBlocks = new ArrayList();
	private final ArrayList<BlockKey> allowedBlocks = new ArrayList();
	private final ArrayList<BlockKey> allowedOneTimeBlocks = new ArrayList();

	private final HashSet<Coordinate> blockRay = new HashSet();

	private static final Collection<BlockKey> visuallyTransparent = new HashSet();
	private static boolean loadedTransparent = false;

	public RayTracer(double x1, double y1, double z1, double x2, double y2, double z2) {
		originX = x1;
		originY = y1;
		originZ = z1;
		targetX = x2;
		targetY = y2;
		targetZ = z2;
	}

	public RayTracer setOrigins(double x1, double y1, double z1, double x2, double y2, double z2) {
		originX = x1;
		originY = y1;
		originZ = z1;
		targetX = x2;
		targetY = y2;
		targetZ = z2;
		blockRay.clear();
		return this;
	}

	public RayTracer offset(double dx, double dy, double dz) {
		return this.offset(dx, dy, dz, dx, dy, dz);
	}

	public RayTracer offset(double dx1, double dy1, double dz1, double dx2, double dy2, double dz2) {
		originX += dx1;
		originY += dy1;
		originZ += dz1;
		targetX += dx2;
		targetY += dy2;
		targetZ += dz2;
		blockRay.clear();
		return this;
	}

	public RayTracer addOpaqueBlock(Block b) {
		return this.addOpaqueBlock(b, -1);
	}

	public RayTracer addOpaqueBlock(Block b, int meta) {
		forbiddenBlocks.add(new BlockKey(b, meta));
		return this;
	}

	public RayTracer addTransparentBlock(Block b) {
		return this.addTransparentBlock(b, -1);
	}

	public RayTracer addTransparentBlock(Block b, int meta) {
		allowedBlocks.add(new BlockKey(b, meta));
		return this;
	}

	public RayTracer addOneTimeIgnoredBlock(Block b) {
		return this.addOneTimeIgnoredBlock(b, -1);
	}

	public RayTracer addOneTimeIgnoredBlock(Block b, int meta) {
		allowedOneTimeBlocks.add(new BlockKey(b, meta));
		return this;
	}

	public boolean isClearLineOfSight(World world) {
		Vec3 vec1 = Vec3.createVectorHelper(originX, originY, originZ);
		Vec3 vec2 = Vec3.createVectorHelper(targetX, targetY, targetZ);
		if (uniDirectionalChecks && new DecimalPosition(vec1).hashCode() < new DecimalPosition(vec2).hashCode()) {
			Vec3 vec = vec1;
			vec1 = vec2;
			vec2 = vec;
		}
		Vec3 ray = ReikaVectorHelper.subtract(vec1, vec2);
		double dx = vec2.xCoord-vec1.xCoord;
		double dy = vec2.yCoord-vec1.yCoord;
		double dz = vec2.zCoord-vec1.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		for (double d = 0.25; d <= dd; d += 0.25) {
			Vec3 vec0 = ReikaVectorHelper.scaleVector(ray, d);
			Vec3 vec = ReikaVectorHelper.scaleVector(ray, d-0.25);
			vec0.xCoord += vec1.xCoord;
			vec0.yCoord += vec1.yCoord;
			vec0.zCoord += vec1.zCoord;
			vec.xCoord += vec1.xCoord;
			vec.yCoord += vec1.yCoord;
			vec.zCoord += vec1.zCoord;

			if (cacheBlockRay) {
				blockRay.add(new Coordinate(vec));
				blockRay.add(new Coordinate(vec0));
			}

			MovingObjectPosition mov = world.rayTraceBlocks(vec, vec0);
			if (mov != null) {
				if (mov.typeOfHit == MovingObjectType.BLOCK) {
					int bx = mov.blockX;
					int by = mov.blockY;
					int bz = mov.blockZ;
					if (this.isNonTerminal(bx, by, bz)) {
						if (this.isDisallowedBlock(world, bx, by, bz)) {
							//ReikaJavaLibrary.pConsole(mov+":"+world.getBlock(bx, by, bz), Side.SERVER);
							allowedOneTimeBlocks.clear();
							return false;
						}
					}
				}
			}
		}
		allowedOneTimeBlocks.clear();
		return true;
	}

	public Set<Coordinate> getRayBlocks() {
		return Collections.unmodifiableSet(blockRay);
	}

	private boolean isNonTerminal(int x, int y, int z) {
		if (x == MathHelper.floor_double(originX) && y == MathHelper.floor_double(originY) && z == MathHelper.floor_double(originZ))
			return false;
		if (x == MathHelper.floor_double(targetX) && y == MathHelper.floor_double(targetY) && z == MathHelper.floor_double(targetZ))
			return false;
		return true;
	}

	private boolean isDisallowedBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		BlockKey key = new BlockKey(b, meta);
		if (airOnly && b != Blocks.air)
			return true;
		if (allowedBlocks.contains(key))
			return false;
		if (allowedOneTimeBlocks.contains(key))
			return false;
		if (forbiddenBlocks.contains(key))
			return true;
		if (!allowFluids && ReikaBlockHelper.isLiquid(b))
			return true;
		return !ReikaWorldHelper.softBlocks(world, x, y, z) || (softBlocksOnly && ReikaBlockHelper.isCollideable(world, x, y, z));
	}

	public boolean isBlockPassable(World world, int x, int y, int z) {
		return !this.isDisallowedBlock(world, x, y, z);
	}

	public static void addVisuallyTransparentBlock(Block b) {
		visuallyTransparent.add(new BlockKey(b));
	}

	public static void addVisuallyTransparentBlock(Block b, int meta) {
		visuallyTransparent.add(new BlockKey(b, meta));
	}

	static {
		addVisuallyTransparentBlock(Blocks.glass);
		addVisuallyTransparentBlock(Blocks.ice);
		addVisuallyTransparentBlock(Blocks.glass_pane);
		addVisuallyTransparentBlock(Blocks.iron_bars);
		addVisuallyTransparentBlock(Blocks.fence);
		addVisuallyTransparentBlock(Blocks.nether_brick_fence);
		addVisuallyTransparentBlock(Blocks.mob_spawner);
		addVisuallyTransparentBlock(Blocks.leaves);
		addVisuallyTransparentBlock(Blocks.leaves2);
		addVisuallyTransparentBlock(Blocks.tallgrass);
	}

	private static void loadLastTransparent() {
		if (loadedTransparent)
			return;
		loadedTransparent = true;
		if (ModList.EXTRAUTILS.isLoaded()) {
			if (ExtraUtilsHandler.getInstance().deco2ID != null) {
				addVisuallyTransparentBlock(ExtraUtilsHandler.getInstance().deco2ID, 1);
				addVisuallyTransparentBlock(ExtraUtilsHandler.getInstance().deco2ID, 2);
				addVisuallyTransparentBlock(ExtraUtilsHandler.getInstance().deco2ID, 4);
			}
		}
		if (ModList.TINKERER.isLoaded() && TinkerBlockHandler.getInstance().clearGlassID != null) {
			addVisuallyTransparentBlock(TinkerBlockHandler.getInstance().clearGlassID);
		}
	}

	public static Collection<BlockKey> getTransparentBlocks() {
		return Collections.unmodifiableCollection(visuallyTransparent);
	}

	public static RayTracer getVisualLOS() {
		RayTracer trace = new RayTracer(0, 0, 0, 0, 0, 0);
		loadLastTransparent();

		for (BlockKey bk : visuallyTransparent) {
			if (bk.hasMetadata())
				trace.addTransparentBlock(bk.blockID, bk.metadata);
			else
				trace.addTransparentBlock(bk.blockID);
		}
		trace.allowFluids = true;

		return trace;
	}

	public static RayTracerWithCache getVisualLOSForRenderCulling() {
		RayTracer ret = getVisualLOS();
		return new RayTracerWithCache(ret);
	}

	public static <V> RayTracerWithCache getMultipointVisualLOSForRenderCulling(MultipointChecker<V> mc) {
		RayTracer ret = getVisualLOS();
		return new MultipointRayTracerWithCache(ret, mc);
	}

	public double getLength() {
		return ReikaMathLibrary.py3d(originX-targetX, originY-targetY, originZ-targetZ);
	}

	private static class MultipointRayTracerWithCache<V> extends RayTracerWithCache<V> {

		private final MultipointChecker<V> checker;

		private MultipointRayTracerWithCache(RayTracer ret, MultipointChecker<V> mc) {
			super(ret);
			checker = mc;
		}

		@Override
		protected boolean getLOS(V focus, World world) {
			return checker.isClearLineOfSight(focus, trace, world);
		}

	}

	public static interface MultipointChecker<V> {

		public boolean isClearLineOfSight(V focus, RayTracer trace, World world);

	}

	public static class RayTracerWithCache<V> {

		protected final RayTracer trace;

		private Boolean cachedRaytrace;
		private long lastTraceTick;
		private int lastTraceTileHash;

		private RayTracerWithCache(RayTracer ret) {
			trace = ret;
		}

		public final void setOrigins(double x1, double y1, double z1, double x2, double y2, double z2) {
			trace.setOrigins(x1, y1, z1, x2, y2, z2);
		}

		public final boolean isClearLineOfSight(Entity e) {
			return this.isClearLineOfSight((V)e, e.worldObj);
		}

		public final boolean isClearLineOfSight(TileEntity e) {
			return this.isClearLineOfSight((V)e, e.worldObj);
		}

		public final boolean isClearLineOfSight(V focus, World world) {
			this.update(focus, world);
			if (cachedRaytrace == null)
				cachedRaytrace = this.getLOS(focus, world);
			return cachedRaytrace.booleanValue();
		}

		protected boolean getLOS(V focus, World world) {
			return trace.isClearLineOfSight(world);
		}
		/*
		public final void update(TileEntity te) {
			this.update(te, te.worldObj);
		}

		public final void update(Entity te) {
			this.update(te, te.worldObj);
		}
		 */
		private final void update(Object focus, World world) {
			if (cachedRaytrace == null)
				return;
			long time = world.getTotalWorldTime();
			int hash = System.identityHashCode(focus);
			if (time-lastTraceTick > 5 || hash != lastTraceTileHash) {
				cachedRaytrace = null;
				lastTraceTileHash = hash;
				lastTraceTick = time;
			}
		}

	}

}
