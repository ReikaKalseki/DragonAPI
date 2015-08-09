/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;

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

	public static ForgeDirection getDirectionBetween(Point from, Point to) {
		return getDirectionBetween(from.x, 0, from.y, to.x, 0, to.y);
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

	/** Returns the two positive direction vectors perpendicular to the supplied direction. */
	public static ArrayList<ForgeDirection> getPerpendicularDirections(ForgeDirection dir) {
		ArrayList<ForgeDirection> dirs = new ArrayList();
		for (int i = 0; i < 6; i++) {
			ForgeDirection d = ForgeDirection.VALID_DIRECTIONS[i];
			if (d != dir && d != dir.getOpposite())
				dirs.add(d);
		}
		dirs.remove(ForgeDirection.WEST);
		dirs.remove(ForgeDirection.NORTH);
		dirs.remove(ForgeDirection.DOWN);
		return dirs;
	}

	public static ForgeDirection getRandomDirection(boolean vertical, Random rand) {
		int idx = vertical ? rand.nextInt(6) : 2+rand.nextInt(4);
		return ForgeDirection.VALID_DIRECTIONS[idx];
	}

	public static EnumFacing getOpposite(EnumFacing facing) {
		int val = facing.ordinal()%2 != 0 ? facing.ordinal()-1 : facing.ordinal()+1;
		return EnumFacing.values()[val];
	}

	public static ForgeDirection getSideOfBox(int i, int j, int k, boolean vertical, int size) {
		if (i == size)
			return ForgeDirection.EAST;
		else if (i == 0)
			return ForgeDirection.WEST;
		else if (k == size)
			return ForgeDirection.SOUTH;
		else if (k == 0)
			return ForgeDirection.NORTH;
		else if (vertical && j == size)
			return ForgeDirection.UP;
		else if (vertical && j == 0)
			return ForgeDirection.DOWN;
		else
			return null;
	}

	public static int getRelativeAngle(ForgeDirection from, ForgeDirection to) {
		int rel = getHeading(to)-getHeading(from);
		return (360+rel%360)%360;
	}

	public static int getHeading(ForgeDirection dir) {
		switch(dir) {
			case NORTH:
				return 0;
			case EAST:
				return 90;
			case SOUTH:
				return 180;
			case WEST:
				return 270;
			default:
				return 0;
		}
	}

	public static double getCompassHeading(double dx, double dz) {
		double phi = ReikaPhysicsHelper.cartesianToPolar(dx, 0, -dz)[2];
		phi += 90; //since phi=0 is EAST
		return (phi%360D+360D)%360D;
	}

}
