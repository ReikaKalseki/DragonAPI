/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface BlockCheck {

	public boolean matchInWorld(World world, int x, int y, int z);
	public boolean match(Block b, int meta);
	public void place(World world, int x, int y, int z);
	public ItemStack asItemStack();
}
