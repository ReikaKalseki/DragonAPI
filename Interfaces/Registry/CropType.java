/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CropType extends RegistryType {

	public boolean isRipe(World world, int x, int y, int z);

	public void setHarvested(World world, int x, int y, int z);
	public void makeRipe(World world, int x, int y, int z);

	public int getGrowthState(World world, int x, int y, int z);

	public boolean isSeedItem(ItemStack is);

	public boolean destroyOnHarvest();

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune);

	public boolean isCrop(Block id, int meta);

	public boolean neverDropsSecondSeed();

	//public CropFormat getShape();

	public static class CropMethods {

		public static void removeOneSeed(CropType c, ArrayList<ItemStack> li) {
			if (c.neverDropsSecondSeed())
				return;
			Iterator<ItemStack> it = li.iterator();
			while (it.hasNext()) {
				ItemStack is = it.next();
				if (c.isSeedItem(is)) {
					if (is.stackSize > 1)
						is.stackSize--;
					else
						it.remove();
					return;
				}
			}
		}
	}
	/*
	public static enum CropFormat {

		PLANT(),
		POD(),
		BLOCK(),
		BLOCKSIDE();

		public boolean isSolid() {
			return this == BLOCK || this == BLOCKSIDE;
		}
	}*/

}
