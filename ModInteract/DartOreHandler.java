/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import net.minecraft.item.ItemStack;

public class DartOreHandler {

	public final int oreID;

	private final ItemStack oreItem;

	public DartOreHandler(int ore) {
		oreID = ore;
		oreItem = new ItemStack(oreID, 1, 0);
	}

	public ItemStack getOre() {
		return oreItem.copy();
	}

	public boolean isDartOre(ItemStack block) {
		return block.itemID == oreID;
	}

}
