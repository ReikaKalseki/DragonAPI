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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.BlockPlace;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.SetBlock;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileSet;

public class OriginBlockCache {

	private final Collection<PositionedBlock> data = new HashSet();

	public final int originX;
	public final int originY;
	public final int originZ;
	private ForgeDirection primaryDirection;

	public OriginBlockCache(int x, int y, int z, ForgeDirection dir) {
		originX = x;
		originY = y;
		originZ = z;
		primaryDirection = dir;
	}

	public void flipX() {
		this.flip(0);
	}

	public void flipY() {
		this.flip(1);
	}

	public void flipZ() {
		this.flip(2);
	}

	private void flip(int i) {
		for (PositionedBlock b : data) {
			switch(i) {
				case 0:
					int dx = b.posX-originX;
					b.posX = originX-dx;
					break;
				case 1:
					int dy = b.posY-originY;
					b.posY = originY-dy;
					break;
				case 2:
					int dz = b.posZ-originZ;
					b.posZ = originZ-dz;
					break;
			}
		}
	}

	public void scale(double x, double y, double z) {
		for (PositionedBlock b : data) {
			int dx = b.posX-originX;
			int dy = b.posY-originY;
			int dz = b.posZ-originZ;
			b.posX = originX+(int)(dx*x);
			b.posY = originY+(int)(dy*y);
			b.posZ = originZ+(int)(dz*z);
		}

		Collection<PositionedBlock> c = new ArrayList(data);
		for (PositionedBlock b : c) {
			for (int i = 0; i < (int)x; i++) {
				for (int j = 0; j < (int)y; j++) {
					for (int k = 0; k < (int)z; k++) {
						data.add(new PositionedBlock(b.block, b.posX+i, b.posY+j, b.posZ+k));
					}
				}
			}
		}
	}

	public void align(ForgeDirection dir) {
		for (PositionedBlock b : data) {
			int dx = b.posX-originX;
			int dy = b.posY-originY;
			int dz = b.posZ-originZ;
			if (Math.abs(dir.offsetX) != Math.abs(primaryDirection.offsetX)) {
				int sgn = (int)Math.signum(dir.offsetX+primaryDirection.offsetX);
				b.posX = originX+dz*sgn;
				b.posZ = originZ+dx*sgn;
			}
			else if (dir.offsetX == -primaryDirection.offsetX) {
				b.posX = originX-dx;
			}
			else if (dir.offsetZ == -primaryDirection.offsetZ) {
				b.posZ = originZ-dz;
			}
		}
		primaryDirection = dir;
	}

	public void translate(int x, int y, int z) {
		for (PositionedBlock b : data) {
			b.posX += x;
			b.posY += y;
			b.posZ += z;
		}
	}

	public void setBlock(int x, int y, int z, Block b) {
		this.setBlock(x, y, z, new SetBlock(b));
	}

	public void setBlock(int x, int y, int z, Block b, int meta) {
		this.setBlock(x, y, z, new SetBlock(b, meta));
	}

	public void setBlock(int x, int y, int z, BlockPlace b) {
		data.add(new PositionedBlock(b, x, y, z));
	}

	public void setTileEntity(int x, int y, int z, Block b, int meta, TileCallback tc) {
		data.add(new PositionedBlock(new TileSet(tc, b, meta), x, y, z));
	}

	private static class PositionedBlock {

		private final BlockPlace block;
		private int posX;
		private int posY;
		private int posZ;

		private PositionedBlock(BlockPlace b, int x, int y, int z) {
			block = b;
			posX = x;
			posY = y;
			posZ = z;
		}

		@Override
		public final int hashCode() {
			return posX + (posZ << 8) + (posY << 16);
		}

		@Override
		public final boolean equals(Object o) {
			if (o instanceof PositionedBlock) {
				PositionedBlock p = (PositionedBlock)o;
				return p.posX == posX && p.posY == posY && p.posZ == posZ;
			}
			return false;
		}

	}

	public void addToGenCache(ChunkSplicedGenerationCache world) {
		for (PositionedBlock b : data) {
			world.place(b.posX, b.posY, b.posZ, b.block);
		}
	}

}
