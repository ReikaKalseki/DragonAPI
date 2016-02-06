package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class LightmapEvent extends Event {

	public static void fire() {
		MinecraftForge.EVENT_BUS.post(new LightmapEvent());
	}

}
