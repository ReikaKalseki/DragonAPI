/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.DragonAPI.Base.BlockCustomLeaf;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public enum ReikaTreeHelper implements TreeType {

	OAK(Blocks.log, Blocks.leaves, Blocks.sapling, new int[]{0,4,8,12}, new int[]{0,4,8,12}, 0),
	SPRUCE(Blocks.log, Blocks.leaves, Blocks.sapling, new int[]{1,5,9,13}, new int[]{1,5,9,13}, 1),
	BIRCH(Blocks.log, Blocks.leaves, Blocks.sapling, new int[]{2,6,10,14}, new int[]{2,6,10,14}, 2),
	JUNGLE(Blocks.log, Blocks.leaves, Blocks.sapling, new int[]{3,7,11,15}, new int[]{3,7,11,15}, 3),
	ACACIA(Blocks.log2, Blocks.leaves2, Blocks.sapling, new int[]{0,4,8,12}, new int[]{0,4,8,12}, 4),
	DARKOAK(Blocks.log2, Blocks.leaves2, Blocks.sapling, new int[]{1,5,9,13}, new int[]{1,5,9,13}, 5);

	private int[] leafMeta;
	private int[] logMeta;
	private int saplingMeta;

	private Block leaf;
	private Block log;
	private Block sapling;

	public static final ReikaTreeHelper[] treeList = ReikaTreeHelper.values();

	private static final BlockMap<ReikaTreeHelper> logMappings = new BlockMap();
	private static final BlockMap<ReikaTreeHelper> leafMappings = new BlockMap();
	private static final BlockMap<ReikaTreeHelper> saplingMappings = new BlockMap();

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

	public static ReikaTreeHelper getTree(Block id, int meta) {
		return logMappings.get(id, meta);
	}

	public static ReikaTreeHelper getTree(ItemStack wood) {
		return getTree(Block.getBlockFromItem(wood.getItem()), wood.getItemDamage());
	}

	public static ReikaTreeHelper getTreeFromLeaf(Block id, int meta) {
		return leafMappings.get(id, meta);
	}

	public static ReikaTreeHelper getTreeFromLeaf(ItemStack leaf) {
		return getTreeFromLeaf(Block.getBlockFromItem(leaf.getItem()), leaf.getItemDamage());
	}

	public static ReikaTreeHelper getTreeFromSapling(Block id, int meta) {
		return saplingMappings.get(id, meta);
	}

	public static ReikaTreeHelper getTreeFromSapling(ItemStack sapling) {
		return getTreeFromSapling(Block.getBlockFromItem(sapling.getItem()), sapling.getItemDamage());
	}

	public boolean isTree(ItemStack wood) {
		return this.getTree(wood) != null;
	}

	public boolean isTree(Block id, int meta) {
		return this.getTree(id, meta) != null;
	}

	public boolean isTreeLeaf(ItemStack leaf) {
		return this.getTreeFromLeaf(leaf) != null;
	}

	public boolean isTreeLeaf(Block id, int meta) {
		return this.getTreeFromLeaf(id, meta) != null;
	}

	public boolean isTreeSapling(ItemStack sapling) {
		return this.getTreeFromSapling(sapling) != null;
	}

	public boolean isTreeSapling(Block id, int meta) {
		return this.getTreeFromSapling(id, meta) != null;
	}

	public ItemStack getLog() {
		return new ItemStack(log, 1, logMeta[0]);
	}

	public ItemStack getLeaf() {
		return new ItemStack(leaf, 1, leafMeta[0]);
	}

	public ItemStack getSapling() {
		return new ItemStack(sapling, 1, saplingMeta);
	}

	public ItemStack getDamagedLog(int dmg) {
		return new ItemStack(log, 1, logMeta[dmg]);
	}

	public ItemStack getDamagedLeaf(int dmg) {
		return new ItemStack(leaf, 1, leafMeta[dmg]);
	}

	public int getBaseLeafMeta() {
		return leafMeta[0];
	}

	public String getName() {
		return ReikaStringParser.capFirstChar(this.name());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		sb.append(" (LOG "+log+":"+Arrays.toString(logMeta)+";");
		sb.append(" ");
		sb.append("LEAF "+leaf+":"+Arrays.toString(leafMeta)+";");
		sb.append(" ");
		sb.append("SAPLING "+sapling+":"+saplingMeta);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(log, 1, logMeta[0]);
	}

	@Override
	public ItemStack getBasicLeaf() {
		return new ItemStack(leaf, 1, leafMeta[0]);
	}

	@Override
	public Block getLogID() {
		return log;
	}

	@Override
	public Block getLeafID() {
		return leaf;
	}

	@Override
	public Block getSaplingID() {
		return sapling;
	}

	public int getBaseLogMeta() {
		return logMeta[0];
	}

	@Override
	public ArrayList<Integer> getLogMetadatas() {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < logMeta.length; i++)
			li.add(logMeta[i]);
		return li;
	}

	@Override
	public ArrayList<Integer> getLeafMetadatas() {
		ArrayList<Integer> li = new ArrayList();
		for (int i = 0; i < leafMeta.length; i++)
			li.add(leafMeta[i]);
		return li;
	}

	@Override
	public boolean canBePlacedSideways() {
		return true;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getSaplingMeta() {
		return saplingMeta;
	}

	static {
		for (int i = 0; i < treeList.length; i++) {
			ReikaTreeHelper w = treeList[i];
			Block id = w.log;
			Block leaf = w.leaf;
			int[] metas = w.logMeta;
			int[] leafmetas = w.leafMeta;
			Block sapling = w.sapling;
			int saplingMeta = w.saplingMeta;
			for (int k = 0; k < metas.length; k++) {
				logMappings.put(id, metas[k], w);
			}
			for (int k = 0; k < leafmetas.length; k++) {
				leafMappings.put(leaf, leafmetas[k], w);
			}
			saplingMappings.put(sapling, saplingMeta, w);
		}
	}

	public static boolean isNaturalLeaf(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b instanceof BlockCustomLeaf)
			return ((BlockCustomLeaf)b).isNatural();
		ModWoodList mod = ModWoodList.getModWoodFromLeaf(b, meta);
		if (mod != null) {
			return mod.isNaturalLeaf(world, x, y, z);
		}
		if (b instanceof BlockLeaves)
			return (meta&4) == 0;
		return true;
	}

	@Override
	public BlockBox getTypicalMaximumSize() {
		switch(this) {
			case ACACIA:
				return BlockBox.origin().expand(9, 12, 9);
			case BIRCH:
				return BlockBox.origin().expand(5, 9, 5);
			case DARKOAK:
				return BlockBox.origin().expand(6, 11, 6);
			case JUNGLE:
				return BlockBox.origin().expand(10, 50, 10);
			case OAK:
				return BlockBox.origin().expand(15, 25, 15);
			case SPRUCE:
				return BlockBox.origin().expand(9, 40, 9);
			default:
				return BlockBox.nothing();
		}
	}

}
