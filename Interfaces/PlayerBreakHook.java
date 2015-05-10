package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface PlayerBreakHook {

	/** Return false to cancel the block break. */
	public boolean breakByPlayer(EntityPlayer ep);

}
