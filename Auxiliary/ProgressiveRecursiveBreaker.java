/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Collections.RelativePositionList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Block.MachineRegistryBlock;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ProgressiveRecursiveBreaker implements TickHandler {

	public static final ProgressiveRecursiveBreaker instance = new ProgressiveRecursiveBreaker();

	private static final int MAX_DEPTH = 4;
	private static final int MAX_SIZE = 32000;
	private static final ForgeDirection[] dirs = ForgeDirection.values();
	private final MultiMap<Integer, ProgressiveBreaker> breakers = new MultiMap();

	public static final class ProgressiveBreaker {
		private final BlockArray start = new BlockArray();
		private final World world;
		private final int maxDepth;
		private int depth = 0;
		private boolean isDone = false;
		private final HashSet<BlockKey> ids = new HashSet();
		public boolean extraSpread = false;
		public int tickRate = 1;
		private int tick;
		public int fortune = 0;
		public boolean silkTouch = false;
		public boolean drops = true;
		public IInventory dropInventory = null;
		public EntityPlayer player;
		public BlockBox bounds = BlockBox.infinity();
		public BreakerCallback call;
		public boolean isOmni = false;
		private boolean isBlacklist = false;
		public boolean pathTracking = false;
		public boolean dropFluids = true;
		private final Collection<Coordinate> path = new HashSet();
		public boolean taxiCabDistance = false;
		//public final BlockMap<BlockKey> looseMatches = new BlockMap();

		private ProgressiveBreaker(World world, int x, int y, int z, int depth, List<BlockKey> ids) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			this.ids.addAll(ids);
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int depth, BlockKey... ids) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			for (int i = 0; i < ids.length; i++) {
				this.ids.add(ids[i]);
			}
		}

		private ProgressiveBreaker(World world, int x, int y, int z, Block id, int depth) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			for (int i = 0; i < 16; i++)
				ids.add(new BlockKey(id, i));
		}

		private ProgressiveBreaker(World world, int x, int y, int z, Block id, int meta, int depth) {
			this.world = world;
			start.addBlockCoordinate(x, y, z);
			maxDepth = depth;
			ids.add(new BlockKey(id, meta));
		}

		private ProgressiveBreaker(World world, int x, int y, int z, int depth) {
			this(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), depth);
		}

		public void addBlock(BlockKey bk) {
			ids.add(bk);
		}

		public void setBlacklist(BlockKey... keys) {
			ids.clear();
			isBlacklist = true;
			for (int i = 0; i < keys.length; i++) {
				ids.add(keys[i]);
			}
		}

		private void tick() {
			tick++;
			if (tick < tickRate)
				return;
			tick = 0;
			if (depth < maxDepth) {
				BlockArray next = new BlockArray();
				for (int i = 0; i < start.getSize() && !isDone; i++) {
					Coordinate c = start.getNthBlock(i);
					int x = c.xCoord;
					int y = c.yCoord;
					int z = c.zCoord;
					Block b = world.getBlock(x, y, z);
					if (b == Blocks.air)
						continue;
					int meta = world.getBlockMetadata(x, y, z);
					if (call != null && !call.canBreak(this, world, x, y, z, b, meta))
						continue;
					for (int k = 0; k < 6; k++) {
						ForgeDirection dir = dirs[k];
						int dx = x+dir.offsetX;
						int dy = y+dir.offsetY;
						int dz = z+dir.offsetZ;
						if (this.canSpreadTo(world, dx, dy, dz)) {
							next.addBlockCoordinate(dx, dy, dz);
						}
					}
					if (extraSpread) {
						for (int n = 0; n < RelativePositionList.cornerDirections.getSize(); n++) {
							Coordinate d = RelativePositionList.cornerDirections.getNthPosition(x, y, z, n);
							int dx = d.xCoord;
							int dy = d.yCoord;
							int dz = d.zCoord;
							if (this.canSpreadTo(world, dx, dy, dz)) {
								next.addBlockCoordinate(dx, dy, dz);
							}
						}
					}
					if (pathTracking)
						path.add(new Coordinate(x, y, z));
					this.dropBlock(world, x, y, z);
				}
				start.clear();
				for (int i = 0; i < next.getSize() && i < MAX_SIZE; i++) {
					Coordinate c = next.getNthBlock(i);
					int x = c.xCoord;
					int y = c.yCoord;
					int z = c.zCoord;
					start.addBlockCoordinate(x, y, z);
				}
				depth++;
				if (start.isEmpty())
					this.finish();
			}
			else {
				this.finish();
			}
		}

		private void finish() {
			isDone = true;
			if (call != null) {
				call.onFinish(this);
			}
		}

		public void terminate() {
			this.finish();
		}

		private boolean canSpreadTo(World world, int x, int y, int z) {
			if (pathTracking && path.contains(new Coordinate(x, y, z)))
				return false;
			if (!bounds.isBlockInside(x, y, z))
				return false;
			Block id = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (id == Blocks.air)
				return false;
			if (!isOmni) {
				if (!ids.contains(new BlockKey(id, meta)))
					return false;
			}
			return player == null || (!world.isRemote && ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)player));
		}

		private void dropBlock(World world, int x, int y, int z) {
			Block id = world.getBlock(x, y, z);
			if (id == Blocks.air)
				return;
			int meta = world.getBlockMetadata(x, y, z);
			if (drops) {
				ArrayList<ItemStack> drops = new ArrayList();
				if (id instanceof BlockTieredResource) {
					BlockTieredResource bt = (BlockTieredResource)id;
					if (player != null) {
						if (bt.isPlayerSufficientTier(world, x, y, z, player)) {
							drops.addAll(bt.getHarvestResources(world, x, y, z, fortune, player));
						}
						else {
							drops.addAll(bt.getNoHarvestResources(world, x, y, z, fortune, player));
						}
					}
				}
				else if (id instanceof MachineRegistryBlock) {
					drops.add(((MachineRegistryBlock)id).getMachine(world, x, y, z).getCraftedProduct());
				}
				else {
					if (silkTouch && ReikaBlockHelper.attemptSilkTouch(world, x, y, z) && id.canSilkHarvest(world, player, x, y, z, meta))
						drops.add(ReikaBlockHelper.getSilkTouch(world, x, y, z, id, meta, player, dropFluids));
					else
						drops.addAll(ReikaWorldHelper.getDropsAt(world, x, y, z, fortune, player));
				}
				for (ItemStack is : drops) {
					boolean flag = false;
					if (dropInventory != null) {
						if (dropInventory instanceof InventoryPlayer) {
							if (MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(((InventoryPlayer)dropInventory).player, new EntityItem(world, x+0.5, y+0.5, z+0.5, is)))) {
								continue;
							}
						}
						flag = ReikaInventoryHelper.addToIInv(is, dropInventory);
					}
					if (!flag) {
						ReikaItemHelper.dropItem(world, x+0.5, y+0.5, z+0.5, is);
					}
				}
			}
			if (ReikaBlockHelper.isLiquid(id)) {
				if (id.getMaterial() == Material.water) {
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "game.neutral.swim");
				}
				else {
					ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "mob.ghast.fireball");
				}
			}
			else {
				ReikaSoundHelper.playBreakSound(world, x, y, z, id);
			}
			world.setBlockToAir(x, y, z);
			world.markBlockForUpdate(x, y, z);
			if (call != null)
				call.onBreak(this, world, x, y, z, id, meta);
		}
	}

	public static interface BreakerCallback {

		public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta);
		public void onBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta);
		public void onFinish(ProgressiveBreaker b);

	}

	private ProgressiveRecursiveBreaker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void unloadWorld(WorldEvent.Unload evt) {
		//breakers.clear();
	}

	public void addCoordinate(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		this.addCoordinate(world, x, y, z, Integer.MAX_VALUE);
	}

	public void addCoordinate(World world, ProgressiveBreaker b) {
		if (world.isRemote)
			return;
		breakers.addValue(world.provider.dimensionId, b);
	}

	public void addCoordinate(World world, int x, int y, int z, TreeType tree) {
		if (world.isRemote)
			return;
		ProgressiveBreaker b = this.getTreeBreaker(world, x, y, z, tree);
		breakers.addValue(world.provider.dimensionId, b);
	}

	public ProgressiveBreaker getTreeBreaker(World world, int x, int y, int z, TreeType tree) {
		if (world.isRemote)
			return null;
		Block log = tree.getLogID();
		Block leaf = tree.getLeafID();
		List<Integer> logmetas = tree.getLogMetadatas();
		List<Integer> leafmetas = tree.getLeafMetadatas();
		ArrayList<BlockKey> ids = new ArrayList();
		for (int i = 0; i < logmetas.size(); i++) {
			ids.add(new BlockKey(log, logmetas.get(i)));
		}
		for (int i = 0; i < leafmetas.size(); i++) {
			ids.add(new BlockKey(leaf, leafmetas.get(i)));
		}
		int depth = 30;
		if (tree == ModWoodList.SEQUOIA)
			depth = 350;
		if (tree == ModWoodList.TWILIGHTOAK)
			depth = 200;
		if (tree == ModWoodList.DARKWOOD)
			depth = 32;
		ProgressiveBreaker b = new ProgressiveBreaker(world, x, y, z, depth, ids);
		b.extraSpread = true;
		return b;
	}

	public void addCoordinate(World world, int x, int y, int z, List<BlockKey> ids) {
		if (world.isRemote)
			return;
		breakers.addValue(world.provider.dimensionId, new ProgressiveBreaker(world, x, y, z, Integer.MAX_VALUE, ids));
	}

	public void addCoordinate(World world, int x, int y, int z, int maxDepth) {
		if (world.isRemote)
			return;
		breakers.addValue(world.provider.dimensionId, new ProgressiveBreaker(world, x, y, z, maxDepth));
	}

	public ProgressiveBreaker addCoordinateWithReturn(World world, int x, int y, int z, int maxDepth) {
		if (world.isRemote)
			return null;
		ProgressiveBreaker b = new ProgressiveBreaker(world, x, y, z, maxDepth);
		breakers.addValue(world.provider.dimensionId, b);
		return b;
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		World world = (World)tickData[0];
		Collection<ProgressiveBreaker> li = breakers.get(world.provider.dimensionId);
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
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Progressive Recursive Breaker";
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.START;
	}

}
