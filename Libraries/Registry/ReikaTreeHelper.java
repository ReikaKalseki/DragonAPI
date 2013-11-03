/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public enum ReikaTreeHelper {

	OAK(Block.wood, Block.leaves, Block.sapling, new int[]{0,4,8,12}, new int[]{0,4,8,12}, 0),
	SPRUCE(Block.wood, Block.leaves, Block.sapling, new int[]{1,5,9,13}, new int[]{1,5,9,13}, 1),
	BIRCH(Block.wood, Block.leaves, Block.sapling, new int[]{2,6,10,14}, new int[]{2,6,10,14}, 2),
	JUNGLE(Block.wood, Block.leaves, Block.sapling, new int[]{3,7,11,15}, new int[]{3,7,11,15}, 3);

	private int[] leafMeta;
	private int[] logMeta;
	private int saplingMeta;

	private Block leaf;
	private Block log;
	private Block sapling;

	public static final ReikaTreeHelper[] treeList = ReikaTreeHelper.values();

	public static final int TREE_MIN_LOG = 2;
	public static final int TREE_MIN_LEAF = 5;

	private ReikaTreeHelper(Block wood, Block leaves, Block tree, int[] logmeta, int[] leafmeta, int saplingmeta) {
		log = wood;
		leaf = leaves;
		sapling = tree;
		logMeta = new int[logmeta.length];
		System.arraycopy(logmeta, 0, logMeta, 0, logmeta.length);
		leafMeta = new int[leafmeta.length];
		System.arraycopy(leafmeta, 0, leafMeta, 0, leafmeta.length);
		saplingMeta = saplingmeta;
	}

	public static ReikaTreeHelper getTree(ItemStack wood) {
		for (int i = 0; i < treeList.length; i++) {
			for (int k = 0; k < treeList[i].logMeta.length; k++) {
				if (ReikaItemHelper.matchStacks(wood, treeList[i].getDamagedLog(k)))
					return treeList[i];
			}
		}
		return null;
	}

	public static ReikaTreeHelper getTree(int id, int meta) {
		return getTree(new ItemStack(id, 1, meta));
	}

	public static ReikaTreeHelper getTreeFromLeaf(int id, int meta) {
		return getTreeFromLeaf(new ItemStack(id, 1, meta));
	}

	public static ReikaTreeHelper getTreeFromLeaf(ItemStack leaf) {
		for (int i = 0; i < treeList.length; i++) {
			for (int k = 0; k < treeList[i].leafMeta.length; k++) {
				if (ReikaItemHelper.matchStacks(leaf, treeList[i].getDamagedLeaf(k)))
					return treeList[i];
			}
		}
		return null;
	}

	public boolean isTree(ItemStack wood) {
		return this.getTree(wood) != null;
	}

	public ItemStack getLog() {
		return new ItemStack(log.blockID, 1, logMeta[0]);
	}

	public ItemStack getLeaf() {
		return new ItemStack(leaf.blockID, 1, leafMeta[0]);
	}

	public ItemStack getSapling() {
		return new ItemStack(sapling.blockID, 1, saplingMeta);
	}

	public ItemStack getDamagedLog(int dmg) {
		return new ItemStack(log.blockID, 1, logMeta[dmg]);
	}

	public ItemStack getDamagedLeaf(int dmg) {
		return new ItemStack(leaf.blockID, 1, leafMeta[dmg]);
	}

}
