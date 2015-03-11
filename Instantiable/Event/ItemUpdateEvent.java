package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.item.ItemEvent;

public class ItemUpdateEvent extends ItemEvent {

	public ItemUpdateEvent(EntityItem ei) {
		super(ei);
	}

}
