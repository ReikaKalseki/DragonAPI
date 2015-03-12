package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientLoginEvent extends Event {

	public final EntityPlayer player;

	public ClientLoginEvent(EntityPlayer ep) {
		player = ep;
	}

}
