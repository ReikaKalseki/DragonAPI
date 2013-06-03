/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * 
 * Distribution of the software in any form is only allowed
 * with explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import net.minecraft.block.Block;

import Reika.DragonAPI.DragonAPICore;

public final class ReikaBlockHelper extends DragonAPICore {

	public static boolean alwaysDropsSelf(int ID) {
		int k = 0;
		//for (k = 0; k <= 20; k++)
		for (int i = 0; i < 16; i++)
			if (ID != Block.blocksList[ID].idDropped(i, rand, k) && ID-256 != Block.blocksList[ID].idDropped(i, rand, k))
				return false;/*
		for (int i = 0; i < 16; i++)
			if (Block.blocksList[ID].damageDropped(i) != i)
				return false;*/
		return true;
	}

	public static boolean neverDropsSelf(int ID) {
		boolean hasID = false;
		boolean hasMeta = false;
		for (int k = 0; k <= 20 && !hasID; k++)
			for (int i = 0; i < 16 && !hasID; i++)
				if (ID == Block.blocksList[ID].idDropped(i, rand, k) || ID-256 == Block.blocksList[ID].idDropped(i, rand, k))
					hasID = true;/*
		for (int i = 0; i < 16 && !hasMeta; i++)
			if (Block.blocksList[ID].damageDropped(i) == i)*/
		hasMeta = true;
		return (hasID && hasMeta);
	}

	/** Returns true if the Block ID corresponds to an ore block. Args: ID */
	public static boolean isOre(int id) {
		if (id == Block.oreCoal.blockID)
			return true;
		if (id == Block.oreIron.blockID)
			return true;
		if (id == Block.oreGold.blockID)
			return true;
		if (id == Block.oreRedstone.blockID)
			return true;
		if (id == Block.oreLapis.blockID)
			return true;
		if (id == Block.oreDiamond.blockID)
			return true;
		if (id == Block.oreEmerald.blockID)
			return true;
		if (id == Block.oreRedstoneGlowing.blockID)
			return true;
		if (id == Block.oreNetherQuartz.blockID)
			return true;
		return false;
	}

	public static boolean canSilkTouch(int id, int meta) {
		if (isOre(id))
			return true;
		if (id == Block.stone.blockID)
			return true;
		if (id == Block.grass.blockID)
			return true;
		if (id == Block.glass.blockID)
			return true;
		if (id == Block.glowStone.blockID)
			return true;
		if (id == Block.thinGlass.blockID)
			return true;
		if (id == Block.ice.blockID)
			return true;
		if (id == Block.leaves.blockID)
			return true;
		if (id == Block.silverfish.blockID)
			return true;
		return false;
	}

}
