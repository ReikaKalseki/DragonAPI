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

import net.minecraftforge.common.util.ForgeDirection;

public class BlockVector {

	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	public final ForgeDirection direction;

	public BlockVector(ForgeDirection dir, int x, int y, int z) {
		this(x, y, z, dir);
	}

	public BlockVector(int x, int y, int z, ForgeDirection dir) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
		direction = dir;
	}

}
