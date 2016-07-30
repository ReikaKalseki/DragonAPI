package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class ItemStackUpdateEvent extends Event {

	public final ItemStack item;
	public final Entity holder;
	public final int slot;
	public final boolean held;

	public ItemStackUpdateEvent(ItemStack is, Entity e, int s, boolean h) {
		item = is;
		held = h;
		holder = e;
		slot = s;
	}

	public static void fire(ItemStack is, Entity e, int s, boolean h) {
		MinecraftForge.EVENT_BUS.post(new ItemStackUpdateEvent(is, e, s, h));
	}

}
