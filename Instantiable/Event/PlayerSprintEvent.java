package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class PlayerSprintEvent extends PlayerEvent {

	private PlayerSprintEvent(EntityPlayer ep) {
		super(ep);
	}

	public static void fire(EntityPlayer ep) {
		MinecraftForge.EVENT_BUS.post(new PlayerSprintEvent(ep));
	}

}
