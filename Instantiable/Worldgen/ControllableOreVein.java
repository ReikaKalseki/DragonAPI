/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
			double d6 = xvar_pos + (xvar_neg - xvar_pos) * l / veinSize;
			double d7 = ypos_1 + (ypos_2 - ypos_1) * l / veinSize;
			double d8 = zvar_pos + (zvar_neg - zvar_pos) * l / veinSize;
			double d9 = rand.nextDouble() * veinSize / 16D;
			double d10 = (MathHelper.sin(l * (float)Math.PI / veinSize) + 1F) * d9 + 1D;
			double d11 = (MathHelper.sin(l * (float)Math.PI / veinSize) + 1F) * d9 + 1D;

			int i1 = MathHelper.floor_double(d6 - d10 / 2D);
			int j1 = MathHelper.floor_double(d7 - d11 / 2D);
			int k1 = MathHelper.floor_double(d8 - d10 / 2D);
			int l1 = MathHelper.floor_double(d6 + d10 / 2D);
			int i2 = MathHelper.floor_double(d7 + d11 / 2D);
			int j2 = MathHelper.floor_double(d8 + d10 / 2D);

			for (int dx = i1; dx <= l1; dx++ ) {
				double rx = (dx + 0.5D - d6) / (d10 / 2D);

				if (rx * rx < 1D) {
					for (int dy = j1; dy <= i2; dy++ ) {
						double ry = (dy + 0.5D - d7) / (d11 / 2D);

						if (rx * rx + ry * ry < 1D) {
							for (int dz = k1; dz <= j2; dz++ ) {
								double rz = (dz + 0.5D - d8) / (d10 / 2D);

								if (rx * rx + ry * ry + rz * rz < 1D) {
									if (world.getBlock(dx, dy, dz).isReplaceableOreGen(world, dx, dy, dz, target)) {
										if (this.canPlaceBlockHere(world, dx, dy, dz)) {
											world.setBlock(dx, dy, dz, block.blockID, block.metadata, 2);
										}
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

}
