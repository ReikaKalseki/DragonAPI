/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Block;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.DragonAPI.Interfaces.Registry.OreType;

public interface SpecialOreBlock {

	public OreType getOre(IBlockAccess world, int x, int y, int z);
	public ItemStack getSilkTouchVersion(World world, int x, int y, int z);
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune);
	public ItemStack getReplacementBlock(World world, int x, int y, int z);
	public ItemStack getDisplayItem(World world, int x, int y, int z);

}
