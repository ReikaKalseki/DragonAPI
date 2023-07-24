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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface TileEnum {

	public Class<? extends TileEntity> getTEClass();
	public String getName();
	public Block getBlock();
	public int getBlockMetadata();
	public ItemStack getCraftedProduct();
	public ItemStack getCraftedProduct(TileEntity te);
	public int ordinal();

}
