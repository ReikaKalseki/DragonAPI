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

import net.minecraft.item.ItemStack;

public interface IndexedItemSprites {

	public int getItemSpriteIndex(ItemStack is);

	public String getTexture(ItemStack is);

	public Class getTextureReferenceClass();

}
