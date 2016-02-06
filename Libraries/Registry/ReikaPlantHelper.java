/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public enum ReikaPlantHelper {

	SAPLING(Blocks.sapling),
	SUGARCANE(Blocks.reeds),
	CACTUS(Blocks.cactus),
	MUSHROOM(Blocks.brown_mushroom, Blocks.red_mushroom),
	FLOWER(Blocks.red_flower, Blocks.yellow_flower),
	TALLGRASS(Blocks.tallgrass),
	BUSH(Blocks.deadbush),
	CROP(Blocks.wheat, Blocks.carrots, Blocks.potatoes, Blocks.melon_stem, Blocks.pumpkin_stem),
	NETHERWART(Blocks.nether_wart),
	LILYPAD(Blocks.waterlily),
	VINES(Blocks.vine);

	private List<Block> ids = new ArrayList();

	public static final ReikaPlantHelper[] plantList = values();

	private static final HashMap<Block, ReikaPlantHelper> plantMappings = new HashMap();

	private ReikaPlantHelper(Block... blocks) {
		for (int i = 0; i < blocks.length; i++) {
			ids.add(blocks[i]);
		}
	}

	public static ReikaPlantHelper getPlant(Block id) {
		return plantMappings.get(id);
	}

	/** Can a plant be planted at yes/no. Args: World, x, y, z */
	public boolean canPlantAt(World world, int x, int y, int z) {
		Block idbelow = world.getBlock(x, y-1, z);
		int metabelow = world.getBlockMetadata(x, y-1, z);
		Material matbelow = ReikaWorldHelper.getMaterial(world, x, y-1, z);
		switch(this) {
			case CACTUS:
				return idbelow == Blocks.sand;
			case FLOWER:
				return ReikaBlockHelper.isDirtType(idbelow, metabelow, matbelow);
			case MUSHROOM:
				return idbelow == Blocks.dirt || idbelow == Blocks.mycelium;
			case SAPLING:/*
			if (idbelow == TwilightBlockHandler.getInstance().rootID) {
				world.setBlock(x, y, z, Blocks.grass.blockID);
				return true;
			}*/
				return ReikaBlockHelper.isDirtType(idbelow, metabelow, matbelow);
			case SUGARCANE:
				if (idbelow != Blocks.sand && !ReikaBlockHelper.isDirtType(idbelow, metabelow, matbelow))
					return false;
				ForgeDirection water = ReikaWorldHelper.checkForAdjMaterial(world, x, y-1, z, Material.water);
				return water != null && water.offsetY == 0;
			case BUSH:
				return idbelow == Blocks.sand;
			case CROP:
				return idbelow == Blocks.farmland;
			case NETHERWART:
				return idbelow == Blocks.soul_sand;
			case TALLGRASS:
				return ReikaBlockHelper.isDirtType(idbelow, metabelow, matbelow);
			case LILYPAD:
				return matbelow == Material.water && metabelow == 0;
			case VINES:
				for (int i = 1; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					if (world.getBlock(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ).isOpaqueCube())
						return true;
				}
		}
		return false;
	}

	public boolean grows() {
		switch(this) {
			case SAPLING:
			case SUGARCANE:
			case CACTUS:
			case MUSHROOM:
			case CROP:
			case NETHERWART:
			case VINES:
				return true;
			default:
				return false;
		}
	}

	static {
		for (int i = 0; i < plantList.length; i++) {
			ReikaPlantHelper w = plantList[i];
			for (int k = 0; k < w.ids.size(); k++)
				plantMappings.put(w.ids.get(k), w);
		}
	}

}
