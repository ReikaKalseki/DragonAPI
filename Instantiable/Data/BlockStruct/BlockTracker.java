/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class BlockTracker extends BlockArray {

	public BlockTracker(World world) {
		super();
		refWorld = world;
	}

	private HashMap<ItemStack, Integer> counts = new HashMap<ItemStack, Integer>();
	private HashMap<Block, Integer> ids = new HashMap<Block, Integer>();

	@Override
	public boolean addBlockCoordinate(int x, int y, int z) {
		if (super.addBlockCoordinate(x, y, z)) {
			this.incrementCounts(x, y, z);
			return true;
		}
		return false;
	}

	private void incrementCounts(int x, int y, int z) {
		if (this.hasWorldReference()) {
			Block id = refWorld.getBlock(x, y, z);
			int meta = refWorld.getBlockMetadata(x, y, z);
			ItemStack dat = new ItemStack(id, 1, meta);
			if (counts.containsKey(dat)) {
				int count = counts.get(dat);
				counts.put(dat, count+1);
			}
			else {
				counts.put(dat, 1);
			}
			if (ids.containsKey(id)) {
				int count = ids.get(id);
				ids.put(id, count+1);
			}
			else {
				ids.put(id, 1);
			}
		}
		else {
			ReikaJavaLibrary.pConsole("You must specify a reference world for BlockTracker!");
		}
	}

	public int getNumberOf(Block id, int meta) {
		ItemStack dat = new ItemStack(id, 1, meta);
		if (counts.containsKey(dat)) {
			int count = counts.get(dat);
			return count;
		}
		else {
			return 0;
		}
	}

	public int getNumberOf(int id) {
		if (ids.containsKey(id)) {
			int count = ids.get(id);
			return count;
		}
		else {
			return 0;
		}
	}

	public int getNumberOfLiquidSources(Fluid liq) {
		if (!liq.canBePlacedInWorld()) {
			ReikaJavaLibrary.pConsole("Cannot call the count for a non-world liquid!");
			return 0;
		}
		return this.getNumberOf(liq.getBlock(), 0);
	}

}
