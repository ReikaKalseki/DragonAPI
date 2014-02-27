/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

public class TreasureChest {

	public final World refWorld;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;
	private boolean trapped;

	private ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();

	public TreasureChest(World world, int x, int y, int z) {
		refWorld = world;
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public TreasureChest setTrapped() {
		trapped = true;
		return this;
	}

	public TreasureChest addItem(ItemStack item) {
		itemList.add(item);
		return this;
	}

	public TreasureChest addItems(List<ItemStack> items) {
		itemList.addAll(items);
		return this;
	}

	public boolean isTrapped() {
		return trapped;
	}

	public int getChestID() {
		return this.isTrapped() ? Block.chestTrapped.blockID : Block.chest.blockID;
	}

	public void generate() {
		refWorld.setBlock(xCoord, yCoord, zCoord, this.getChestID());
		TileEntityChest te = (TileEntityChest)refWorld.getBlockTileEntity(xCoord, yCoord, zCoord);
		for (int i = 0; i < itemList.size(); i++)
			ReikaInventoryHelper.addToIInv(itemList.get(i), te);
	}

}
