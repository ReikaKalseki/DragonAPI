/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;


public class MultiBlockBlueprint {

	public final int xSize;
	public final int ySize;
	public final int zSize;

	private int[][][] IDs;
	private int[][][] metas;

	public MultiBlockBlueprint(int x, int y, int z) {
		xSize = x;
		ySize = y;
		zSize = z;
		IDs = new int[x][y][z];
		metas = new int[x][y][z];
	}

	public MultiBlockBlueprint addBlockAt(int id, int meta, int x, int y, int z) {
		IDs[x][y][z] = id;
		metas[x][y][z] = meta;
		return this;
	}

	public MultiBlockBlueprint addBlockAt(int id, int x, int y, int z) {
		return this.addBlockAt(id, OreDictionary.WILDCARD_VALUE, x, y, z);
	}

	public boolean isMatch(World world, int x0, int y0, int z0) {
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				for (int k = 0; k < zSize; k++) {
					int id = world.getBlockId(x0+i, y0+j, z0+k);
					int meta = world.getBlockMetadata(x0+i, y0+j, z0+k);
					if (id != IDs[i][j][k])
						return false;
					if (meta != metas[i][j][k] && metas[i][j][k] != OreDictionary.WILDCARD_VALUE)
						return false;
				}
			}
		}
		return true;
	}

	public void createInWorld(World world, int x0, int y0, int z0) {
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				for (int k = 0; k < zSize; k++) {
					int meta = metas[i][j][k];
					if (meta == OreDictionary.WILDCARD_VALUE)
						meta = 0;
					world.setBlock(x0+i, y0+j, z0+k, IDs[i][j][k], meta, 3);
				}
			}
		}
	}
}
