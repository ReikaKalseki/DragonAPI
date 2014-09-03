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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;


public class MultiBlockBlueprint {

	public final int xSize;
	public final int ySize;
	public final int zSize;

	protected Block[][][] IDs;
	protected int[][][] metas;

	private final List<Integer> overrides = new ArrayList();

	protected static final Random rand = new Random();

	public MultiBlockBlueprint(int x, int y, int z) {
		xSize = x;
		ySize = y;
		zSize = z;
		IDs = new Block[x][y][z];
		metas = new int[x][y][z];
		//Arrays.fill(IDs, -1);
		//Arrays.fill(metas, -1);
	}

	public MultiBlockBlueprint addBlockAt(int x, int y, int z, Block id, int meta) {
		IDs[x][y][z] = id;
		metas[x][y][z] = meta;
		return this;
	}

	public MultiBlockBlueprint addBlockAt(int x, int y, int z, Block id) {
		return this.addBlockAt(x, y, z, id, OreDictionary.WILDCARD_VALUE);
	}

	public MultiBlockBlueprint addCenteredBlockAt(int x, int y, int z, Block id, int meta) {
		return this.addBlockAt(x+xSize/2, y, z+zSize/2, id, meta);
	}

	public MultiBlockBlueprint addCenteredBlockAt(int x, int y, int z, Block id) {
		return this.addCenteredBlockAt(x, y, z, id, OreDictionary.WILDCARD_VALUE);
	}

	public boolean isMatch(World world, int x0, int y0, int z0) {
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				for (int k = 0; k < zSize; k++) {
					Block b = world.getBlock(x0+i, y0+j, z0+k);
					int meta = world.getBlockMetadata(x0+i, y0+j, z0+k);
					if (b != IDs[i][j][k])
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
					Block id = IDs[i][j][k];
					if (id != null) {
						int meta = metas[i][j][k];
						if (meta == OreDictionary.WILDCARD_VALUE)
							meta = 0;
						if (this.canPlaceBlockAt(world, x0+i, y0+j, z0+k)) {
							//ReikaJavaLibrary.pConsole("Creating "+id+":"+meta+" @ "+(x0+i)+", "+(y0+j)+", "+(z0+k));
							world.setBlock(x0+i, y0+j, z0+k, id, meta, 3);
						}
					}
				}
			}
		}
	}

	protected boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (ReikaWorldHelper.softBlocks(world, x, y, z))
			return true;
		Block b = world.getBlock(x, y, z);
		return overrides.contains(b);
	}

	public MultiBlockBlueprint addOverwriteableID(int id) {
		overrides.add(id);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				for (int k = 0; k < zSize; k++) {
					Block id = IDs[i][j][k];
					int meta = metas[i][j][k];
					sb.append("["+id+":"+meta+"]");
				}
			}
		}
		return sb.toString();
	}

	public void clear() {
		IDs = new Block[xSize][ySize][zSize];
		metas = new int[xSize][ySize][zSize];
	}
}
