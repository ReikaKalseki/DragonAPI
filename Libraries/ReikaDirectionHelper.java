/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import Reika.DragonAPI.DragonAPICore;

import net.minecraftforge.common.util.ForgeDirection;

public class ReikaDirectionHelper extends DragonAPICore {

	public static ForgeDirection getLeftBy90(ForgeDirection dir) {
		switch(dir) {
		case EAST:
			return ForgeDirection.NORTH;
		case NORTH:
			return ForgeDirection.WEST;
		case SOUTH:
			return ForgeDirection.EAST;
		case WEST:
			return ForgeDirection.SOUTH;
		default:
			return dir;
		}
	}

	public static ForgeDirection getRightBy90(ForgeDirection dir) {
		switch(dir) {
		case EAST:
			return ForgeDirection.SOUTH;
		case NORTH:
			return ForgeDirection.EAST;
		case SOUTH:
			return ForgeDirection.WEST;
		case WEST:
			return ForgeDirection.NORTH;
		default:
			return dir;
		}
	}

	public static ForgeDirection getDirectionBetween(int x1, int y1, int z1, int x2, int y2, int z2) {
		int dx = x2-x1;
		int dy = y2-y1;
		int dz = z2-z1;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			if (dir.offsetX == dx && dir.offsetY == dy && dir.offsetZ == dz)
				return dir;
		}
		return null;
	}

}
