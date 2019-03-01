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
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public abstract class ControllableOreVein extends WorldGenerator {

	private final BlockKey block;

	private final int veinSize;

	public Block target = Blocks.stone;

	public ControllableOreVein(Block block, int size) {
		this(new BlockKey(block), size);
	}

	public ControllableOreVein(Block block, int meta, int size) {
		this(new BlockKey(block, meta), size);
	}

	public ControllableOreVein(BlockKey b, int size) {
		block = b;
		veinSize = size;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		float f = rand.nextFloat() * (float)Math.PI;
		double xvar_pos = x + 8 + MathHelper.sin(f) * veinSize / 8F;
		double xvar_neg = x + 8 - MathHelper.sin(f) * veinSize / 8F;
		double zvar_pos = z + 8 + MathHelper.cos(f) * veinSize / 8F;
		double zvar_neg = z + 8 - MathHelper.cos(f) * veinSize / 8F;
		double ypos_1 = y + rand.nextInt(3) - 2;
		double ypos_2 = y + rand.nextInt(3) - 2;

		for (int l = 0; l <= veinSize; l++ ) {
			double posx = xvar_pos + (xvar_neg - xvar_pos) * l / veinSize;
			double posy = ypos_1 + (ypos_2 - ypos_1) * l / veinSize;
			double posz = zvar_pos + (zvar_neg - zvar_pos) * l / veinSize;
			double range = rand.nextDouble() * veinSize / 16D;
			double range_horiz = (MathHelper.sin(l * (float)Math.PI / veinSize) + 1F) * range + 1D;
			double range_vert = (MathHelper.sin(l * (float)Math.PI / veinSize) + 1F) * range + 1D;

			int minx = MathHelper.floor_double(posx - range_horiz / 2D);
			int miny = MathHelper.floor_double(posy - range_vert / 2D);
			int minz = MathHelper.floor_double(posz - range_horiz / 2D);
			int maxx = MathHelper.floor_double(posx + range_horiz / 2D);
			int maxy = MathHelper.floor_double(posy + range_vert / 2D);
			int maxz = MathHelper.floor_double(posz + range_horiz / 2D);

			for (int dx = minx; dx <= maxx; dx++ ) {
				double rx = (dx + 0.5D - posx) / (range_horiz / 2D);

				if (rx * rx < 1D) {
					for (int dy = miny; dy <= maxy; dy++ ) {
						double ry = (dy + 0.5D - posy) / (range_vert / 2D);

						if (rx * rx + ry * ry < 1D) {
							for (int dz = minz; dz <= maxz; dz++ ) {
								double rz = (dz + 0.5D - posz) / (range_horiz / 2D);

								if (rx * rx + ry * ry + rz * rz < 1D) {
									if (world.getBlock(dx, dy, dz).isReplaceableOreGen(world, dx, dy, dz, target)) {
										//if (!world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
										if (this.canPlaceBlockHere(world, dx, dy, dz)) {
											world.setBlock(dx, dy, dz, block.blockID, block.metadata, 2);
										}/*
										}
										else {

										}*/
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	public abstract boolean canPlaceBlockHere(World world, int x, int y, int z);

	public static class ExposedOreVein extends ControllableOreVein {

		public ExposedOreVein(Block block, int size) {
			super(block, size);
		}

		public ExposedOreVein(Block block, int meta, int size) {
			super(block, meta, size);
		}

		public ExposedOreVein(BlockKey block, int size) {
			super(block, size);
		}

		@Override
		public boolean canPlaceBlockHere(World world, int x, int y, int z) {
			return ReikaWorldHelper.checkForAdjMaterial(world, x, y, z, Material.air) != null;
		}

	}

	public static final class BlockAdjacentOreVein extends ControllableOreVein {

		private final BlockKey check;

		public BlockAdjacentOreVein(Block block, int size, BlockKey ch) {
			super(block, size);
			check = ch;
		}

		public BlockAdjacentOreVein(Block block, int meta, int size, BlockKey ch) {
			super(block, meta, size);
			check = ch;
		}

		public BlockAdjacentOreVein(BlockKey block, int size, BlockKey ch) {
			super(block, size);
			check = ch;
		}

		@Override
		public boolean canPlaceBlockHere(World world, int x, int y, int z) {
			return ReikaWorldHelper.checkForAdjBlock(world, x, y, z, check.blockID, check.metadata) != null;
		}

	}

	public static final class BlockExcludingOreVein extends ControllableOreVein {

		private final BlockCheck check;

		public BlockExcludingOreVein(Block block, int size, BlockCheck ch) {
			super(block, size);
			check = ch;
		}

		public BlockExcludingOreVein(Block block, int meta, int size, BlockCheck ch) {
			super(block, meta, size);
			check = ch;
		}

		public BlockExcludingOreVein(BlockKey block, int size, BlockCheck ch) {
			super(block, size);
			check = ch;
		}

		@Override
		public boolean canPlaceBlockHere(World world, int x, int y, int z) {
			return !check.matchInWorld(world, x, y, z);
		}

	}

}
