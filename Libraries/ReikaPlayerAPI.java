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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public final class ReikaPlayerAPI extends DragonAPICore {

	/** Transfers a player's entire inventory to an inventory. Args: Player, Inventory */
	public static void transferInventoryToChest(EntityPlayer ep, ItemStack[] inv) {
		int num = ReikaInventoryHelper.getTotalUniqueStacks(ep.inventory.mainInventory);
		if (num >= inv.length)
			return;
	}

	/** Clears a player's hotbar. Args: Player */
	public static void clearHotbar(EntityPlayer ep) {
		for (int i = 0; i < 9; i++)
			ep.inventory.mainInventory[i] = null;
	}

	/** Clears a player's inventory. Args: Player */
	public static void clearInventory(EntityPlayer ep) {
		for (int i = 0; i < ep.inventory.mainInventory.length; i++)
			ep.inventory.mainInventory[i] = null;
	}

	/** Sorts a player's inventory. Args: Player, boolean hotbar only */
	public static void cleanInventory(EntityPlayer ep, boolean hotbar) {

	}

	/** Get the block a player is looking at. Args: Player, Range, Detect 'soft' blocks yes/no */
	public static MovingObjectPosition getLookedAtBlock(EntityPlayer ep, int range, boolean hitSoft) {
		Vec3 norm = ep.getLookVec();
		World world = ep.worldObj;
		for (float i = 0; i <= range; i += 0.2) {
			int[] xyz = ReikaVectorHelper.getPlayerLookBlockCoords(ep, i);
			int id = world.getBlockId(xyz[0], xyz[1], xyz[2]);
			if (id != 0) {
				boolean isSoft = ReikaWorldHelper.softBlocks(world, xyz[0], xyz[1], xyz[2]);
				if (hitSoft || !isSoft) {
					return new MovingObjectPosition(xyz[0], xyz[1], xyz[2], 0, norm);
				}
			}
		}
		return null;
	}

	/** Gets a direction from a player's look direction. Args: Player, allow vertical yes/no */
	public static ForgeDirection getDirectionFromPlayerLook(EntityPlayer ep, boolean vertical) {
		if (MathHelper.abs(ep.rotationPitch) < 60 || !vertical) {
			int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
			while (i > 3)
				i -= 4;
			while (i < 0)
				i += 4;
			switch (i) {
			case 0:
				return ForgeDirection.SOUTH;
			case 1:
				return ForgeDirection.WEST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.EAST;
			}
		}
		else { //Looking up/down
			if (ep.rotationPitch > 0)
				return ForgeDirection.DOWN; //set to up
			else
				return ForgeDirection.UP; //set to down
		}
		return ForgeDirection.UNKNOWN;
	}

	public static boolean isAdmin(EntityPlayer ep) {
		return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).isPlayerOpped(ep.getEntityName());
	}
}
