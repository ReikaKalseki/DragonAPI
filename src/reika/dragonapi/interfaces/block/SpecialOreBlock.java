/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.block;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import reika.dragonapi.interfaces.registry.OreType;

public interface SpecialOreBlock {

	public OreType getOre(IBlockAccess world, int x, int y, int z);
	public ItemStack getSilkTouchVersion(World world, int x, int y, int z);
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int fortune);
	public ItemStack getReplacementBlock(World world, int x, int y, int z);
	public ItemStack getDisplayItem(World world, int x, int y, int z);

}
