/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.GUI.Slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Event.SlotEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.AddToSlotEvent;
import Reika.DragonAPI.Instantiable.Event.SlotEvent.RemoveFromSlotEvent;

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
