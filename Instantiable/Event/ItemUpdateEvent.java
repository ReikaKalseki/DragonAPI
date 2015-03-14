/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.item.ItemEvent;

public class ItemUpdateEvent extends ItemEvent {

	public ItemUpdateEvent(EntityItem ei) {
		super(ei);
	}

}
