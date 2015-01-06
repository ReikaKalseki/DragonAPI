/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public final class CoordinateData {

	public final Block block;
	public final int meta;
	public final TileEntity tile;

	public CoordinateData(WorldLocation loc) {
		this(loc.getWorld(), loc.xCoord, loc.yCoord, loc.zCoord);
	}

	public CoordinateData(World world, int x, int y, int z) {
		this(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
	}

	public CoordinateData(World world, MovingObjectPosition hit) {
		this(world, hit.blockX, hit.blockY, hit.blockZ);
	}

	private CoordinateData(Block b, int meta, TileEntity te) {
		block = b;
		this.meta = meta;
		tile = te;
	}

	@Override
	public int hashCode() {
		return block.hashCode()+(meta << 24)+(tile != null ? tile.hashCode() : 0);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CoordinateData) {
			CoordinateData cd = (CoordinateData)o;
			return cd.block == block && cd.meta == meta && matchTiles(tile, cd.tile);
		}
		return false;
	}

	private static boolean matchTiles(TileEntity t1, TileEntity t2) {
		if (t1 == t2)
			return true;
		if (t1 == null || t2 == null)
			return false;
		return t1.equals(t2);
	}

}
