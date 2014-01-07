/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public enum ReikaPlantHelper {

	SAPLING(Block.sapling),
	SUGARCANE(Block.reed),
	CACTUS(Block.cactus),
	MUSHROOM(Block.mushroomBrown, Block.mushroomRed),
	FLOWER(Block.plantRed, Block.plantYellow),
	TALLGRASS(Block.tallGrass),
	BUSH(Block.deadBush),
	CROP(Block.crops, Block.carrot, Block.potato, Block.melonStem, Block.pumpkinStem),
	NETHERWART(Block.netherStalk),
	LILYPAD(Block.waterlily);

	private List<Integer> ids = new ArrayList<Integer>();

	private ReikaPlantHelper(Block... blocks) {
		for (int i = 0; i < blocks.length; i++) {
			ids.add(blocks[i].blockID);
		}
	}

	private ReikaPlantHelper(Item... items) {
		for (int i = 0; i < items.length; i++) {
			ids.add(items[i].itemID);
		}
	}

	/** Can a plant be planted at yes/no. Args: World, x, y, z */
	public boolean canPlantAt(World world, int x, int y, int z) {
		int idbelow = world.getBlockId(x, y-1, z);
		int metabelow = world.getBlockMetadata(x, y-1, z);
		Material matbelow = world.getBlockMaterial(x, y-1, z);
		switch(this) {
		case CACTUS:
			return idbelow == Block.sand.blockID;
		case FLOWER:
			return ReikaWorldHelper.isDirtType(idbelow, metabelow, matbelow);
		case MUSHROOM:
			return idbelow == Block.dirt.blockID || idbelow == Block.mycelium.blockID;
		case SAPLING:/*
			if (idbelow == TwilightBlockHandler.getInstance().rootID) {
				world.setBlock(x, y, z, Block.grass.blockID);
				return true;
			}*/
			return ReikaWorldHelper.isDirtType(idbelow, metabelow, matbelow);
		case SUGARCANE:
			if (idbelow != Block.sand.blockID && !ReikaWorldHelper.isDirtType(idbelow, metabelow, matbelow))
				return false;
			ForgeDirection water = ReikaWorldHelper.checkForAdjMaterial(world, x, y-1, z, Material.water);
			return water != null && water.offsetY != 0;
		case BUSH:
			return idbelow == Block.sand.blockID;
		case CROP:
			return idbelow == Block.tilledField.blockID;
		case NETHERWART:
			return idbelow == Block.slowSand.blockID;
		case TALLGRASS:
			return ReikaWorldHelper.isDirtType(idbelow, metabelow, matbelow);
		case LILYPAD:
			return matbelow == Material.water && metabelow == 0;
		}
		return false;
	}

}
