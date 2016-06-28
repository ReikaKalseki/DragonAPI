/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.rendering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reika.dragonapi.auxiliary.ReikaSpriteSheets;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.interfaces.item.MultisheetItem;

public class MultiSheetItemRenderer extends ItemSpriteSheetRenderer {

	public MultiSheetItemRenderer(DragonAPIMod mod, Class root) {
		super(mod, root, null);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (item == null)
			return;
		Item cls = item.getItem();
		if (cls instanceof MultisheetItem) {
			MultisheetItem iis = (MultisheetItem)cls;
			int index = iis.getItemSpriteIndex(item);
			String sheet = iis.getSpritesheet(item);
			ReikaSpriteSheets.renderItem(modClass, sheet, index, type, item, data);
		}
	}
}
