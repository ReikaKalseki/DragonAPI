package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


public class FogDistanceEvent extends Event {

	public float fogDistance;
	public final float originalDistance;

	public FogDistanceEvent(float f) {
		fogDistance = originalDistance = f;
	}

	public static float fire(float f1) {
		FogDistanceEvent evt = new FogDistanceEvent(f1);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.fogDistance;
	}

}
