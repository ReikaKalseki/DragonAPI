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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class MultiBlockBlueprint {

	/** Do not overwrite a block if this is the ID at this position */
	private static final int NULL_ID = -1;
	public final int xSize;
	public final int ySize;
	public final int zSize;

	private int[][][] IDs;
	private int[][][] metas;

	private List<Integer> overrides = new ArrayList();

	public MultiBlockBlueprint(int x, int y, int z) {
		xSize = x;
		ySize = y;
		zSize = z;
		IDs = new int[x][y][z];
		metas = new int[x][y][z];
		//Arrays.fill(IDs, -1);
		//Arrays.fill(metas, -1);
	}

	public MultiBlockBlueprint addBlockAt(int x, int y, int z, int id, int meta) {
		IDs[x][y][z] = id;
		metas[x][y][z] = meta;
		return this;
	}

	public MultiBlockBlueprint addBlockAt(int x, int y, int z, int id) {
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
					int id = IDs[i][j][k];
					if (id != NULL_ID) {
						int meta = metas[i][j][k];
						if (meta == OreDictionary.WILDCARD_VALUE)
							meta = 0;
						if (this.canPlaceBlockAt(world, x0+i, y0+j, z0+k))
							world.setBlock(x0+i, y0+j, z0+k, id, meta, 3);
					}
				}
			}
		}
	}

	private boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return true;
		int id = world.getBlockId(x, y, z);
		return overrides.contains(id);
	}

	public void addOverwriteableID(int id) {
		overrides.add(id);
	}
}
