package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class RemovePlayerItemEvent extends PlayerEvent {

	private final ItemStack item;

	public RemovePlayerItemEvent(EntityPlayer ep, ItemStack is) {
		super(ep);

		item = is;
	}

	public ItemStack getItem() {
		return item.copy();
	}

}
