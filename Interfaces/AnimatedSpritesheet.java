package Reika.DragonAPI.Interfaces;

import net.minecraft.item.ItemStack;

public interface AnimatedSpritesheet extends IndexedItemSprites {

	public boolean useAnimatedRender(ItemStack is);

	public int getFrameSpeed();

	public int getColumn(ItemStack is);

	public int getFrameCount();

	public int getBaseRow(ItemStack is);

}
