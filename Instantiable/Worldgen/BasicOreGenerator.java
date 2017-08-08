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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import Reika.DragonAPI.Interfaces.OreGenerator;
import Reika.DragonAPI.Interfaces.Registry.OreEnum;


public class BasicOreGenerator implements OreGenerator {

	public final Block replaceable;
	public final int veinSize;
	public final int veinsPerChunk;
	public final int chunksPerVein;

	public BasicOreGenerator(Block b, int size, int n, int c) {
		replaceable = b;
		veinSize = size;
		veinsPerChunk = n;
		chunksPerVein = Math.max(1, c);
	}

	@Override
	public void generateOre(OreEnum ore, Random random, World world, int chunkX, int chunkZ) {
		if (random.nextInt(chunksPerVein) == 0) {
			for (int i = 0; i < veinsPerChunk; i++) {
				Block id = ore.getBlock();
				int meta = ore.getBlockMetadata();
				//TileEntity te = ore.getTileEntity(world, x, y, z);
				int posX = chunkX+random.nextInt(16);
				int posZ = chunkZ+random.nextInt(16);
				int posY = ore.getRandomGeneratedYCoord(world, posX, posZ, random);//ore.minY+random.nextInt(ore.maxY-ore.minY+1);

				if (ore.canGenAt(world, posX, posY, posZ)) {
					if ((new WorldGenMinable(id, meta, veinSize, replaceable)).generate(world, random, posX, posY, posZ)) {

					}
				}
			}
		}
	}

}
