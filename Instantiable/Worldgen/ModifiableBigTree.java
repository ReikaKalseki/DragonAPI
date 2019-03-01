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
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;

public class ModifiableBigTree extends WorldGenAbstractTree {
	/**
	 * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
	 * and 2 for 1, and 0 and 1 for 2.
	 */
	private static final byte[] otherCoordPairs = new byte[] {(byte)2, (byte)0, (byte)0, (byte)1, (byte)2, (byte)1};
	/** random seed for GenBigTree */
	protected final Random rand = new Random();
	/** Reference to the World object. */
	private int[] basePos = new int[] {0, 0, 0};
	private int heightLimit;
	private int height;
	private double heightAttenuation = 0.618D;
	private double branchDensity = 1.0D;
	private double branchSlope = 0.381D;
	private double scaleWidth = 1.0D;
	private double leafDensity = 1.0D;
	/** Currently always 1, can be set to 2 in the class constructor to generate a double-sized tree trunk for big trees. */
	private int trunkSize = 1;
	/** Sets the limit of the random value used to initialize the height limit. */
	private int heightLimitLimit = 12;
	/** Sets the distance limit for how far away the generator will populate leaves from the base leaf node. */
	private int leafDistanceLimit = 4;
	/** Contains a list of a points at which to generate groups of leaves. */
	private int[][] leafNodes;

	public ModifiableBigTree(boolean updates) {
		super(updates);
	}

	/**
	 * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
	 */
	private void generateLeafNodeList(World world) {
		height = (int)(heightLimit * heightAttenuation);

		if (height >= heightLimit) {
			height = heightLimit - 1;
		}

		int i = (int)(1.382D + Math.pow(leafDensity * heightLimit / 13.0D, 2.0D));

		if (i < 1) {
			i = 1;
		}

		int[][] aint = new int[i * heightLimit][4];
		int j = basePos[1] + heightLimit - leafDistanceLimit;
		int k = 1;
		int l = basePos[1] + height;
		int i1 = j - basePos[1];
		aint[0][0] = basePos[0];
		aint[0][1] = j;
		aint[0][2] = basePos[2];
		aint[0][3] = l;
		--j;

		while (i1 >= 0) {
			int j1 = 0;
			float f = this.layerSize(i1);

			if (f < 0.0F) {
				--j;
				--i1;
			}
			else {
				for (double d0 = 0.5D; j1 < i; ++j1) {
					double d1 = scaleWidth * f * (rand.nextFloat() + 0.328D);
					double d2 = rand.nextFloat() * 2.0D * Math.PI;
					int k1 = MathHelper.floor_double(d1 * Math.sin(d2) + basePos[0] + d0);
					int l1 = MathHelper.floor_double(d1 * Math.cos(d2) + basePos[2] + d0);
					int[] aint1 = new int[] {k1, j, l1};
					int[] aint2 = new int[] {k1, j + leafDistanceLimit, l1};

					if (this.checkBlockLine(world, aint1, aint2) == -1) {
						int[] aint3 = new int[] {basePos[0], basePos[1], basePos[2]};
						double d3 = Math.sqrt(Math.pow(Math.abs(basePos[0] - aint1[0]), 2.0D) + Math.pow(Math.abs(basePos[2] - aint1[2]), 2.0D));
						double d4 = d3 * branchSlope;

						if (aint1[1] - d4 > l) {
							aint3[1] = l;
						}
						else {
							aint3[1] = (int)(aint1[1] - d4);
						}

						if (this.checkBlockLine(world, aint3, aint1) == -1) {
							aint[k][0] = k1;
							aint[k][1] = j;
							aint[k][2] = l1;
							aint[k][3] = aint3[1];
							++k;
						}
					}
				}

				--j;
				--i1;
			}
		}

		leafNodes = new int[k][4];
		System.arraycopy(aint, 0, leafNodes, 0, k);
	}

