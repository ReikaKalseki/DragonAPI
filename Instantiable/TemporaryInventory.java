/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.item.ItemStack;

public class TemporaryInventory extends BasicInventory {

	public TemporaryInventory(int size) {
		super("temp", size);
	}

	public TemporaryInventory(int size, int stack) {
		super("temp", size, stack);
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack is) {
		return true;
	}

}
