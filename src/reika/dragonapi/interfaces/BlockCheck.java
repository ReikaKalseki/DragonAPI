/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import reika.dragonapi.instantiable.data.immutable.BlockKey;
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
