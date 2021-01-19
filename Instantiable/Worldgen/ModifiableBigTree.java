/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;

public abstract class ModifiableBigTree extends WorldGenAbstractTree {
	/**
	 * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
	 * and 2 for 1, and 0 and 1 for 2.
	 */
	private static final byte[] otherCoordPairs = new byte[] {(byte)2, (byte)0, (byte)0, (byte)1, (byte)2, (byte)1};

	protected static final double BASE_ATTENUATION = 0.618;
	protected static final double BASE_SLOPE = 0.381;

	/** random seed for GenBigTree */
	protected final Random rand = new Random();

	protected double heightAttenuation = BASE_ATTENUATION;
	protected double branchSlope = BASE_SLOPE;
	protected double branchDensity = 1.0D;
	protected double scaleWidth = 1.0D;
	protected double leafDensity = 1.0D;

	private int heightLimit;
	private int height;

	/** Contains a list of a points at which to generate groups of leaves. */
	private int[][] leafNodes;
	private int[] basePos = new int[] {0, 0, 0};

	protected int[] globalOffset = new int[] {0, 0, 0};

	protected int trunkSize = 1;
	/** Sets the limit of the random value used to initialize the height limit. */
	protected int heightLimitLimit = 12;
	/** Sets the distance limit for how far away the generator will populate leaves from the base leaf node. */
	protected int leafDistanceLimit = 4;
	/** How far above the bottom log block the branches must stay. */
	//protected int minBranchHeight;
	/** The minimum height of the tree. */
	protected int minHeight = 5;

	protected final boolean doUpdates;

	public ModifiableBigTree(boolean updates) {
		super(updates);
		doUpdates = updates;
	}

	/**
	 * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
	 */
	private void generateLeafNodeList(World world) {
		height = (int)(heightLimit * heightAttenuation);

		if (height >= heightLimit) {
			height = heightLimit - 1;
		}

		int i = (int)((1.382D + Math.pow(leafDensity * heightLimit / 13.0D, 2.0D))*branchDensity);

		if (i < 1) {
			i = 1;
		}

		int[][] aint = new int[i * heightLimit][4];
		int ry = basePos[1] + heightLimit - leafDistanceLimit;
		int k = 1;
		int l = basePos[1] + height;
		int yh = ry - basePos[1];
		aint[0][0] = basePos[0];
		aint[0][1] = ry;
		aint[0][2] = basePos[2];
		aint[0][3] = l;
		--ry;

		while (yh >= 0) {
			int j1 = 0;
			float f = this.layerSize(yh);

			if (f < 0.0F) {
				--ry;
				--yh;
			}
			else {
				for (double d0 = 0.5D; j1 < i; ++j1) {
					double d1 = scaleWidth * f * (rand.nextFloat() + 0.328D);
					double d2 = rand.nextFloat() * 2.0D * Math.PI;
					int rx = MathHelper.floor_double(d1 * Math.sin(d2) + basePos[0] + d0);
					int rz = MathHelper.floor_double(d1 * Math.cos(d2) + basePos[2] + d0);
					int[] pos1 = new int[] {rx, ry, rz};
					int[] pos2 = new int[] {rx, ry + leafDistanceLimit, rz};

					if (this.checkBlockLine(world, pos1, pos2) == -1) {
						int[] aint3 = new int[] {basePos[0], basePos[1], basePos[2]};
						double d3 = Math.sqrt(Math.pow(Math.abs(basePos[0] - pos1[0]), 2.0D) + Math.pow(Math.abs(basePos[2] - pos1[2]), 2.0D));
						double d4 = d3 * branchSlope;

						if (pos1[1] - d4 > l) {
							aint3[1] = l;
						}
						else {
							aint3[1] = (int)(pos1[1] - d4);
						}

						if (this.checkBlockLine(world, aint3, pos1) == -1) {
							aint[k][0] = rx;
							aint[k][1] = ry;
							aint[k][2] = rz;
							aint[k][3] = aint3[1];
							++k;
						}
					}
				}

				--ry;
				--yh;
			}
		}

		leafNodes = new int[k][4];
		System.arraycopy(aint, 0, leafNodes, 0, k);
	}

	private void placeLeafNode(World world, int x, int y, int z, float size, byte p_150529_5_) {
		int l = (int)(size + 0.618D);
		byte b1 = otherCoordPairs[p_150529_5_];
		byte b2 = otherCoordPairs[p_150529_5_ + 3];
		int[] pos1 = new int[] {x, y, z};
		int[] pos2 = new int[] {0, 0, 0};
		int i1 = -l;
		int j1 = -l;

		for (pos2[p_150529_5_] = pos1[p_150529_5_]; i1 <= l; ++i1) {
			pos2[b1] = pos1[b1] + i1;
			j1 = -l;

			while (j1 <= l) {
				double d0 = Math.pow(Math.abs(i1) + 0.5D, 2.0D) + Math.pow(Math.abs(j1) + 0.5D, 2.0D);

				if (d0 > size * size) {
					++j1;
				}
				else {
					pos2[b2] = pos1[b2] + j1;
					Block block1 = world.getBlock(pos2[0], pos2[1], pos2[2]);

					if (!block1.isAir(world, pos2[0], pos2[1], pos2[2]) && !block1.isLeaves(world, pos2[0], pos2[1], pos2[2])) {
						++j1;
					}
					else {
						BlockKey leaf = this.getLeafBlock(pos2[0], pos2[1], pos2[2]);
						this.setBlockAndNotifyAdequately(world, pos2[0], pos2[1], pos2[2], leaf.blockID, leaf.metadata);
						++j1;
					}
				}
			}
		}
	}

