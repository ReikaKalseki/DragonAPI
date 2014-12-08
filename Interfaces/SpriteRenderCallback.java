package Reika.DragonAPI.Interfaces;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public interface SpriteRenderCallback {

	/** Return true to prevent further rendering. OpenGL transforms are already set up for you. */
	public boolean onRender(RenderItem ri, ItemStack is, ItemRenderType type);

}
