/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface BlockCheck {

	public boolean matchInWorld(World world, int x, int y, int z);
	public boolean match(Block b, int meta);
	public void place(World world, int x, int y, int z);
	public ItemStack asItemStack();

	@SideOnly(Side.CLIENT)
	public ItemStack getDisplay();

	public BlockKey asBlockKey();

	public static interface TileEntityCheck extends BlockCheck {

		@SideOnly(Side.CLIENT)
		public TileEntity getTileEntity();

	}
}
