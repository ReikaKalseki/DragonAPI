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

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLogic;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaRedstoneHelper extends DragonAPICore {

	/** Returns true on the postive redstone edge. Args: World, x, y, z, last power state*/
	public static boolean isPositiveEdge(World world, int x, int y, int z, boolean lastPower) {
		if (world.isBlockIndirectlyGettingPowered(x, y, z) == lastPower)
			return false;
		if (!world.isBlockIndirectlyGettingPowered(x, y, z))
			return false;
		return true;
	}

	public static boolean isPositiveEdgeOnSide(World world, int x, int y, int z, boolean lastPower, ForgeDirection side) {
		boolean sided = world.getIndirectPowerOutput(x+side.offsetX, y+side.offsetY, z+side.offsetZ, side.getOpposite().ordinal());
		boolean repeat = false;
		int id = world.getBlockId(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
		if (id != 0) {
			Block b = Block.blocksList[id];
			if (b instanceof BlockRedstoneLogic) {
				repeat = ((BlockRedstoneLogic) b).func_83011_d(world, x, y, z, side.ordinal());
			}
		}
		repeat = false;
		boolean pwr = world.isBlockIndirectlyGettingPowered(x, y, z);
		//ReikaJavaLibrary.pConsole(((sided || repeat) && pwr && !lastPower)+" for "+lastPower);
		return (sided || repeat) && pwr && !lastPower;
	}

	/** Returns true on the negative redstone edge. Args: World, x, y, z, last power state*/
	public static boolean isNegativeEdge(World world, int x, int y, int z, boolean lastPower) {
		if (world.isBlockIndirectlyGettingPowered(x, y, z) == lastPower)
			return false;
		if (world.isBlockIndirectlyGettingPowered(x, y, z))
			return false;
		return true;
	}

	/** Returns true on a redstone edge. Args: World, x, y, z, last power state */
	public static boolean isEdge(World world, int x, int y, int z, boolean lastPower) {
		if (world.isBlockIndirectlyGettingPowered(x, y, z) == lastPower)
			return false;
		return true;
	}

	/** Returns true if the redstone signal is greater or equal to a value. Args: World, x, y, z, level */
	public static boolean isRedstoneAtLevel(World world, int x, int y, int z, int level) {
		if (level > 15) {
			ReikaJavaLibrary.pConsole("Redstone level "+level+" is impossible!");
			return false;
		}
		int pwr = world.getBlockPowerInput(x, y, z);
		return (pwr >= level);
	}

	/** Returns true if the redstone signal is either zero or full (15). Args: World, x, y, z */
	public static boolean isBinaryValue(World world, int x, int y, int z) {
		int pwr = world.getBlockPowerInput(x, y, z);
		return (pwr == 0 || pwr == 15);
	}

}
