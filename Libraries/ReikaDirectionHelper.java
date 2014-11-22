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

import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Coordinate;

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
			if (Math.signum(dir.offsetX) == Math.signum(dx) && Math.signum(dir.offsetY) == Math.signum(dy) && Math.signum(dir.offsetZ) == Math.signum(dz))
				return dir;
		}
		//ReikaJavaLibrary.pConsole(x1+","+y1+","+z1+" > "+x2+","+y2+","+z2+" ("+dx+":"+dy+":"+dz+")");
		return null;
	}

	public static ForgeDirection getDirectionBetween(Coordinate c1, Coordinate c2) {
		return getDirectionBetween(c1.xCoord, c1.yCoord, c1.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
	}

	public static int getDirectionIndex(ForgeDirection dir) {
		switch (dir) {
		case EAST:
		case WEST:
			return 0;
		case DOWN:
		case UP:
			return 1;
		case NORTH:
		case SOUTH:
			return 2;
		default:
			return -1;
		}
	}

}
