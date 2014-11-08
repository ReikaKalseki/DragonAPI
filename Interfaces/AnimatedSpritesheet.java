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

import net.minecraft.item.ItemStack;

public interface AnimatedSpritesheet extends IndexedItemSprites {

	public boolean useAnimatedRender(ItemStack is);

	public int getFrameSpeed();

	public int getColumn(ItemStack is);

	public int getFrameCount();

	public int getFrameOffset(ItemStack is);

	public int getBaseRow(ItemStack is);

}
