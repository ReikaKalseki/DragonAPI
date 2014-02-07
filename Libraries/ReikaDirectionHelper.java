/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;

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

}
