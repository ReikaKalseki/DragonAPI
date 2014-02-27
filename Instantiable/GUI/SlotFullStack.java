/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotFullStack extends Slot {

	public SlotFullStack(IInventory ii, int id, int x, int y) {
		super(ii, id, x, y);
	}

}
