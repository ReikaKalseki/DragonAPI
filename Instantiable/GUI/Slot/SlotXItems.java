/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI.Slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotXItems extends Slot {

	public final int slotCapacity;

	public SlotXItems(IInventory ii, int par2, int par3, int par4, int size) {
		super(ii, par2, par3, par4);
		slotCapacity = size;
	}

	@Override
	public int getSlotStackLimit()
	{
		return slotCapacity;
	}

}
