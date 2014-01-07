/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class CropHandlerBase extends ModHandlerBase {

	public abstract int getRipeMeta();

	public abstract int getFreshMeta();

	public abstract boolean isCrop(int id);

	public abstract boolean isRipeCrop(int id, int meta);

	public abstract boolean isSeedItem(ItemStack is);

	public abstract float getSecondSeedDropRate();

	public abstract ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, int id, int meta, int fortune);

}
