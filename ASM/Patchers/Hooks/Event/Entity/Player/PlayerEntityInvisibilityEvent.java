package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity.Player;

import Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity.EntityInvisibilityEvent;

public class PlayerEntityInvisibilityEvent extends EntityInvisibilityEvent {

	public PlayerEntityInvisibilityEvent() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

}
