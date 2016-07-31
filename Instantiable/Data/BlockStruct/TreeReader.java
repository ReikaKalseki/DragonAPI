/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.TreeGetter;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public final class TreeReader extends BlockArray {

	private int leafCount;
	private int logCount;

	private TreeType tree;
	private ReikaDyeHelper dyeTree;

	private final Block dyeLeafID;
	private final Block rainbowLeafID;
	private final Block rainbowSaplingID;
	private final Block dyeSaplingID;

	private boolean isDyeTree = false;
	private boolean isRainbowTree = false;
	private int dyeMeta = -1;

	public TreeReader() {
		super();
		if (ModList.CHROMATICRAFT.isLoaded()) {
			dyeLeafID = TreeGetter.getNaturalDyeLeafID();
			rainbowLeafID = TreeGetter.getRainbowLeafID();
			rainbowSaplingID = TreeGetter.getRainbowSaplingID();
			dyeSaplingID = TreeGetter.getSaplingID();
		}
		else {
			dyeLeafID = null;
			rainbowLeafID = null;
			rainbowSaplingID = null;
			dyeSaplingID = null;
		}
	}

	public boolean isDyeTree() {
		return isDyeTree;
	}

	public boolean isRainbowTree() {
		return isRainbowTree;
	}

	public int getDyeTreeMeta() {
		return dyeMeta;
	}

	public void addTree(World world, int x, int y, int z) {
		// Using LinkedLists over ArrayLists. we expect those lists to grow fast
		// and we don't need indexed access.
		HashSet<Coordinate> search = new HashSet();
		HashSet<Coordinate> failed = new HashSet();
		HashSet<Coordinate> next = new HashSet();

		int iterations = 0;

		this.validateAndAdd(world, x, y, z, search, failed);

		while (!search.isEmpty() && iterations < maxDepth) {
			iterations++;

			Iterator<Coordinate> it = search.iterator();
			while (it.hasNext()) {
				Coordinate c = it.next();
				this.addBlockCoordinate(c.xCoord, c.yCoord, c.zCoord);

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						for (int dz = -1; dz <= 1; dz++) {
							if (dx == 0 && dy == 0 && dz == 0)
								continue;

							Coordinate c2 = c.offset(dx, dy, dz);
							if (!search.contains(c2) && !next.contains(c2) && !this.containsKey(c2) && !failed.contains(c2)) {
								this.validateAndAdd(world, c2.xCoord, c2.yCoord, c2.zCoord, next, failed);
							}
						}
					}
				}
				it.remove();
			}

			search.addAll(next);
			next.clear();
		}
	}

	private boolean isTree(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (tree != null && tree.getLogID() == b) {
			logCount++;
			return true;
		}
		else if (!isRainbowTree && b == dyeLeafID && (dyeMeta == -1 || dyeMeta == meta)) {
			dyeMeta = meta;
			isDyeTree = true;
			leafCount++;
			maxDepth = 12;
			return true;
		}
		else if (!isDyeTree && b == rainbowLeafID) {
			isRainbowTree = true;
			leafCount++;
			maxDepth = 36;
			return true;
		}
		else if (tree != null && !isDyeTree && !isRainbowTree && b == tree.getLeafID() && tree.getLeafMetadatas().contains(meta)) {
			leafCount++;
			return true;
		}
		return false;
	}

	private void validateAndAdd(World world, int x, int y, int z, HashSet<Coordinate> search, HashSet<Coordinate> failed) {
		Coordinate c = new Coordinate(x, y, z);
		if (this.isTree(world, x, y, z)) {
			search.add(c);
		}
		else {
			failed.add(c);
		}
	}

	public int getNumberLeaves() {
		return leafCount;
	}

	public int getNumberLogs() {
		return logCount;
	}

	public void reset() {
		logCount = 0;
		leafCount = 0;
		tree = null;
		isDyeTree = false;
		dyeMeta = -1;
	}

	public void setTree(TreeType tree) {
		this.tree = tree;
		if (tree != null)
			maxDepth = this.getMaxDepthFromTreeType(tree);
	}

	public static int getMaxDepthFromTreeType(TreeType tree) {
		if (tree instanceof ReikaTreeHelper) {
			switch((ReikaTreeHelper)tree) {
				case ACACIA:
					return 12;
				case BIRCH:
					return 6;
				case DARKOAK:
					return 12;
				case JUNGLE:
					return 36;
				case OAK:
					return 18;
				case SPRUCE:
					return 48;
				default:
					return 12;
			}
		}
		if (tree instanceof ModWoodList) {
			switch((ModWoodList)tree) {
				case IRONWOOD:
					return 60;
				case SEQUOIA:
					return 200;
				case REDWOOD:
					return 130;
				case DARKWOOD:
					return 80;
				case SACRED:
					return 50;
				case CANOPY:
					return 40;
				case TWILIGHTOAK:
					return 200;
				case MANGROVE:
					return 30;
				case SAKURA:
					return 24;
				default:
					return 12;
			}
		}
		return 12;
	}

	public ItemStack getSapling() {
		if (isDyeTree)
			return new ItemStack(dyeSaplingID, 1, dyeMeta);
		if (isRainbowTree)
			return new ItemStack(rainbowSaplingID);
		return tree != null ? new ItemStack(tree.getSaplingID(), 1, tree.getSaplingMeta()) : null;
	}

	@Override
	public Coordinate getNextAndMoveOn() {
		Coordinate next = super.getNextAndMoveOn();
		if (this.isEmpty())
			;// this.reset();
		return next;
	}

	public boolean isValidTree() {
		if (tree == ModWoodList.SEQUOIA)
			return true;
		return this.getNumberLeaves() >= ReikaTreeHelper.TREE_MIN_LEAF && this.getNumberLogs() >= ReikaTreeHelper.TREE_MIN_LOG;
	}

	public TreeType getTreeType() {
		return tree;
	}

	@Override
	protected BlockArray instantiate() {
		return new TreeReader();
	}

	@Override
	public void copyTo(BlockArray cp) {
		TreeReader copy = (TreeReader)cp;

		copy.leafCount = leafCount;
		copy.logCount = logCount;

		copy.tree = tree;
		copy.dyeTree = dyeTree;

		copy.isDyeTree = isDyeTree;
		copy.isRainbowTree = isRainbowTree;
		copy.dyeMeta = dyeMeta;
	}

}