	protected BlockKey getLogBlock(int x, int y, int z) {
		return new BlockKey(ReikaTreeHelper.OAK.getLogID(), ReikaTreeHelper.OAK.getBaseLogMeta());
	}

	protected BlockKey getLeafBlock(int x, int y, int z) {
		return new BlockKey(ReikaTreeHelper.OAK.getLeafID(), ReikaTreeHelper.OAK.getBaseLeafMeta());
	}

	/** Gets the rough size of a layer of the tree. */
	protected float layerSize(int layer) {
		if (layer < (heightLimit) * 0.3D) { //TODO is this trunk height?
			return -1.618F;
		}
		else {
			float f = heightLimit / 2.0F;
			float f1 = heightLimit / 2.0F - layer;
			float f2;

			if (f1 == 0.0F) {
				f2 = f;
			}
			else if (Math.abs(f1) >= f) {
				f2 = 0.0F;
			}
			else {
				f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2.0D) - Math.pow(Math.abs(f1), 2.0D));
			}

			f2 *= 0.5F;
			return f2;
		}
	}

	protected float leafSize(int r) {
		return r >= 0 && r < leafDistanceLimit ? (r != 0 && r != leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
	}

	/**
	 * Generates the leaves surrounding an individual entry in the leafNodes list.
	 */
	private void generateLeafNode(World world, int x, int y, int z) {
		int l = y;

		for (int i1 = y + leafDistanceLimit; l < i1; ++l) {
			float f = this.leafSize(l - y);
			this.placeLeafNode(world, x, l, z, f, (byte)1);
		}
	}

	private void generateTrunkColumn(World world, int[] root, int[] top) {
		int[] pos = new int[] {0, 0, 0};
		byte axis = 0;
		byte mainAxis;

		for (mainAxis = 0; axis < 3; ++axis) {
			pos[axis] = top[axis] - root[axis];

			if (Math.abs(pos[axis]) > Math.abs(pos[mainAxis])) {
				mainAxis = axis;
			}
		}

		if (pos[mainAxis] != 0) {
			byte b2 = otherCoordPairs[mainAxis];
			byte b3 = otherCoordPairs[mainAxis + 3];
			byte b4;

			if (pos[mainAxis] > 0) {
				b4 = 1;
			}
			else {
				b4 = -1;
			}

			double d0 = (double)pos[b2] / (double)pos[mainAxis];
			double d1 = (double)pos[b3] / (double)pos[mainAxis];
			int[] pos3 = new int[] {0, 0, 0};
			int i = 0;

			for (int j = pos[mainAxis] + b4; i != j; i += b4) {
				pos3[mainAxis] = MathHelper.floor_double(root[mainAxis] + i + 0.5D);
				pos3[b2] = MathHelper.floor_double(root[b2] + i * d0 + 0.5D);
				pos3[b3] = MathHelper.floor_double(root[b3] + i * d1 + 0.5D);
				BlockKey log = this.getLogBlock(pos3[0], pos3[1], pos3[2]);
				byte b5 = (byte)log.metadata;
				int k = Math.abs(pos3[0] - root[0]);
				int l = Math.abs(pos3[2] - root[2]);
				int i1 = Math.max(k, l);

				if (i1 > 0) {
					if (k == i1) {
						b5 += 4;
					}
					else if (l == i1) {
						b5 += 8;
					}
				}

				this.setBlockAndNotifyAdequately(world, pos3[0], pos3[1], pos3[2], log.blockID, b5);
			}
		}
	}

	/**
	 * Generates the leaf portion of the tree as specified by the leafNodes list.
	 */
	private void generateLeaves(World world) {
		int i = 0;

		for (int j = leafNodes.length; i < j; ++i) {
			int k = leafNodes[i][0];
			int l = leafNodes[i][1];
			int i1 = leafNodes[i][2];
			this.generateLeafNode(world, k, l, i1);
		}
	}

	/**
	 * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
	 */
	private boolean leafNodeNeedsBase(int d) {
		return d >= heightLimit * 0.2D;
	}

	/**
	 * Places the trunk for the big tree that is being generated. */
	private void generateTrunk(World world) {
		int x = basePos[0];
		int y0 = basePos[1];
		int y1 = basePos[1] + height;
		int z = basePos[2];
		int[] root = new int[] {x, y0, z};
		int[] top = new int[] {x, y1, z};
		this.generateTrunkColumn(world, root, top);

		switch(trunkSize) {
			case 2: {
				++root[0];
				++top[0];
				this.generateTrunkColumn(world, root, top);
				++root[2];
				++top[2];
				this.generateTrunkColumn(world, root, top);
				root[0] += -1;
				top[0] += -1;
				this.generateTrunkColumn(world, root, top);
				break;
			}
			case 3: {
				for (int i = -1; i <= 1; i++) {
					for (int k = -1; k <= 1; k++) {
						if (i != 0 || k != 0) {
							if (i == 0 || k == 0) {
								root[0] = x+i;
								root[2] = z+k;
								top[0] = x+i;
								top[2] = z+k;
								this.generateTrunkColumn(world, root, top);
							}
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
	 */
	private void generateLeafNodeBases(World world) {
		int i = 0;
		int j = leafNodes.length;

		for (int[] aint = new int[] {basePos[0], basePos[1], basePos[2]}; i < j; ++i) {
			int[] aint1 = leafNodes[i];
			int[] aint2 = new int[] {aint1[0], aint1[1], aint1[2]};
			aint[1] = aint1[3];
			int k = aint[1] - basePos[1];

			if (this.leafNodeNeedsBase(k)) {
				this.generateTrunkColumn(world, aint, aint2);
			}
		}
	}

	/**
	 * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
	 * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
	 */
	private int checkBlockLine(World world, int[] c1, int[] c2) {
		int[] aint2 = new int[] {0, 0, 0};
		byte b0 = 0;
		byte b1;

		for (b1 = 0; b0 < 3; ++b0) {
			aint2[b0] = c2[b0] - c1[b0];

			if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
				b1 = b0;
			}
		}

		if (aint2[b1] == 0) {
			return -1;
		}
		else {
			byte b2 = otherCoordPairs[b1];
			byte b3 = otherCoordPairs[b1 + 3];
			byte b4;

			if (aint2[b1] > 0) {
				b4 = 1;
			}
			else {
				b4 = -1;
			}

			double d0 = (double)aint2[b2] / (double)aint2[b1];
			double d1 = (double)aint2[b3] / (double)aint2[b1];
			int[] aint3 = new int[] {0, 0, 0};
			int i = 0;
			int j;

			for (j = aint2[b1] + b4; i != j; i += b4) {
				aint3[b1] = c1[b1] + i;
				aint3[b2] = MathHelper.floor_double(c1[b2] + i * d0);
				aint3[b3] = MathHelper.floor_double(c1[b3] + i * d1);
				Block block = world.getBlock(aint3[0], aint3[1], aint3[2]);

				if (!this.isReplaceable(world, aint3[0], aint3[1], aint3[2])) {
					break;
				}
			}

			return i == j ? -1 : Math.abs(i);
		}
	}

	/**
	 * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
	 * limit, is valid.
	 */
	private boolean validTreeLocation(World world) {
		return this.isValidLocation(world, basePos[0], basePos[1], basePos[2]);
	}

	protected boolean isValidLocation(World world, int x, int y, int z) {
		int[] aint = new int[] {x, y, z};
		int[] aint1 = new int[] {x, y + heightLimit - 1, z};
		boolean isSoil = this.isValidUnderBlock(world, x, y-1, z);
		if (!isSoil) {
			return false;
		}
		else {
			int i = this.checkBlockLine(world, aint, aint1);

			if (i == -1) {
				return true;
			}
			else if (i < 6) {
				return false;
			}
			else {
				heightLimit = i;
				return true;
			}
		}
	}

	protected boolean isValidUnderBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, (BlockSapling)Blocks.sapling);
	}

	@Override
	protected boolean func_150523_a(Block b) {
		Material m = b.getMaterial();
		return m == Material.air || m == Material.leaves || b == Blocks.grass || b == Blocks.dirt || this.isMatchingLog(b) || this.isMatchingSapling(b) || b == Blocks.vine;
	}

	protected boolean isMatchingLog(Block b) {
		return b == Blocks.log;
	}

	protected boolean isMatchingSapling(Block b) {
		return b == Blocks.leaves;
	}

	/**
	 * Rescales the generator settings, only used in WorldGenBigTree
	 */
	@Override
	public void setScale(double h, double w, double d) {
		heightLimitLimit = (int)(h * 12.0D);

		if (h > 0.5D) {
			leafDistanceLimit = 5;
		}

		scaleWidth = w;
		leafDensity = d;
	}

	@Override
	public boolean generate(World world, Random r, int x, int y, int z) {
		long l = r.nextLong();
		rand.setSeed(l);
		basePos[0] = x;
		basePos[1] = y;
		basePos[2] = z;

		if (heightLimit == 0) {
			heightLimit = minHeight + rand.nextInt(heightLimitLimit-minHeight+5);
		}

		if (!this.validTreeLocation(world)) {
			return false;
		}
		else {
			this.generateLeafNodeList(world);
			this.generateLeaves(world);
			this.generateTrunk(world);
			this.generateLeafNodeBases(world);
			return true;
		}
	}

	@Override
	protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block b, int meta) {
		world.setBlock(x+globalOffset[0], y+globalOffset[1], z+globalOffset[2], b, meta, doUpdates ? 3 : 2);
	}

	protected final void resetHeight() {
		heightLimit = 0;
	}
}
