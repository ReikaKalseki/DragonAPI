/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.Auxiliary.ReikaSpriteSheets;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Interfaces.MultisheetItem;

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
