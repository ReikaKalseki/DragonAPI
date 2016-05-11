/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public final class BlockVector {

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	public final ForgeDirection direction;

	public BlockVector(ForgeDirection dir, Coordinate c) {
		this(dir, c.xCoord, c.yCoord, c.zCoord);
	}

	public BlockVector(ForgeDirection dir, TileEntity c) {
		this(dir, c.xCoord, c.yCoord, c.zCoord);
	}

	public BlockVector(ForgeDirection dir, WorldLocation c) {
		this(dir, c.xCoord, c.yCoord, c.zCoord);
	}

	public BlockVector(ForgeDirection dir, int x, int y, int z) {
		this(x, y, z, dir);
	}

	public BlockVector(int x, int y, int z, ForgeDirection dir) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
		direction = dir;
	}

	@Override
	public String toString() {
		return xCoord+", "+yCoord+", "+zCoord+" > "+direction;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", xCoord);
		tag.setInteger("y", yCoord);
		tag.setInteger("z", zCoord);
		tag.setInteger("dir", direction.ordinal());
	}

	public static BlockVector readFromNBT(NBTTagCompound tag) {
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("dir")];
		return new BlockVector(x, y, z, dir);
	}

}