	private void func_150529_a(World world, int x, int y, int z, float p_150529_4_, byte p_150529_5_) {
		int l = (int)(p_150529_4_ + 0.618D);
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

				if (d0 > p_150529_4_ * p_150529_4_) {
					++j1;
				}
				else {
					pos2[b2] = pos1[b2] + j1;
					Block block1 = world.getBlock(pos2[0], pos2[1], pos2[2]);

					if (!block1.isAir(world, pos2[0], pos2[1], pos2[2]) && !block1.isLeaves(world, pos2[0], pos2[1], pos2[2])) {
						++j1;
					}
					else {
						this.setBlockAndNotifyAdequately(world, pos2[0], pos2[1], pos2[2], this.getLeafBlock(pos2[0], pos2[1], pos2[2]), this.getLeafMetadata(pos2[0], pos2[1], pos2[2]));
						++j1;
					}
				}
			}
		}
	}

	public Block getLogBlock(int x, int y, int z) {
		return ReikaTreeHelper.OAK.getLogID();
	}

	public int getLogMetadata(int x, int y, int z) {
		return ReikaTreeHelper.OAK.getBaseLogMeta();
	}

	public Block getLeafBlock(int x, int y, int z) {
		return ReikaTreeHelper.OAK.getLeafID();
	}

	public int getLeafMetadata(int x, int y, int z) {
		return ReikaTreeHelper.OAK.getBaseLeafMeta();
	}

	/**
	 * Gets the rough size of a layer of the tree.
	 */
	private float layerSize(int layer) {
		if (layer < (heightLimit) * 0.3D) {
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

	private float leafSize(int p_76495_1_) {
		return p_76495_1_ >= 0 && p_76495_1_ < leafDistanceLimit ? (p_76495_1_ != 0 && p_76495_1_ != leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
	}

	/**
	 * Generates the leaves surrounding an individual entry in the leafNodes list.
	 */
	private void generateLeafNode(World world, int x, int y, int z) {
		int l = y;

		for (int i1 = y + leafDistanceLimit; l < i1; ++l) {
			float f = this.leafSize(l - y);
			this.func_150529_a(world, x, l, z, f, (byte)1);
		}
	}

	private void func_150530_a(World world, int[] pos1, int[] pos2) {
		int[] aint2 = new int[] {0, 0, 0};
		byte b0 = 0;
		byte b1;

		for (b1 = 0; b0 < 3; ++b0) {
			aint2[b0] = pos2[b0] - pos1[b0];

			if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
				b1 = b0;
			}
		}

		if (aint2[b1] != 0) {
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
			int[] pos3 = new int[] {0, 0, 0};
			int i = 0;

			for (int j = aint2[b1] + b4; i != j; i += b4) {
				pos3[b1] = MathHelper.floor_double(pos1[b1] + i + 0.5D);
				pos3[b2] = MathHelper.floor_double(pos1[b2] + i * d0 + 0.5D);
				pos3[b3] = MathHelper.floor_double(pos1[b3] + i * d1 + 0.5D);
				byte b5 = (byte)this.getLogMetadata(pos3[0], pos3[1], pos3[2]);
				int k = Math.abs(pos3[0] - pos1[0]);
				int l = Math.abs(pos3[2] - pos1[2]);
				int i1 = Math.max(k, l);

				if (i1 > 0) {
					if (k == i1) {
						b5 += 4;
					}
					else if (l == i1) {
						b5 += 8;
					}
				}

				this.setBlockAndNotifyAdequately(world, pos3[0], pos3[1], pos3[2], this.getLogBlock(pos3[0], pos3[1], pos3[2]), b5);
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
	 * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
	 * field that is always 1 to 2.
	 */
	private void generateTrunk(World world) {
		int i = basePos[0];
		int j = basePos[1];
		int k = basePos[1] + height;
		int l = basePos[2];
		int[] aint = new int[] {i, j, l};
		int[] aint1 = new int[] {i, k, l};
		this.func_150530_a(world, aint, aint1);

		if (trunkSize == 2) {
			++aint[0];
			++aint1[0];
			this.func_150530_a(world, aint, aint1);
			++aint[2];
			++aint1[2];
			this.func_150530_a(world, aint, aint1);
			aint[0] += -1;
			aint1[0] += -1;
			this.func_150530_a(world, aint, aint1);
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
				this.func_150530_a(world, aint, aint2);
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
		int[] aint = new int[] {basePos[0], basePos[1], basePos[2]};
		int[] aint1 = new int[] {basePos[0], basePos[1] + heightLimit - 1, basePos[2]};
		Block block = world.getBlock(basePos[0], basePos[1] - 1, basePos[2]);

		boolean isSoil = block.canSustainPlant(world, basePos[0], basePos[1] - 1, basePos[2], ForgeDirection.UP, (BlockSapling)Blocks.sapling);
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
			heightLimit = 5 + rand.nextInt(heightLimitLimit);
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
}
