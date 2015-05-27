/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import java.util.ArrayList;
import java.util.Iterator;

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

	public static class CropMethods {

		public static void removeOneSeed(CropType c, ArrayList<ItemStack> li) {
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

}
