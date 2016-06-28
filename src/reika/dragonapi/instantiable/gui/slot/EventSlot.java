/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import reika.dragonapi.instantiable.event.SlotEvent;
import reika.dragonapi.instantiable.event.SlotEvent.AddToSlotEvent;
import reika.dragonapi.instantiable.event.SlotEvent.RemoveFromSlotEvent;

public class EventSlot extends Slot {

	public EventSlot(IInventory ii, int id, int x, int y) {
		super(ii, id, x, y);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer ep, ItemStack is)
	{
		if (!MinecraftForge.EVENT_BUS.post(new RemoveFromSlotEvent(slotNumber, inventory, is, ep)))
			super.onPickupFromSlot(ep, is);
	}

	@Override
	public void putStack(ItemStack is)
	{
		if (!MinecraftForge.EVENT_BUS.post(new AddToSlotEvent(slotNumber, inventory, is)))
			super.putStack(is);
	}

	@Override
	public void onSlotChanged()
	{
		super.onSlotChanged();
		MinecraftForge.EVENT_BUS.post(new SlotEvent(slotNumber, inventory));
	}


}
