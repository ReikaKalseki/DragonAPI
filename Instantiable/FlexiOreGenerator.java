/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FlexiOreGenerator extends WorldGenerator {

	private final Block[] replaceables;
	private final int clusterSize;
	private final int generatedMeta;
	private final Block generatedID;

	public FlexiOreGenerator(Block id, int meta, int number, Block... target)
	{
		generatedID = id;
		generatedMeta = meta;
		clusterSize = number;
		replaceables = new Block[target.length];
		System.arraycopy(target, 0, replaceables, 0, target.length);
	}

	@Override
	public boolean generate(World world, Random par2Random, int x, int y, int z)
	{
		float f = par2Random.nextFloat() * (float)Math.PI;
		double d0 = x + 8 + MathHelper.sin(f) * clusterSize / 8.0F;
		double d1 = x + 8 - MathHelper.sin(f) * clusterSize / 8.0F;
		double d2 = z + 8 + MathHelper.cos(f) * clusterSize / 8.0F;
		double d3 = z + 8 - MathHelper.cos(f) * clusterSize / 8.0F;
		double d4 = y + par2Random.nextInt(3) - 2;
		double d5 = y + par2Random.nextInt(3) - 2;

		for (int l = 0; l <= clusterSize; ++l) {
			double d6 = d0 + (d1 - d0) * l / clusterSize;
			double d7 = d4 + (d5 - d4) * l / clusterSize;
			double d8 = d2 + (d3 - d2) * l / clusterSize;
			double d9 = par2Random.nextDouble() * clusterSize / 16.0D;
			double d10 = (MathHelper.sin(l * (float)Math.PI / clusterSize) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin(l * (float)Math.PI / clusterSize) + 1.0F) * d9 + 1.0D;
			int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
			int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
			int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
			int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
			int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
			int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

			for (int k2 = i1; k2 <= l1; ++k2) {
				double d12 = (k2 + 0.5D - d6) / (d10 / 2.0D);
				if (d12 * d12 < 1.0D) {
					for (int l2 = j1; l2 <= i2; l2++) {
						double d13 = (l2 + 0.5D - d7) / (d11 / 2.0D);
						if (d12 * d12 + d13 * d13 < 1.0D) {
							for (int i3 = k1; i3 <= j2; i3++) {
								double d14 = (i3 + 0.5D - d8) / (d10 / 2.0D);

								Block block = world.getBlock(k2, l2, i3);
								for (int rr = 0; rr < replaceables.length; rr++) {
									if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && (block != null && block.isReplaceableOreGen(world, k2, l2, i3, replaceables[rr])))
										world.setBlock(k2, l2, i3, generatedID, generatedMeta, 2);
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

}
