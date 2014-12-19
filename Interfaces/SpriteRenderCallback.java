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

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public interface SpriteRenderCallback {

	/** Return true to prevent further rendering. */
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type);

	public boolean doPreGLTransforms(ItemStack is, ItemRenderType type);

}
